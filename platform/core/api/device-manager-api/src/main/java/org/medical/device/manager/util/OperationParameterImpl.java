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
package org.medical.device.manager.util;

import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;
import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.ParameterType;

public class OperationParameterImpl extends StateVariableImpl implements OperationParameter {
	
	private ParameterType _paramType;
	
	public OperationParameterImpl(String name, Object value, Class type,
			ParameterType paramType, String description) {
		super(name, value, type, VariableType.UNKNOWN, description, ParameterType.IN_OUT.equals(paramType) || ParameterType.OUT.equals(paramType), true, null);
		_paramType = paramType;
	}
	
	public OperationParameterImpl(OperationParameter param) {
		this(param.getName(), param.getValue(), param.getValueType(), param.getParameterType(), param.getDescription());
	}

	@Override
	public ParameterType getParameterType() {
		return _paramType;
	}

}
