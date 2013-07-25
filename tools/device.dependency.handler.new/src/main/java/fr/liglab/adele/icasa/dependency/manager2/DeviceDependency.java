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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyCallback;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightListener;
import fr.liglab.adele.icasa.device.GenericDevice;

public class DeviceDependency extends Dependency implements AccessRightListener {

	public static final int BIND_ICASA = 10;

	public static final int UNBIND_ICASA = 11;

	private ComponentInstance m_instance;

	private DeviceDependencyHandler m_handler;

	private BundleContext m_context;

	private Set<AccessRight> accessRights = new HashSet<AccessRight>();

	public DeviceDependency(DeviceDependencyHandler handler, String field, Class spec, Filter filter,
	      boolean isOptional, boolean isAggregate, boolean nullable, boolean isProxy, String identity,
	      BundleContext context, int policy, Comparator cmp, String defaultImplem) {
		super(handler, field, spec, filter, isOptional, isAggregate, false, true, identity, context, policy, cmp,
		      defaultImplem);

		m_instance = handler.getInstanceManager();
		m_handler = handler;
		m_context = context;

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

	// --------------------------- Added methods ------------------------------ //

	@Override
	public void onAccessRightModified(AccessRight accessRight) {
		this.invalidateMatchingServices();
	}

	@Override
	public void onMethodAccessRightModified(AccessRight accessRight, String methodName) {
		// Nothing to be done here
	}

	public String getApplicationId() {
		return m_handler.getApplicationId(m_context);
	}

	public void addAccessRight(AccessRight accessRight) {
		boolean added = false;
		synchronized (accessRights) {
			added = accessRights.add(accessRight);
		}
		if (added)
			accessRight.addListener(this);
	}

	@Override
	protected void onObjectCreation(Object pojo) {

		super.onObjectCreation(pojo);

		ServiceReference[] refs;
		synchronized (this) {
			// Check optional case : nullable object case : do not call bind on nullable object
			if (isOptional() && getSize() == 0) {
				return;
			}

			refs = getServiceReferences(); // Stack confinement.
		}

		if (refs == null) {
			return;
		}

		// Call bind callback.
		DependencyCallback[] m_callbacks = getCallbacks();

		for (int j = 0; m_callbacks != null && j < m_callbacks.length; j++) { // The array is constant.
			if (m_callbacks[j].getMethodType() == DeviceDependency.BIND_ICASA) {
				if (isAggregate()) {
					for (int i = 0; i < refs.length; i++) {
						callBindMethodIcasa(refs[i]);
					}
				} else {
					callBindMethodIcasa(refs[0]);
				}
			}
		}
	}

	@Override
	public void onServiceDeparture(ServiceReference reference) {
		System.out.println("onServiceDeparture -----------> " + reference);
		callUnbindMethodIcasa(reference);
	}

	@Override
	public void onServiceArrival(ServiceReference reference) {
		System.out.println("onServiceArrival -----------> " + reference);
		callBindMethodIcasa(reference);
	}

	@Override
	public void onDependencyReconfiguration(ServiceReference[] departs, ServiceReference[] arrivals) {

		for (int i = 0; departs != null && i < departs.length; i++) {
			callUnbindMethodIcasa(departs[i]);
		}

		for (int i = 0; arrivals != null && i < arrivals.length; i++) {
			System.out.println("onDependencyReconfiguration -----------> " + arrivals[i]);
			callBindMethodIcasa(arrivals[i]);
		}
	}

	private void callUnbindMethodIcasa(ServiceReference ref) {
		if (m_handler.getInstanceManager().getState() > InstanceManager.STOPPED
		      && m_handler.getInstanceManager().getPojoObjects() != null) {

			DependencyCallback[] m_callbacks = getCallbacks();
			for (int i = 0; m_callbacks != null && i < m_callbacks.length; i++) {
				if (m_callbacks[i].getMethodType() == DeviceDependency.UNBIND_ICASA) {

					Object svc = getService(ref, false);

					AggregateDynamicProxyFactory proxyFactory = new AggregateDynamicProxyFactory(svc);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());

					invokeCallbackIcasa(m_callbacks[i], ref, proxiedObject, null); // Call on each created pojo objects.
				}
			}
		}
	}

