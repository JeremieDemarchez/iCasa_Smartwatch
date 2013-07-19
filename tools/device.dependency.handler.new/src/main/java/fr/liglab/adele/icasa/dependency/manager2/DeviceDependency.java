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
package fr.liglab.adele.icasa.dependency.manager2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Comparator;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyCallback;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class DeviceDependency extends Dependency {

	private ComponentInstance instance;

	private PrimitiveHandler m_handler;

	public DeviceDependency(DependencyHandler handler, String field, Class spec, Filter filter, boolean isOptional,
	      boolean isAggregate, boolean nullable, boolean isProxy, String identity, BundleContext context, int policy,
	      Comparator cmp, String defaultImplem) {
		super(handler, field, spec, filter, isOptional, isAggregate, nullable, isProxy, identity, context, policy, cmp,
		      defaultImplem);
		instance = handler.getInstanceManager();
		m_handler = handler;

		System.out.println("------------------------> Instance Name: " + instance.getInstanceName() + " Dependency: "
		      + field);
	}

	protected DependencyCallback[] getCallbacks() {
		return super.getCallbacks();
	}

	protected void setType(int type) {
		super.setType(type);
	}

	protected void addConstructorInjection(int index) throws ConfigurationException {
		super.addConstructorInjection(index);
	}

	protected void addDependencyCallback(DependencyCallback callback) {
		super.addDependencyCallback(callback);
	}

	protected void onObjectCreation(Object pojo) {
		super.onObjectCreation(pojo);
	}

	@Override
	public boolean match(ServiceReference ref) {
		// Determines if service reference match the "filters"
		// here we have to call the AccessManager Service

		System.out.println("--------------------------------------> " + instance.getInstanceName() + "Device Number: "
		      + ref);

		// String value = (String) ref.getProperty("device.serialNumber");

		return super.match(ref);
	}

	@Override
	public void start() {
		super.start();
				
		try {
	      Field field = this.getClass().getSuperclass().getDeclaredField("m_proxyObject");
	      
	      DynamicProxyFactory proxyFactory = new DynamicProxyFactory();
	      Object proxy = proxyFactory.getProxy(getSpecification());
	      
	      field.setAccessible(true);
	      field.set(this, proxy);
      } catch (SecurityException e) {
	      e.printStackTrace();
      } catch (NoSuchFieldException e) {
	      e.printStackTrace();
      } catch (IllegalArgumentException e) {
	      e.printStackTrace();
      } catch (IllegalAccessException e) {
	      e.printStackTrace();
      }

	}
	
   private class DynamicProxyFactory implements InvocationHandler {

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
       */
      public DynamicProxyFactory() {
          try {
              m_hashCodeMethod = Object.class.getMethod("hashCode", null);
              m_equalsMethod = Object.class
                  .getMethod("equals", new Class[] { Object.class });
              m_toStringMethod = Object.class.getMethod("toString", null);
          } catch (NoSuchMethodException e) {
              throw new NoSuchMethodError(e.getMessage());
          }
      }

      /**
       * Creates a proxy object for the given specification. The proxy
       * uses the given dependency to get the service object.
       * @param spec the service specification (interface)
       * @return the proxy object.
       */
      public Object getProxy(Class spec) {
          return java.lang.reflect.Proxy.newProxyInstance(
         		 	m_handler.getInstanceManager().getClazz().getClassLoader(),
                  new Class[] {spec},
                  this);
      }

      /**
       * Invocation Handler delegating invocation on the
       * service object.
       * @param proxy the proxy object
       * @param method the method
       * @param args the arguments
       * @return a proxy object.
       * @throws Exception if the invocation throws an exception
       * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
       */
      public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
          Object svc = getService();
          Class declaringClass = method.getDeclaringClass();
          if (declaringClass == Object.class) {
              if (method.equals(m_hashCodeMethod)) {
                  return new Integer(this.hashCode());
              } else if (method.equals(m_equalsMethod)) {
                  return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
              } else if (method.equals(m_toStringMethod)) {
                  return this.toString();
              } else {
                  throw new InternalError(
                          "Unexpected Object method dispatched: " + method);
              }
          }
          System.out.println("================ Invoking iCasa Handler =============");
          return method.invoke(svc, args);
      }

  }

}
