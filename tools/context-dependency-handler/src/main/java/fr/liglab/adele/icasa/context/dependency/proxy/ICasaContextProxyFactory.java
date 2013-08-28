/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.context.dependency.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.liglab.adele.icasa.context.dependency.ContextDependency;
import fr.liglab.adele.icasa.context.dependency.ContextDependencyHandler;

public class ICasaContextProxyFactory implements InvocationHandler {

    /**
	 * 
	 */
    private final ContextDependency m_dependency;

    /**
     * HashCode method.
     */
    private Method m_hashCodeMethod;

    /**
     * Equals method.
     */
    private Method m_equalsMethod;

    /**
     * toStirng method.
     */
    private Method m_toStringMethod;

    /**
     * Creates a DynamicProxyFactory.
     * 
     * @param dependency TODO
     */
    public ICasaContextProxyFactory(ContextDependency dependency) {
        m_dependency = dependency;
        try {
            m_hashCodeMethod = Object.class.getMethod("hashCode", null);
            m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
            m_toStringMethod = Object.class.getMethod("toString", null);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    /**
     * Creates a proxy object for the given specification. The proxy uses the given dependency to get the service
     * object.
     * 
     * @param spec the service specification (interface)
     * @return the proxy object.
     */
    public Object getProxy(Class spec) {
        return java.lang.reflect.Proxy.newProxyInstance(m_dependency.getHandler().getInstanceManager().getClazz()
                .getClassLoader(), new Class[] { spec }, this);
    }

    /**
     * Invocation Handler delegating invocation on the service object.
     * 
     * @param proxy the proxy object
     * @param method the method
     * @param args the arguments
     * @return a proxy object.
     * @throws Exception if the invocation throws an exception
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        Class declaringClass = method.getDeclaringClass();
        if (declaringClass == Object.class) {
            return delegateObjectMethodsInvokation(method, args);
        }
        return delegateServiceInvokation(method, args);
    }

    private Object delegateObjectMethodsInvokation(Method method, Object[] args) throws Exception {
        if (method.equals(m_hashCodeMethod)) {
            return getService().hashCode();
        } else if (method.equals(m_equalsMethod)) {
            try {
                // if is a iCasaProxy return true if the tow services are the same
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[0]);
                if (invocationHandler instanceof ICasaContextProxyFactory) {
                    ICasaContextProxyFactory argumentProxyFactory = (ICasaContextProxyFactory) invocationHandler;
                    return getService().equals(argumentProxyFactory.getService());
                }
            } catch (IllegalArgumentException e) {
                // Nothing to do
            }
            return Boolean.FALSE;
        } else if (method.equals(m_toStringMethod)) {
            return this.toString();
        } else {
            throw new InternalError("Unexpected Object method dispatched: " + method);
        }
    }

    protected Object delegateServiceInvokation(Method method, Object[] args) throws Exception {
        Object service = getService();

        if (service == null) {
            throw new IllegalStateException("No device injected yet");
        }

        String methodName = method.getName();

        if (methodName.equals("createZone") || methodName.equals("removeZone") || methodName.equals("moveZone")
                || methodName.equals("resizeZone") || methodName.equals("getZoneVariables")
                || methodName.equals("getZoneVariableValue") || methodName.equals("addZoneVariable")
                || methodName.equals("setZoneVariable")) {
            
            ContextDependencyHandler handler = (ContextDependencyHandler) m_dependency.getHandler();
            String zoneId = (String) args[0];

            String contextGroupId = handler.getPrivateZoneGroupId(zoneId);

            if (contextGroupId != null) {
                if (!contextGroupId.equals(m_dependency.getContextGroupId())) {
                    throw new IllegalStateException("Dependency is not owner of zone: " + zoneId + " . Method: " + methodName + " cannot be invoked");
                }
            }
        }
        
        

        /*
         * 
         * if (methodName.equals("addZoneVariable") || methodName.equals("setZoneVariable")) { String variableName =
         * (String) args[1]; ContextDependencyHandler handler = (ContextDependencyHandler) m_dependency.getHandler();
         * 
         * 
         * 
         * if (handler.isPrivateVariable(variableName)) { String dependencyId = m_dependency.getDependencyID(); String
         * ownerId = handler.getInstanceOwner(variableName);
         * 
         * System.out.println(" <=====================================================> ");
         * System.out.println(methodName + " ======> Private Variable: " + variableName);
         * System.out.println(" ======> Dependency ID: " + dependencyId); System.out.println(" ======> Owner ID: " +
         * ownerId);
         * 
         * if (!ownerId.equals(dependencyId)) { throw new IllegalStateException("Dependency is not owner of variables");
         * } }
         * 
         * }
         */

        return method.invoke(service, args);
    }

    protected Object getService() {
        Object service = null;
        try {
            service = m_dependency.getService();
        } catch (Exception e) {
            // TODO: handle exception
        }

        return service;
    }

}