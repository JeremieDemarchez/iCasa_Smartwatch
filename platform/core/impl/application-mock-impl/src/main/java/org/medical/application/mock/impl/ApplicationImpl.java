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
package org.medical.application.mock.impl;

import java.util.Set;

import org.medical.common.ProgressMonitor;
import org.medical.common.StateVariable;
import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;
import org.medical.common.impl.EntityImpl;
import org.medical.application.Application;
import org.medical.application.ApplicationCategory;
import org.medical.application.ApplicationState;
import org.osgi.framework.Bundle;

public class ApplicationImpl extends EntityImpl implements Application {

	private static final String VENDOR_PROP_NAME = "Vendor";

	private static final String NAME_PROP_NAME = "Name";

	private static final String ACTIVATION_STATE_PROP_NAME = "ActivationState";
	
	private ApplicationCategory _category;

	public ApplicationImpl(String name, String vendor, ApplicationCategory category) {
		this(name, vendor, category, ApplicationState.STOPED);
	}

	public ApplicationImpl(String name, String vendor,
			ApplicationCategory category, ApplicationState state) {
		super(name);
		StateVariable vendorVar = new StateVariableImpl(VENDOR_PROP_NAME, vendor, String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "Entity identifier", false, true, this);
		addStateVariable(vendorVar);
		StateVariable nameVar = new StateVariableImpl(NAME_PROP_NAME, name, String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "Name", false, true, this);
		addStateVariable(nameVar);
		StateVariable stateVar = new StateVariableImpl(ACTIVATION_STATE_PROP_NAME, state, ApplicationState.class, VariableType.STATE, "State", true, true, this);
		addStateVariable(stateVar);
		
		if (category == null)
			throw new IllegalArgumentException("category cannot be null.");
		
		_category = category;
	}

	@Override
	public String getName() {
		return getId();
	}

	@Override
	public String getVendor() {
		return (String) getPropertyValue(VENDOR_PROP_NAME);
	}

	@Override
	public ApplicationCategory getCategory() {
		return _category;
	}

	@Override
	public void start(ProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public void stop(ProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public void resume(ProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public void pause(ProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public Set<Bundle> getBundles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationState getState() {
		return (ApplicationState) getPropertyValue(ACTIVATION_STATE_PROP_NAME);
	}
	
	public void setState(ApplicationState newState) {
		setPropertyValue(ACTIVATION_STATE_PROP_NAME, newState);
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
