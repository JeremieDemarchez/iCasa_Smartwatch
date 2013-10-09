/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.m2mappbuilder.upnp.settopbox.proxy;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Properties;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.settopbox.SetTopBox;
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

public class UPnPSetTopBoxProxyImpl extends AbstractDevice implements PresenceSensor, SetTopBox, SimulatedDevice,
      ZoneListener {

	private UPnPDevice device;

	private ServiceRegistration listenerRegistration;

	private BundleContext m_context;
	
	private String state;
	private String fault;
    private String m_serialNumber;
	private Boolean presenceSensed = true;   

	private volatile SimulatedEnvironment m_env;

	public UPnPSetTopBoxProxyImpl(BundleContext context) {
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
				UPnPStateVariable[] variables = uPnPService.getStateVariables();
			}
			
			m_serialNumber = (String) device.getDescriptions(null).get(UPnPDevice.UDN);
			registerListener();
		}
		
	}

	public void stop() {
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

	@Override
	public boolean isStarted() {
		return true;
	}
}
