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
package fr.liglab.adele.device.model.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.medical.common.Attributable;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.ApplicationDeviceProxy;
import org.medical.device.manager.Device;
import org.medical.device.manager.Operation;
import org.medical.device.manager.Service;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Creates java dynamic proxy object for application devices.
 */
public class DynamicProxyFactory {

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
	 * Used class loader.
	 */
	private ClassLoader _cl;
	
	private Class[] _proxyInterfaces;

	/**
	 * Creates a DeviceProxyFactory.
	 */
	public DynamicProxyFactory(ClassLoader cl, Class... proxyClasses) {
		_proxyInterfaces = proxyClasses;
		_cl = cl;
		try {
			m_hashCodeMethod = Object.class.getMethod("hashCode", null);
			m_equalsMethod = Object.class.getMethod("equals",
					new Class[] { Object.class });
			m_toStringMethod = Object.class.getMethod("toString", null);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}

	/**
	 * Creates a proxy object for the given specification. The proxy delegates to specified object.
	 * 
	 * @param spec
	 *            the public interface of the proxy
	 * @return the proxy object.
	 */
	public Object getProxy(final Device device) {
		return java.lang.reflect.Proxy.newProxyInstance(_cl,
				_proxyInterfaces, new InvocationHandler() {
			
			private Device _device = device;
			
			/**
			 * Invocation Handler delegating invocation on the delegate object.
			 * 
			 * @param proxy
			 *            the proxy object
			 * @param method
			 *            the method
			 * @param args
			 *            the arguments
			 * @return a proxy object.
			 * @throws Exception
			 *             if the invocation throws an exception
			 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
			 *      java.lang.reflect.Method, java.lang.Object[])
			 */
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Exception {
				Class declaringClass = method.getDeclaringClass();
				if (declaringClass == Object.class) {
					if (method.equals(m_hashCodeMethod)) {
						return new Integer(this.hashCode());
					} else if (method.equals(m_equalsMethod)) {
						return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
					} else if (method.equals(m_toStringMethod)) {
						return this.toString();
					} else {
						throw new InternalError("Unexpected Object method dispatched: "
								+ method);
					}
				} else if (declaringClass == ApplicationDeviceProxy.class) {
					return ((ApplicationDevice) _device).getApplication();
				}
				
				Attributable attributable = null;
				if (declaringClass == GenericDevice.class) {
					if (method.getName().equals("getSerialNumber"))
						return _device.getId();
					
					attributable = _device;
				} else {
					Service serv = _device.getService(declaringClass.getName());
					attributable = serv;
				}
				//TODO cache used methods
				if (attributable != null) {
					// case of attribute
					if (ReflectUtil.isGetterMethod(method)) {
						String attrName = ReflectUtil.getAttrName(method);
						return attributable.getPropertyValue(attrName);
					} else if (ReflectUtil.isSetterMethod(method)) {
						String attrName = ReflectUtil.getAttrName(method);
						attributable.setPropertyValue(attrName, args[0]);
						return null;
					}
					
					// case of operation
					Service serv = (Service) attributable;
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (!method.getReturnType().equals(Void.TYPE)) {
						parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length + 1);
						parameterTypes[parameterTypes.length - 1] = method.getReturnType();
					}
					Operation op = serv.getOperation(method.getName(), parameterTypes);
					return op.invoke(args);
				}

				// should not happen
				throw new IllegalStateException("Cannot find mapping between method " + method.toGenericString() + " to device model");
			}
		});
	}

}