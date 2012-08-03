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

import java.lang.reflect.Method;

import org.medical.device.manager.ProvidedDevice;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.GenericDevice;

public class ReflectUtil {

	public static ProvidedDevice createProvidedDev(GenericDevice device, BundleContext context) {
		
		return new ProvidedDeviceFromIntf(device, context);
	}

	public static Class getVariableTypeFrom(Method getterMethod,
			Method setterMethod) {
		
		if (getterMethod != null) {
			return getterMethod.getReturnType();
		}
		
		if (setterMethod != null) {
			Class[] paramTypes = setterMethod.getParameterTypes();
			if ((paramTypes != null) && (paramTypes.length > 0))
				return paramTypes[0];
		}
		
		return null;
	}

	public static String getVendorFromPackage(GenericDevice device) {
		String vendor = "";
		String packageName = device.getClass().getPackage().getName();
		String[] packageParts  = packageName.split("\\.");
		if (packageParts.length > 2)
			vendor = packageParts[1];
		
		return vendor;
	}

	public static Object callGetter(Method getterMethod, Object object) {
		if (getterMethod == null)
			return null;
		
		try {
			return getterMethod.invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	public static Method getGetterMethod(String attrName, Class attrType, Class interf) {
		attrName = upperCaseFirstChar(attrName);
		
		Method getterMethod = null;
		if (attrType.isPrimitive() && (Boolean.class.equals(attrType))) {
			try {
				getterMethod = interf.getMethod("is" + attrName);
				if (!Boolean.class.equals(getterMethod.getReturnType()))
					getterMethod = null;
			} catch (Exception e) {
				// do nothing
			} 
			if (getterMethod != null)
				return getterMethod;
		}
		
		try {
			getterMethod = interf.getMethod("get" + attrName);
			if (!attrType.equals(getterMethod.getReturnType()))
				getterMethod = null;
		} catch (Exception e) {
			// do nothing
		} 
		
		return getterMethod;
	}

	public static String upperCaseFirstChar(String attrName) {
		char[] stringArray = attrName.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		
		return new String(stringArray);
	}

	public static void callSetter(Method setterMethod, Object value,
			Object object) {
		if (setterMethod == null)
			return;
		
		try {
			setterMethod.invoke(object, value);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public static Method getSetterMethod(String attrName,
			Class<String> attrType, Class interf) {
		attrName = upperCaseFirstChar(attrName);
		
		Method setterMethod = null;
		try {
			setterMethod = interf.getMethod("set" + attrName, attrType);
		} catch (Exception e) {
			// do nothing
		}
		
		return setterMethod;
	}

	public static boolean isGetterMethod(Method method) {
		if (method.getReturnType().equals(Void.TYPE))
			return false;
		if (method.getParameterTypes().length > 0)
			return false;
		
		String methodName = method.getName();
		String postFix = null; 
		if (methodName.startsWith("is")) {
			postFix = methodName.substring(2); 
		} else if (methodName.startsWith("get")) {
			postFix = methodName.substring(3); 
		} else
			return false;

		
		return Character.isUpperCase(postFix.charAt(0));
	}

	public static boolean isSetterMethod(Method method) {
		String methodName = method.getName();
		if (!methodName.startsWith("set"))
			return false;
		final Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1)
			return false;
		
		return Character.isUpperCase(methodName.charAt(3));
	}

	public static String getAttrName(Method method) {
		String methodName = method.getName();
		String postFix = null; 
		if (methodName.startsWith("is")) {
			return methodName.substring(2); 
		} else if ((methodName.startsWith("get")) || (methodName.startsWith("set"))) {
			return methodName.substring(3); 
		} 
		
		return null;
	}
}
