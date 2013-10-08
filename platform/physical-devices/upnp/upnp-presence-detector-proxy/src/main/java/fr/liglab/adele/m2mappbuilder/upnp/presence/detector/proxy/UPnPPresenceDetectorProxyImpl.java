/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector.proxy;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Properties;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
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
import fr.liglab.adele.icasa.environment.ZoneListener;

public class UPnPPresenceDetectorProxyImpl extends AbstractDevice implements PresenceSensor, SimulatedDevice,
      ZoneListener {

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
		UPnPEventListenerImpl listener = new UPnPEventListenerImpl(this);
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
	public void setState(String state) {
		this.state = state;
   }

	@Override
   public String getState() {
   	return state;
   }
	
	class UPnPEventListenerImpl implements UPnPEventListener {

        private final UPnPPresenceDetectorProxyImpl _device;

        public UPnPEventListenerImpl(UPnPPresenceDetectorProxyImpl device) {
            _device = device;
        }

		@Override
		public void notifyUPnPEvent(String deviceId, String serviceId, Dictionary events) {
			System.out.println("+++++ Device ID: " + deviceId);
			System.out.println("+++++ Service ID: " + serviceId);
			Boolean temp = (Boolean) events.get("DetectedPresence");
			if (temp!=null) {
                final boolean oldPresencedSensed = presenceSensed;
				presenceSensed = temp;

				Runnable notificator = new Runnable() {					
					@Override
					public void run() {
						notifyListeners(new DeviceEvent(_device, DeviceEventType.PROP_MODIFIED, PRESENCE_SENSOR_SENSED_PRESENCE, oldPresencedSensed));
					}
				};
				
				Thread notificationThread = new Thread(notificator);
				notificationThread.start();
				
			}
		}		
	}

    public void zoneAdded(fr.liglab.adele.icasa.environment.Zone zone) {
        // do nothing
    }

    public void zoneRemoved(fr.liglab.adele.icasa.environment.Zone zone) {
        // do nothing
    }

    public void zoneMoved(fr.liglab.adele.icasa.environment.Zone zone, fr.liglab.adele.icasa.environment.Position position) {
        // do nothing
    }

    public void zoneResized(fr.liglab.adele.icasa.environment.Zone zone) {
        // do nothing
    };

    public void zoneParentModified(fr.liglab.adele.icasa.environment.Zone zone, fr.liglab.adele.icasa.environment.Zone zone1) {
        // do nothing
    }

    public void zoneVariableAdded(fr.liglab.adele.icasa.environment.Zone zone, java.lang.String s) {
        // do nothing
    }

    public void zoneVariableRemoved(fr.liglab.adele.icasa.environment.Zone zone, java.lang.String s) {
        // do nothing
    }

    public void zoneVariableModified(fr.liglab.adele.icasa.environment.Zone zone, java.lang.String s, java.lang.Object o) {
        // do nothing
    }
}
