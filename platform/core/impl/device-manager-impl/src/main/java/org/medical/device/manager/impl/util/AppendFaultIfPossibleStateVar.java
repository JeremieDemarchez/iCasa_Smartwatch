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
package org.medical.device.manager.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.medical.common.Attributable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.impl.ComparisonUtil;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.impl.DeriveIfPossibleStateVar;
import org.medical.device.manager.util.FaultUtil;

/**
 * This variable implementation calls delegate object variable getter and append
 * its value to this variable value. This behavior is only applied for
 * Collection variable types.
 * 
 * @author Thomas Leveque
 * 
 */
public class AppendFaultIfPossibleStateVar extends DeriveIfPossibleStateVar {

	private List<DetailedFault> _oldFaults;
	private List<DetailedFault> _newFaults;

	protected StateVariableListener _originalVarListener = new StateVariableListener() {

		@Override
		public void addVariable(StateVariable variable, Object sourceObject) {
			// do nothing
		}

		@Override
		public void removeVariable(StateVariable variable, Object sourceObject) {
			_originalVar.removeListener(this);
		}

		@Override
		public void notifValueChange(StateVariable variable, Object oldValue,
				Object newValue, Object sourceObject) {

			synchronized (_lockDelegate) {
				notifyValueChange(
						mergeFaults((List<DetailedFault>) oldValue,
								getDelegateFaults()),
						mergeFaults((List<DetailedFault>) newValue,
								getDelegateFaults()));
			}
		}
	};

	protected StateVariableListener _delegateVarListener = new StateVariableListener() {

		@Override
		public void addVariable(StateVariable variable, Object sourceObject) {
			updateDelegateVar(variable);
		}

		@Override
		public void removeVariable(StateVariable variable, Object sourceObject) {
			updateDelegateVar(null);
		}

		@Override
		public void notifValueChange(StateVariable variable, Object oldValue,
				Object newValue, Object sourceObject) {
			synchronized (_lockDelegate) {
				notifyValueChange(
						mergeFaults(getOriginalFaults(),
								(List<DetailedFault>) oldValue),
						mergeFaults(getOriginalFaults(),
								(List<DetailedFault>) newValue));
			}
		}
	};

	public AppendFaultIfPossibleStateVar(StateVariable originalVar,
			Attributable delegateObj) {
		super(originalVar, delegateObj);

		if (!(Collection.class.isAssignableFrom(originalVar.getValueType())))
			throw new IllegalArgumentException(
					"This variable must be of collection type.");
	}

	@Override
	protected void setValueInternal(Object value) {
		if (_originalVar != null)
			_originalVar.setValue(value);

		// do not update delegate value
	}

	@Override
	public Object getValue() {
		synchronized (_lockDelegate) {
			if (getDelegateVar() == null)
				return _originalVar.getValue();

			return mergeFaults(getOriginalFaults(), getDelegateFaults());
		}
	}

	private List<DetailedFault> getDelegateFaults() {
		StateVariable delegateVar = getDelegateVar();
		if (delegateVar == null)
			return new ArrayList<DetailedFault>();

		return (List<DetailedFault>) delegateVar.getValue();
	}

	private List<DetailedFault> mergeFaults(List<DetailedFault> originalValues,
			List<DetailedFault> delegateFaults) {
		synchronized (_lockDelegate) {
			List<DetailedFault> mergedFaults = new ArrayList<DetailedFault>(
					originalValues);
			FaultUtil.mergeFaults(null, delegateFaults, mergedFaults);

			return mergedFaults;
		}
	}

	private List<DetailedFault> getOriginalFaults() {
		List<DetailedFault> _originalValues = null;
		if (_originalVar != null) {
			_originalValues = (List<DetailedFault>) _originalVar.getValue();
		} else {
			_originalValues = new ArrayList<DetailedFault>();
		}

		return _originalValues;
	}

	protected void notifyValueChange(Object oldValue, Object newValue) {
		if (ComparisonUtil.same(oldValue, newValue))
			return;

		synchronized (_listeners) {
			for (StateVariableListener listener : _listeners) {
				listener.notifValueChange(this, oldValue, newValue,
						_originalVar.getOwner());
			}
		}
	}
}