	private void callBindMethodIcasa(ServiceReference ref) {
		// call bind method :
		// if (m_handler.getInstanceManager().getState() == InstanceManager.VALID) {

		if (m_handler.getInstanceManager().getState() > InstanceManager.STOPPED
		      && m_handler.getInstanceManager().getPojoObjects() != null) {

			DependencyCallback[] m_callbacks = getCallbacks();

			for (int i = 0; m_callbacks != null && i < m_callbacks.length; i++) {
				if (m_callbacks[i].getMethodType() == DeviceDependency.BIND_ICASA) {

					Object svc = getService(ref);

					AggregateDynamicProxyFactory proxyFactory = new AggregateDynamicProxyFactory(svc);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());

					if (svc != null) {
						invokeCallbackIcasa(m_callbacks[i], ref, proxiedObject, null);
					} else {
						// We can't get the service object (https://issues.apache.org/jira/browse/FELIX-3896).
						// This is probably because the service is leaving.
						// We consider it as a departure.

						// Problem with private package in iPOJO Bundle
						// m_serviceReferenceManager.removedService(ref, null);
					}
				}
			}
		}
	}

	protected void invokeCallbackIcasa(DependencyCallback callback, ServiceReference ref, Object svcObject, Object pojo) {
		try {
			Class[] paramTypes = { DependencyCallback.class, ServiceReference.class, Object.class, Object.class };
			Method method = this.getClass().getSuperclass().getDeclaredMethod("invokeCallback", paramTypes);
			method.setAccessible(true);
			method.invoke(this, callback, ref, svcObject, pojo);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Object getService() {
	   return super.getService();
	}
	
	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
	   return super.onGet(pojo, fieldName, value);
	}
	
	@Override
	public void resetLocalCache() {
	   super.resetLocalCache();
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

	@Override
	public synchronized void stop() {
		super.stop();

		Set<AccessRight> copySet = new HashSet<AccessRight>();
		synchronized (accessRights) {
			copySet.addAll(accessRights);
			accessRights.clear();
		}

		for (AccessRight accessRight : copySet) {
			accessRight.removeListener(this);
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
			return java.lang.reflect.Proxy.newProxyInstance(getHandler().getInstanceManager().getClazz().getClassLoader(),
			      new Class[] { spec }, this);
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
					throw new InternalError("Unexpected Object method dispatched: " + method);
				}
			}

			String deviceId = ((GenericDevice) svc).getSerialNumber();
			AccessRight accessRight = m_handler.getAccessRight(m_context, deviceId);

			if (accessRight != null && accessRight.hasMethodAccess(method)) {
				System.out.println("================ Invoking iCasa Handler =============");
				return method.invoke(svc, args);
			} else {
				throw new RuntimeException("No access to method " + method.getName());
			}

		}

	}

	private class AggregateDynamicProxyFactory implements InvocationHandler {

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

		private Object m_service;

		/**
		 * Creates a DynamicProxyFactory.
		 */
		public AggregateDynamicProxyFactory(Object service) {
			try {
				m_hashCodeMethod = Object.class.getMethod("hashCode", null);
				m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				m_toStringMethod = Object.class.getMethod("toString", null);
				m_service = service;
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
			return java.lang.reflect.Proxy.newProxyInstance(getHandler().getInstanceManager().getClazz().getClassLoader(),
			      new Class[] { spec }, this);
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
			// Object svc = getService(m_service);
			Class declaringClass = method.getDeclaringClass();
			if (declaringClass == Object.class) {
				if (method.equals(m_hashCodeMethod)) {
					return new Integer(this.hashCode());
				} else if (method.equals(m_equalsMethod)) {
					return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
				} else if (method.equals(m_toStringMethod)) {
					return this.toString();
				} else {
					throw new InternalError("Unexpected Object method dispatched: " + method);
				}
			}

			String deviceId = ((GenericDevice) m_service).getSerialNumber();
			AccessRight accessRight = m_handler.getAccessRight(m_context, deviceId);

			if (accessRight != null && accessRight.hasMethodAccess(method)) {
				System.out.println("================ Invoking Aggregate iCasa Handler =============");
				return method.invoke(m_service, args);
			} else {
				throw new RuntimeException("No access to method " + method.getName());
			}

		}

	}

}
