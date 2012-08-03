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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.ParameterType;
import org.medical.device.manager.Service;
import org.medical.device.manager.util.AbstractOperation;

public class DerivedOperationFromIntf extends AbstractOperation {

	private final Object _orignalObj;
	private final Method _originalMethod;

	public DerivedOperationFromIntf(Method method, Object originalObj,
			Service service) {
		super(method.getName(), service);
		_orignalObj = originalObj;
		_originalMethod = method;
		
		String paramName = "arg";
		int argNb = 0;
		for (Class paramType : method.getParameterTypes()) {
			OperationParameter param = new DerivedOpParamFromIntf(paramName + argNb, paramType, ParameterType.IN, "");
			addParameter(param);
			argNb++;
		}
			
		if (!method.getReturnType().equals(Void.TYPE)) {
			OperationParameter param = new DerivedOpParamFromIntf("returnedArg", method.getReturnType(), ParameterType.OUT, "");
			addParameter(param);
		}
	}

	@Override
	public Object invoke(Object... args) {
		try {
			return _originalMethod.invoke(_orignalObj, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//TODO better manage exceptions
		
		return null;
	}

	@Override
	public void invoke(OperationParameter... params) {
		
		final List<OperationParameter> opParams = getParametersInternal();
		boolean hasParams = opParams.size() > 0;
		boolean hasReturn = hasParams && opParams.get(opParams.size() - 1).getParameterType().equals(ParameterType.OUT);
		Object[] args = hasReturn ? new Object[opParams.size() - 1] : new Object[opParams.size()];
		
		for (int i = 0; i < params.length; i++) {
			if ((i == (params.length - 1)) && hasReturn)
				break;
			
			OperationParameter arg = params[i];
			args[i] = arg.getValue();
		}
		
		Object returnObj = invoke(args);
		if (hasReturn)
			params[params.length - 1].setValue(returnObj);
	}
}
