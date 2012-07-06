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
package org.medical.upnp.presence.detector.proxy;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Properties;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulatedEnvironmentListener;

public class UPnPPresenceDetectorProxyImpl extends AbstractDevice implements PresenceSensor, SimulatedDevice,
      SimulatedEnvironmentListener {

	private UPnPDevice device;
	private UPnPService presenceService;
	private UPnPService medicalService;

	private ServiceRegistration listenerRegistration;

	private BundleContext m_context;
	
	private String state;
	private String fault;
   private String m_serialNumber;
	private Boolean presenceSensed = true;   

	private volatile SimulatedEnvironment m_env;

	public UPnPPresenceDetectorProxyImpl(BundleContext context) {
		m_context = context;
	}

	public String getFault() {
		return fault;
	}

	public void setFault(String fault) {
		this.fault = fault;

	}

	public boolean getSensedPresence() {
		return presenceSensed;
	}

	public String getLocation() {
      return getEnvironmentId();
	}

	@Override
	public String getSerialNumber() {
      return m_serialNumber;
	}

	public void start() {
		if (device != null) {
			UPnPService[] services = device.getServices();
			for (UPnPService uPnPService : services) {
				System.out.println("---- UPnP Service " + uPnPService.getId());
				UPnPStateVariable[] variables = uPnPService.getStateVariables();
			}
			presenceService = device.getService("urn:schemas-upnp-org:serviceId:presence:1");
			medicalService = device.getService("urn:schemas-upnp-org:serviceId:medical-device:1");
			
			
			m_serialNumber = (String)device.getDescriptions(null).get(UPnPDevice.UDN);
			registerListener();
		}
		
	}

	public void stop() {
		presenceService = null;
		medicalService = null;
		listenerRegistration.unregister();

	}

	private void registerListener() {
		UPnPEventListenerImpl listener = new UPnPEventListenerImpl();
		String keys = "(UPnP.device.UDN="+ m_serialNumber + ")";
		try {
			Filter filter = m_context.createFilter(keys);
			Properties props = new Properties();
			props.put(UPnPEventListener.UPNP_FILTER, filter);
			listenerRegistration = m_context.registerService(UPnPEventListener.class.getName(), listener, props);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
		
	@Override
	public void environmentPropertyChanged(String arg0, Double arg1, Double arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void bindSimulatedEnvironment(SimulatedEnvironment environment) {
		m_env = environment;
		m_env.addListener(this);
	}

	@Override
	public String getEnvironmentId() {
      return m_env != null ? m_env.getEnvironmentId() : null;
	}

	@Override
	public synchronized void unbindSimulatedEnvironment(SimulatedEnvironment arg0) {
      m_env.removeListener(this);
      m_env = null;

	}
	
	@Override
	public void setState(String state) {
		this.state = state;
   }

	@Override
   public String getState() {
   	return state;
   }
	
	class UPnPEventListenerImpl implements UPnPEventListener {

		@Override
		public void notifyUPnPEvent(String deviceId, String serviceId, Dictionary events) {
			System.out.println("+++++ Device ID: " + deviceId);
			System.out.println("+++++ Service ID: " + serviceId);
			Boolean temp = (Boolean) events.get("DetectedPresence");
			if (temp!=null) {
				presenceSensed = temp;
								
				Runnable notificator = new Runnable() {					
					@Override
					public void run() {
						notifyListeners();						
					}
				};
				
				Thread notificationThread = new Thread(notificator);
				notificationThread.start();
				
			}
		}		
	}
}
