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
package fr.liglab.adele.icasa.dependency.manager;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyCallback;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightListener;
import fr.liglab.adele.icasa.dependency.manager.proxy.ICasaAggregateProxyFactory;
import fr.liglab.adele.icasa.dependency.manager.proxy.ICasaProxyFactory;

public class DeviceDependency extends Dependency implements AccessRightListener {

	/**
	 * Type of callback to avoid iPOJO invokation in obObjectCreation
	 */
	public static final int BIND_ICASA = 10;

	public static final int UNBIND_ICASA = 11;

	private DeviceDependencyHandler m_handler;

	private BundleContext m_context;
	
	/**
	 * The application Id associated to this dependency
	 */
	private String applicationId;


	/**
	 * Access right set of dependency. An instance added by new matching service
	 */	
	private Map<String, AccessRight> m_accessRights = new HashMap<String, AccessRight>();

	public DeviceDependency(DeviceDependencyHandler handler, String field, Class spec, Filter filter,
	      boolean isOptional, boolean isAggregate, boolean nullable, boolean isProxy, String identity,
	      BundleContext context, int policy, Comparator cmp, String defaultImplem) {
		super(handler, field, spec, filter, isOptional, isAggregate, nullable, isProxy, identity, context, policy, cmp,
		      defaultImplem);

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
		if (applicationId==null) {
			applicationId = m_handler.getApplicationId(m_context);
		}
		return applicationId;
	}
	
	public void addAccessRight(String deviceId, AccessRight accessRight) {
		boolean existingEntry = m_accessRights.containsKey(deviceId);
		if (!existingEntry) {
			synchronized (m_accessRights) {
		      m_accessRights.put(deviceId, accessRight);
	      }
			accessRight.addListener(this);
		}
   }
	
	public AccessRight getAccessRight(String deviceId) {
		AccessRight accessRight = null;
		synchronized (m_accessRights) {
			accessRight = m_accessRights.get(deviceId);
      }	
		return accessRight;
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

	/**
	 * This method was override to invoke our callUnbindMethodIcasa method instead of iPOJO callUnbindMethod
	 */
	@Override
	public void onServiceDeparture(ServiceReference reference) {
		callUnbindMethodIcasa(reference);
	}

	/**
	 * This method was override to invoke our callBindMethodIcasa method instead of iPOJO callBindMethod
	 */
	@Override
	public void onServiceArrival(ServiceReference reference) {
		callBindMethodIcasa(reference);
	}

	/**
	 * This method was override to invoke our callBindMethodIcasa method instead of iPOJO callBindMethod This method was
	 * override to invoke our callUnbindMethodIcasa method instead of iPOJO callUnbindMethod
	 * 
	 */
	@Override
	public void onDependencyReconfiguration(ServiceReference[] departs, ServiceReference[] arrivals) {
		for (int i = 0; departs != null && i < departs.length; i++) {
			callUnbindMethodIcasa(departs[i]);
		}

		for (int i = 0; arrivals != null && i < arrivals.length; i++) {
			callBindMethodIcasa(arrivals[i]);
		}
	}

	/**
	 * This method passes the proxy object in the callback
	 * 
	 * @param ref
	 */
	private void callUnbindMethodIcasa(ServiceReference ref) {
		if (m_handler.getInstanceManager().getState() > InstanceManager.STOPPED
		      && m_handler.getInstanceManager().getPojoObjects() != null) {

			DependencyCallback[] m_callbacks = getCallbacks();
			for (int i = 0; m_callbacks != null && i < m_callbacks.length; i++) {
				if (m_callbacks[i].getMethodType() == DeviceDependency.UNBIND_ICASA) {

					Object svc = getService(ref, false);

					ICasaAggregateProxyFactory proxyFactory = new ICasaAggregateProxyFactory(this, svc);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());

					invokeCallbackIcasa(m_callbacks[i], ref, proxiedObject, null); // Call on each created pojo objects.
				}
			}
		}
	}

