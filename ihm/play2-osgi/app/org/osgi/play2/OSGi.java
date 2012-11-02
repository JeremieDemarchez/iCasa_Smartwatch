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
package org.osgi.play2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

public class OSGi {

    static Framework osgiFramework;

    public static BundleContext bundleContext() {
        return osgiFramework.getBundleContext();
    }

    public static <T> T service(final Class<T> service) {
        return (T) Proxy.newProxyInstance(OSGi.class.getClassLoader(),
                new Class[] { service }, new InvocationHandler() {

            public Object invoke(Object o, Method method, Object[] os) throws Throwable {
                ServiceReference ref = bundleContext().getServiceReference(service.getName());
                if (ref == null) {
                    throw new RuntimeException("Unable to find service for " + service.getName());
                }
                Object ret = null;
                Object instance = bundleContext().getService(ref);
                try {
                    ret = method.invoke(instance, os);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                } finally {
                    bundleContext().ungetService(ref);
                }
                return ret;
            }
        });
    }

    public static <T> Iterable<T> services(final Class<T> service, final String filter) {
        return new Iterable<T>() {

            public Iterator<T> iterator() {
                try {
                    ServiceReference[] refs = bundleContext().getServiceReferences(service.getName(), filter);
                    if (refs == null) {
                        throw new RuntimeException("Unable to find services for " + service.getName());
                    }
                    List<T> services = new ArrayList<T>();
                    for (ServiceReference s : refs) {
                        services.add((T) Proxy.newProxyInstance(OSGi.class.getClassLoader(),
                                new Class[]{service}, new InvocationHandler() {

                            public Object invoke(Object o, Method method, Object[] os) throws Throwable {
                                ServiceReference ref = bundleContext().getServiceReference(service.getName());
                                Object ret = null;
                                Object instance = bundleContext().getService(ref);
                                try {
                                    boolean acc = method.isAccessible();
                                    method.setAccessible(true);
                                    ret = method.invoke(instance, os);
                                    method.setAccessible(acc);
                                } finally {
                                    bundleContext().ungetService(ref);
                                }
                                return ret;
                            }
                        }));
                    }
                    return services.iterator();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}