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
package org.medical.device.manager.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.KnownDevice;

/**
 * Enable to track all device events which match a specific filter.
 * 
 * @author Thomas Leveque
 *
 */
public class DeviceTracker implements DeviceListener {

	private final DeviceDependencies _dependencies;
	
	private boolean _started = false;
	
	private final Set<KnownDevice> _matchingKnownDevs = new HashSet<KnownDevice>();

	private final DevicetrackerCustomizer _devTrackCustom;

	private final GlobalDeviceManagerImpl _devMgr;
	
	public DeviceTracker(GlobalDeviceManagerImpl devMgr, DeviceDependencies dependencies, DevicetrackerCustomizer devTrackCustom) {
		_devMgr = devMgr;
		_dependencies = dependencies.cloneDeps();
		_devTrackCustom = devTrackCustom;
	}
	
	public DeviceTracker(GlobalDeviceManagerImpl devMgr, DeviceDependencies dependencies) {
		this(devMgr, dependencies, null);
	}

	public void open() {
		_started = true;
		_devMgr.addDeviceListener(this);
		synchronized(_matchingKnownDevs) {
			for (KnownDevice knownDev : _devMgr.getKnownDevices())
				notifyDeviceEvent(new DeviceEvent(EventType.ADD, knownDev));
		}
	}
	
	public void close() {
		_started = false;
		_devMgr.removeDeviceListener(this);
		synchronized(_matchingKnownDevs) {
			final Set<KnownDevice> matchingKnownDevs = new HashSet<KnownDevice>(_matchingKnownDevs);
			for (KnownDevice knownDev : matchingKnownDevs)
				notifyDeviceEvent(new DeviceEvent(EventType.REMOVE, knownDev));
		}
	}
	
	private void addMatchingDevice(KnownDevice device) {
		synchronized(_matchingKnownDevs) {
			_matchingKnownDevs.add(device);
		}
		
		notifyTrackerCustom(new DeviceEvent(EventType.ADD, device));
	}
	
	private void removeMatchingDevice(KnownDevice device) {
		synchronized(_matchingKnownDevs) {
			_matchingKnownDevs.remove(device);
		}
		
		notifyTrackerCustom(new DeviceEvent(EventType.REMOVE, device));
	}
	
	public Set<KnownDevice> getMatchingKnownDevices() {
		synchronized(_matchingKnownDevs) {
			return Collections.unmodifiableSet(_matchingKnownDevs);
		}
	}

	public DeviceDependencies getDependencies() {
		return _dependencies;
	}

	@Override
	public void notifyDeviceEvent(DeviceEvent event) {
		if (!event.isKnownDevice())
			return;
		
		KnownDevice dev = (KnownDevice) event.getDevice();
		boolean match = _dependencies.matches(dev);
			
		if (event.isAdd() && match) {
			addMatchingDevice(dev);
			return;
		} 
		
		synchronized (_matchingKnownDevs) {
			final boolean isOldMatching = _matchingKnownDevs.contains(dev);
			if (event.isRemove()) {
				if (isOldMatching)
					removeMatchingDevice(dev);
				
				return;
			} 
			
			if (event.isStateUpdate()) {
				if (!isOldMatching && match) {
					addMatchingDevice(dev);
				} else if (isOldMatching && !match) {
					removeMatchingDevice(dev);
				} else if (match) {
					notifyTrackerCustom(event);
				}
			}
		}
	}

	private void notifyTrackerCustom(DeviceEvent event) {
		if (_devTrackCustom == null)
			return;
		
		if (event.isAdd()) {
			_devTrackCustom.addDevice(event);
			return;
		}
		if (event.isRemove()) {
			_devTrackCustom.removeDevice(event);
			return;
		}
		if (event.isStateUpdate()) {
			_devTrackCustom.updateDevice(event);
			return;
		}
	}

	public boolean isOpened() {
		return _started;
	}
}