	/**
	 * This method passes the proxy object in the callback
	 * 
	 * @param ref
	 */
	private void callBindMethodIcasa(ServiceReference ref) {
		if (m_handler.getInstanceManager().getState() > InstanceManager.STOPPED
		      && m_handler.getInstanceManager().getPojoObjects() != null) {

			DependencyCallback[] m_callbacks = getCallbacks();

			for (int i = 0; m_callbacks != null && i < m_callbacks.length; i++) {
				if (m_callbacks[i].getMethodType() == DeviceDependency.BIND_ICASA) {

					Object svc = getService(ref);

					ICasaAggregateProxyFactory proxyFactory = new ICasaAggregateProxyFactory(this, svc);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());

					if (svc != null) {
						invokeCallbackIcasa(m_callbacks[i], ref, proxiedObject, null);
					} else { // TODO: Problem with private package in iPOJO Bundle
						// We can't get the service object (https://issues.apache.org/jira/browse/FELIX-3896).
						// This is probably because the service is leaving.
						// We consider it as a departure.

						// m_serviceReferenceManager.removedService(ref, null);
					}
				}
			}
		}
	}

	/**
	 * This method invokes the private invokeCallback methodd in Dependency class
	 * 
	 * @param callback
	 * @param ref
	 * @param svcObject
	 * @param pojo
	 */
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

	/*
	 * @Override public Object getService() { return super.getService(); }
	 */

	/**
	 * This method passes the proxy object in two cases: when the reference is an Aggregate and the type is Array or
	 * Vector. IPOJO does not use proxy in this cases.
	 */
	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
		Object currentValue = super.onGet(pojo, fieldName, value);

		
		
		if (isAggregate()) {
			int type = getObjectType(); // Getting the type of aggregate dependency (list, set, array, vector)
			if (type == 0) { // Array case
				Object[] currentArray = (Object[]) currentValue;
				Object[] newArray = (Object[]) Array.newInstance(getSpecification(), currentArray.length);
				for (int i = 0; i < currentArray.length; i++) {
					ICasaAggregateProxyFactory proxyFactory = new ICasaAggregateProxyFactory(this, currentArray[i]);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());
					newArray[i] = proxiedObject;
				}
				return newArray;
			} else if (type == 2) { // Vector case - We should use DependencyHandler.VECTOR, but it is a protected field
				Vector currentVector = (Vector) currentValue;
				Vector newVector = new Vector();
				for (Object element : currentVector) {
					ICasaAggregateProxyFactory proxyFactory = new ICasaAggregateProxyFactory(this, element);
					Object proxiedObject = proxyFactory.getProxy(getSpecification());
					newVector.add(proxiedObject);
				}
				return newVector;
			}
		}

		return currentValue;
	}

	/*
	@Override
	public void resetLocalCache() {
		super.resetLocalCache();
	}
	*/

	
	/**
	 * Method added to obtain the m_type field value of class Dependency.
	 * This field is used to determine the type of object to inject (0: Array, 1:List, 2:Vector, 3:Set)
	 * @return
	 */
	protected int getObjectType() {
		Field field;
		try {
			field = this.getClass().getSuperclass().getDeclaredField("m_type");
			field.setAccessible(true);
			return (Integer) field.get(this);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * Method overridden to modify m_proxyObject in Depedency with our ProxyFactory functionality:
	 * 1. If dependency is aggregate uses our ICasaServiceCollection. It allows the creation of proxies when iterator are used.
	 * 2. If dependency is scalar uses ICasaProxyFactory proxy factory
	 */
	@Override
	public void start() {
		super.start();

		try {
			// Preparing private m_proxyObject field for modification
			Field field = this.getClass().getSuperclass().getDeclaredField("m_proxyObject");
			field.setAccessible(true);

			if (isProxy()) {
				if (isAggregate()) { // Set, List, Collection
					field.set(this, new ICasaServiceCollection(this));
				} else { // Scalar dependency
					ICasaProxyFactory proxyFactory = new ICasaProxyFactory(this);
					Object proxy = proxyFactory.getProxy(getSpecification());
					field.set(this, proxy);
				}
			}

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
		synchronized (m_accessRights) {
			copySet.addAll(m_accessRights.values());
			m_accessRights.clear();
		}

		for (AccessRight accessRight : copySet) {
			accessRight.removeListener(this);
		}

	}

}
