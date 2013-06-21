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
package fr.liglab.adele.m2mappbuilder.upnp.photometer.proxy;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import fr.liglab.adele.icasa.device.DeviceEventType;
import fr.liglab.adele.icasa.device.DevicePropertyEvent;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.util.AbstractDevice;

public class UPnPPhotometerProxyImpl extends AbstractDevice implements Photometer {

	private UPnPDevice device;

	private UPnPService illuminanceService;

	private ServiceRegistration listenerRegistration;

	private BundleContext m_context;

	private String state;
	private String fault;
	private String m_serialNumber;
	private float illuminance = 0;

	public UPnPPhotometerProxyImpl(BundleContext context) {
		m_context = context;
		Dictionary value = null;
		illuminanceService = device.getService("urn:upnp-org:serviceId:Photometer");

		UPnPAction test = illuminanceService.getAction("GetIlluminance");
		try {
			value = test.invoke(null);
			setPropertyValue(PHOTOMETER_CURRENT_ILLUMINANCE, value.get("RetIlluminanceValue"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFault() {
		return fault;
	}

	public void setFault(String fault) {
		this.fault = fault;

	}

	public double getIlluminance() {
		Dictionary value = null;
		illuminanceService = device.getService("urn:upnp-org:serviceId:Photometer");

		UPnPAction test = illuminanceService.getAction("GetIlluminance");
		try {
			value = test.invoke(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		illuminance = (Float) value.get("RetIlluminanceValue");
		return illuminance;
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

			illuminanceService = device.getService("urn:upnp-org:serviceId:Photometer");

			m_serialNumber = (String) device.getDescriptions(null).get(UPnPDevice.UDN);
			registerListener();
		}

	}

	public void stop() {
		illuminanceService = null;
		listenerRegistration.unregister();

	}

	private void registerListener() {
		UPnPEventListenerImpl listener = new UPnPEventListenerImpl(this);
		String keys = "(UPnP.device.UDN=" + m_serialNumber + ")";
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

		private final UPnPPhotometerProxyImpl _device;

		public UPnPEventListenerImpl(UPnPPhotometerProxyImpl device) {
			_device = device;
		}

		@Override
		public void notifyUPnPEvent(String deviceId, String serviceId, Dictionary events) {
			System.out.println("+++++ Device ID: " + deviceId);
			System.out.println("+++++ Service ID: " + serviceId);
			float tempIlluminance = (Float) events.get("Illuminance");
			Runnable notificator = new Runnable() {
				@Override
				public void run() {
					Dictionary value = null;
					illuminanceService = device.getService("urn:upnp-org:serviceId:Photometer");

					UPnPAction test = illuminanceService.getAction("GetIlluminance");
					try {
						value = test.invoke(null);
						setPropertyValue(PHOTOMETER_CURRENT_ILLUMINANCE, value.get("RetIlluminanceValue"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					notifyListeners(new DevicePropertyEvent(_device, DeviceEventType.PROP_MODIFIED, PHOTOMETER_CURRENT_ILLUMINANCE,
					      (Float) value.get("RetIlluminanceValue"), (Float) value.get("RetIlluminanceValue")));
				}
			};

			Thread notificationThread = new Thread(notificator);
			notificationThread.start();
		}
	}

}
