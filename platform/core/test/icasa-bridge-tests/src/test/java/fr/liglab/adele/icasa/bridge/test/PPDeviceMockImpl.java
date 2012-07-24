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
package fr.liglab.adele.icasa.bridge.test;

import fr.liglab.adele.icasa.device.DeviceListener;

public class PPDeviceMockImpl implements PPDevice {
	
	private boolean _powerState = true;
	private String _fault;
	private String _state;
	private String _id;
	private String _location;

	public PPDeviceMockImpl(String id, String state, String location, String fault) {
		_id = id;
		_fault = fault;
		_location = location;
		_state = state;
	}

	@Override
	public void addListener(DeviceListener arg0) {
		// do nothing
	}

	@Override
	public String getFault() {
		return _fault;
	}

	@Override
	public String getLocation() {
		return _location;
	}

	@Override
	public String getSerialNumber() {
		return _id;
	}

	@Override
	public String getState() {
		return _state;
	}

	@Override
	public void removeListener(DeviceListener arg0) {
		// do nothing
	}

	@Override
	public void setFault(String fault) {
		_fault = fault;
	}

	@Override
	public void setState(String state) {
		_state = state;
	}

	@Override
	public boolean getSensedPresence() {
		return false;
	}

	@Override
	public boolean getPowerStatus() {
		return _powerState;
	}

	@Override
	public boolean setPowerStatus(boolean powerState) {
		_powerState = powerState;
		return _powerState;
	}

}
