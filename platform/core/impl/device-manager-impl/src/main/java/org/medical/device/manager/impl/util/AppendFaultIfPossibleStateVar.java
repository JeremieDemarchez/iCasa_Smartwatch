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
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.impl.DeriveIfPossibleStateVar;
import org.medical.device.manager.util.FaultUtil;

/**
 * This variable implementation calls delegate object variable getter and append its value to this variable value.
 * This behavior is only applied for Collection variable types. 
 * 
 * @author Thomas Leveque
 *
 */
public class AppendFaultIfPossibleStateVar extends DeriveIfPossibleStateVar
		implements StateVariable {

	private List<DetailedFault> _oldDelegateFaults;

	public AppendFaultIfPossibleStateVar(StateVariable originalVar,
			Attributable delegateObj) {
		super(originalVar, delegateObj);
		
		if (!(Collection.class.isAssignableFrom(originalVar.getValueType())))
			throw new IllegalArgumentException("This variable must be of collection type.");
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
				return super.getValue();

			updateFaults();
		}
		
		return super.getValue();
	}

	private void updateFaults() {
		synchronized (_lockDelegate) {
			List<DetailedFault> delegateFaults = (List<DetailedFault>) getDelegateVar()
					.getValue();

			List<DetailedFault> _originalValues = null;
			if (_originalVar != null) {
				_originalValues = (List<DetailedFault>) _originalVar.getValue();
			} else {
				_originalValues = new ArrayList<DetailedFault>();
			}

			List<DetailedFault> mergedFaults = (List<DetailedFault>) super
					.getValue();
			FaultUtil.mergeFaults(_oldDelegateFaults, delegateFaults,
					mergedFaults);
		}
	}

	@Override
	public synchronized void notifValueChange(StateVariable variable, Object oldValue,
			Object sourceObject) {
		if (variable.getName().equals(getName())) {
			_oldDelegateFaults = (List<DetailedFault>) oldValue;
			updateFaults(); // update the internal value and send notifs
		}
	}
}
