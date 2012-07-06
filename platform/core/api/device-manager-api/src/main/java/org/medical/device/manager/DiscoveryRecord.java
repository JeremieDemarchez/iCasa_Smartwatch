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
package org.medical.device.manager;

import java.util.List;

import org.osgi.service.remoteserviceadmin.EndpointDescription;

/**
 * Represents a discovery record.
 * 
 * @author Thomas Leveque
 *
 */
public class DiscoveryRecord {

	private EndpointDescription discoveryRecord;
	private List<DeviceProxy> proxies;
	
	public DiscoveryRecord(EndpointDescription discoveryRecord,
			List<DeviceProxy> proxies) {
		super();
		this.discoveryRecord = discoveryRecord;
		this.proxies = proxies;
	}

	public final EndpointDescription getDiscoveryRecord() {
		return discoveryRecord;
	}

	public final List<DeviceProxy> getProxies() {
		return proxies;
	}
	
	
}
