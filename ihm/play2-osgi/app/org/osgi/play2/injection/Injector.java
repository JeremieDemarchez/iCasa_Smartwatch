/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.osgi.play2.injection;

import play.Logger;
import play.Play;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Set;

public class Injector {

    /**
     * For now, inject beans in controllers
     */
    public static void inject(BeanSource source) {
        Set<String> classesStrs = Play.application().getTypesAnnotatedWith("controllers", InjectOSGiServices.class);
        ClassLoader cl = Play.application().classloader();
        for(String classStr : classesStrs) {
            Class<?> clazz = null;
            try {
                clazz = cl.loadClass(classStr);
            } catch (ClassNotFoundException e) {
                Logger.warn("OSGi injection : Cannot load " + classStr + " class.");
                continue;
            }

            for(Method method : clazz.getDeclaredMethods()) {
                if(Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(OSGiService.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    // TODO : use TypeLiteral when generic params
                    Type[] genericParameterTypes = method.getGenericParameterTypes();
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    Object[] parameters = new Object[parameterTypes.length];
                    for (int j = 0; j < parameterTypes.length; j++) {
                        Object value = null;
                        if (parameterTypes[j].equals(Iterable.class)) {
                            value = source.getBeanCollectionOfType(getGenericType(genericParameterTypes[j]),
                                    clazz, method, parameterAnnotations[j]);
                        } else {
                            value = source.getBeanOfType(parameterTypes[j],
                                    clazz, method, parameterAnnotations[j]);
                        }
                        parameters[j] = value;
                    }
                    boolean accessible = method.isAccessible();
                    // set a private method as public method to invoke it
                    if (!accessible) {
                        method.setAccessible(true);
                    }
                    // invocation of the method with rights parameters
                    try {
                        method.invoke(null, parameters);
                    } catch(RuntimeException e) {
                        throw e;
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        // if method was private, then put it private back
                        if (!accessible) {
                            method.setAccessible(accessible);
                        }
                    }
                }
            }
            for(Field field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(OSGiService.class)) {
                    Class<?> type = field.getType();
                    Type genericType = field.getGenericType();
                    if (type.equals(Iterable.class)) {
                        Object value = null;
                        field.setAccessible(true);
                        try {
                            value = source.getBeanCollectionOfType(getGenericType(genericType), clazz, field, field.getAnnotations());
                            field.set(null, value);
                        } catch(RuntimeException e) {
                            throw e;
                        } catch(Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Object value = null;
                        field.setAccessible(true);
                        try {
                            value = source.getBeanOfType(type, clazz, field, field.getAnnotations());
                            field.set(null, value);
                        } catch(RuntimeException e) {
                            throw e;
                        } catch(Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private static Class<?> getGenericType(Type t) {
        return (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
    }
}