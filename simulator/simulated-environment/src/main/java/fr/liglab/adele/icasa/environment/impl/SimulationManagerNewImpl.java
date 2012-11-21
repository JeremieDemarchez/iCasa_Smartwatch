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
package fr.liglab.adele.icasa.environment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.icasa.environment.Device;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.environment.Zone;

public class SimulationManagerNewImpl implements SimulationManagerNew {

	private Map<String, Zone> zones = new HashMap<String, Zone>();
	
	private Map<String, Device> devices = new HashMap<String, Device>();
	
	private Map<String, Person> persons = new HashMap<String, Person>();
	
	
	@Override
	public void createZone(String id, String description, int leftX, int topY, int width, int height) {
		Zone zone = new ZoneImpl(leftX, topY ,width, height);
		zones.put(id, zone);
	}


	@Override
	public List<Zone> getZones() {
		return new ArrayList<Zone>(zones.values());
	}

	@Override
	public List<Device> getDevices() {
		return new ArrayList<Device>(devices.values());
	}

	@Override
	public Zone getZone(String zoneId) {		
		return zones.get(zoneId);
	}

	@Override
	public Position getDevicePosition(String deviceSerialNumber) {
		Device device = devices.get(deviceSerialNumber);
		if (device!=null)
			return device.getPosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceSerialNumber, Position position) {
		Device device = devices.get(deviceSerialNumber);		
		if (device!=null)
			device.setPosition(position);		
	}

	@Override
	public void setDeviceZone(String deviceSerialNumber, String zoneId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = persons.get(userName);
		if (person!=null)
			person.setPosition(position);
	}

	@Override
	public void setPersonZone(String userName, String environmentId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAllPersons() {
		persons.clear();
	}

	@Override
	public void addPerson(String userName) {
		Person person = new PersonImpl(userName, null, null);
		persons.put(userName, person);
	}

	@Override
	public void removePerson(String userName) {
		persons.remove(userName);
	}

	@Override
	public List<Person> getPersons() {
		return new ArrayList<Person>(persons.values());
	}

	@Override
	public Set<String> getEnvironmentVariables(String zoneId) {
		Zone zone = zones.get(zoneId);
		return zone.getVariableList();
	}

	@Override
	public Double getVariableValue(String zoneId, String variable) {
		Zone zone = zones.get(zoneId);
		return zone.getVariableValue(variable);
	}

	@Override
	public void setEnvironmentVariable(String zoneId, String variable, Double value) {
		Zone zone = zones.get(zoneId);
		zone.setVariableValue(variable, value);
	}

	@Override
	public void setDeviceFault(String deviceId, boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDevice(String factoryName, String deviceId, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDevice(String deviceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getDeviceFactories() {
		// TODO Auto-generated method stub
		return null;
	}

}
