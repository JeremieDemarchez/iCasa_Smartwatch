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
package fr.liglab.adele.m2mappbuilder.upnp.binary.light.proxy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import fr.liglab.adele.icasa.device.DevicePropertyEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;

public class UPnPBinaryLightProxyImpl extends AbstractDevice implements BinaryLight {

	private UPnPDevice device;
	private UPnPService statusService;

	private ServiceRegistration listenerRegistration;

	private BundleContext m_context;

	private double maxPowerLevel = 100.0;
	private String state;
	private String fault;
	private String m_serialNumber;
	private boolean status = false;

	public UPnPBinaryLightProxyImpl(BundleContext context) {
		m_context = context;

		Dictionary value = null;
		statusService = device.getService("urn:upnp-org:serviceId:SwitchPower");

		UPnPAction test = statusService.getAction("GetStatus");
		try {
			value = test.invoke(null);
			status = (Boolean) value.get("ResultStatus");
			setPropertyValue(BINARY_LIGHT_POWER_STATUS, status);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		setPropertyValue(LIGHT_POWER_STATUS, status);
		setPropertyValue(BINARY_LIGHT_MAX_POWER_LEVEL, maxPowerLevel);
	}

	public String getFault() {
		return fault;
	}

	public void setFault(String fault) {
		this.fault = fault;

	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	public void start() {
		if (device != null) {
			UPnPService[] services = device.getServices();
			for (UPnPService uPnPService : services) {
//				System.out.println("---- UPnP Service " + uPnPService.getId());
				UPnPStateVariable[] variables = uPnPService.getStateVariables();
			}

			statusService = device.getService("urn:upnp-org:serviceId:SwitchPower");
			m_serialNumber = (String)device.getDescriptions(null).get(UPnPDevice.UDN);
			registerListener();
		}

	}

	public void stop() {
		statusService = null;
		listenerRegistration.unregister();

	}

	private void registerListener() {
		UPnPEventListenerImpl listener = new UPnPEventListenerImpl(this);
		String keys = "(UPnP.device.UDN="+ m_serialNumber + ")";
		try {
			Filter filter = m_context.createFilter(keys);
			Dictionary props = new Properties();
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

		private final UPnPBinaryLightProxyImpl _device;

		public UPnPEventListenerImpl(UPnPBinaryLightProxyImpl device) {
			_device = device;
		}

		@Override
		public void notifyUPnPEvent(String deviceId, String serviceId, Dictionary events) {
			Runnable notificator = new Runnable() {					
				@Override
				public void run() {
					Dictionary value = null;
					statusService = device.getService("urn:upnp-org:serviceId:SwitchPower");

					UPnPAction test = statusService.getAction("GetStatus");
					try {
						value = test.invoke(null);
						status = (Boolean) value.get("ResultStatus");
						setPropertyValue(BINARY_LIGHT_POWER_STATUS, status);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					notifyListeners(new DevicePropertyEvent(_device, DeviceEventType.PROP_MODIFIED, BINARY_LIGHT_POWER_STATUS, status, status));
				}
			};

			Thread notificationThread = new Thread(notificator);
			notificationThread.start();
		}		
	}

	@Override
	public double getMaxPowerLevel() {
		return maxPowerLevel;
	}

	@Override
	public boolean getPowerStatus() {
		return status;
	}

	@Override
	public boolean setPowerStatus(boolean newStatus) {

		Dictionary<String, Boolean> value = new Hashtable<String, Boolean>();
		value.put("NewTargetValue", newStatus);
		statusService = device.getService("urn:upnp-org:serviceId:SwitchPower");

		UPnPAction test = statusService.getAction("SetTarget");

		try {
			test.invoke(value);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	@Override
   public void turnOn() {
		setPowerStatus(true);	   
   }

	@Override
   public void turnOff() {
		setPowerStatus(false);	   
   }
}
