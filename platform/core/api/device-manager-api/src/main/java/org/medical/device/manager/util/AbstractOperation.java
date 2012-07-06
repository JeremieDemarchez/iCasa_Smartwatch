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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.medical.common.StateVariable;
import org.medical.device.manager.Operation;
import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.Service;

public abstract class AbstractOperation implements Operation {
	
	private final List<OperationParameter> _parameters = new ArrayList<OperationParameter>();

	private final Service _service;

	private final String _name;

	public AbstractOperation(String name, Service service) {
		_name = name;
		_service = service;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Service getService() {
		return _service;
	}

	@Override
	public List<OperationParameter> getParameters() {
		List<OperationParameter> clonedParams = new ArrayList<OperationParameter>();
		synchronized(_parameters) {
			for (OperationParameter param : _parameters) {
				clonedParams.add(new OperationParameterImpl(param.getName(), param.getValue(), param.getValueType(), param.getParameterType(), param.getDescription()));
			}
		}
		
		return clonedParams;
	}
	
	protected final void addParameter(OperationParameter param) {
		synchronized(_parameters) {
			_parameters.add(param);
		}
	}
	
	protected final List<OperationParameter> getParametersInternal() {
		return Collections.unmodifiableList(_parameters);
	}


}
