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
package fr.liglab.adele.m2mappbuilder.presence.detector.upnp.exporter;

import java.util.Dictionary;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.m2mappbuilder.upnp.presence.detector.AbstractPresenceDetectorDevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.upnp.UPnPDevice;

@Component(name = "upnp.presence.detector", immediate=true)
public class PresenceDetectorUPNPDeviceProxy extends
		AbstractPresenceDetectorDevice {
	
	public static final Object SENSOR_SERVICE_ID_PROP = "presence.sensor.service.id";
	
	private static final String PRESENCE_SENSOR_CLASS_NAME = fr.liglab.adele.icasa.device.presence.PresenceSensor.class.getName();
	
	@Property(name = "presence.sensor.service.id", mandatory = true)
	private Long _presenceSensorServiceId;
	
	private String _serialNumber;
	
	public PresenceDetectorUPNPDeviceProxy(BundleContext context) {
		super(context);
	}

	@Override
	protected String getDeviceId() {
		return "uuid:388bf73f-a43f-43d3-83b0-680d0a000000";
	}

	private String getSerialNumberInternal() {
		if ((_serialNumber == null) && (getPresenceSensor() != null))
			_serialNumber = getPresenceSensor().getSerialNumber();
		
		return _serialNumber;
	}

	protected boolean mustBeNetworkExported() {
		return true;
	}

	@Override
	protected String getModelDescription() {
		return "X10 Presence Sensor";
	}

	@Override
	protected String getUPC() {
		return getDeviceId();
	}

	@Override
	protected String getFriendlyName() {
		return "X10 Presence Sensor";
	}

	@Override
	protected String getManufacturer() {
		return "Not Known";
	}

	@Override
	protected String getModelName() {
		return "MS13E";
	}

	@Override
	protected String getSerialNumber() {
		return getSerialNumberInternal();
	}

	@Override
	protected String getModelURL() {
		return "http://www.marmitek.com/fr/details-des-produits/automatisation-maison-securite/automatisation-maison-x-10/emetteurs-et-controleurs/ms13.php";
	}

	@Override
	protected String getModelNumber() {
		return "1.0";
	}

	@Override
	protected String getManufacterURL() {
		return "http://marmitek.com/fr";
	}
	
	@SuppressWarnings("unused")
	@Bind(aggregate = true, specification = "fr.liglab.adele.m2mappbuilder.device.presence.PresenceSensor")
	private void bindPresenceSensor(ServiceReference sref) {
		Object serviceId = sref.getProperty(Constants.SERVICE_ID);
		if (_presenceSensorServiceId.equals(serviceId)) {
			setPresenceSensor((PresenceSensor) _context.getService(sref));
		}
	}
	
	@SuppressWarnings("unused")
	@Unbind(aggregate = true, specification = "fr.liglab.adele.m2mappbuilder.device.presence.PresenceSensor")
	private void unbindPresenceSensor(ServiceReference sref) {
		Object serviceId = sref.getProperty(Constants.SERVICE_ID);
		if (_presenceSensorServiceId.equals(serviceId)) {
			setPresenceSensor(null);
			_context.ungetService(sref);
		}
	}
	
	@Validate
	protected void start() {
		super.start();
	}
	
	@Invalidate
	protected void stop() {
		super.stop();
	}
}
