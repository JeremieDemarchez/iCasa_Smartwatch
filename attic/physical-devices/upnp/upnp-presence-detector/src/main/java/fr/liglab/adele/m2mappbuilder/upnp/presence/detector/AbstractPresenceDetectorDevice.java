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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector;

import java.util.Dictionary;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.upnp.extra.util.UPnPEventNotifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;

public abstract class AbstractPresenceDetectorDevice implements UPnPDevice {

	private Dictionary dictionary = new Properties();

	private PresenceDetectorService presenceService;

	private GenericDeviceService medicalService;

	private PresenceSensor _presenceSensor;
	
	protected final BundleContext _context;
	
	private ServiceRegistration m_serviceRegistration;

	public AbstractPresenceDetectorDevice(BundleContext context) {
		_context = context;
	
	   presenceService = new PresenceDetectorService(_presenceSensor, isSimulated() );
	   medicalService = new GenericDeviceService();
   }

	public UPnPService getService(String serviceId) {
		if (serviceId.equals(presenceService.getId()))
			return presenceService;
		if (serviceId.equals(medicalService.getId()))
			return medicalService;
		return null;
	}

	public UPnPService[] getServices() {
		return new UPnPService[] { presenceService, medicalService };
	}

	public UPnPIcon[] getIcons(String locale) {
		return null;
	}

	public Dictionary getDescriptions(String locale) {
		return dictionary;
	}

	private void setupDeviceProperties() {
		dictionary = new Properties();
		if (mustBeNetworkExported())
			dictionary.put(UPnPDevice.UPNP_EXPORT, "");
		dictionary.put(org.osgi.service.device.Constants.DEVICE_CATEGORY,
				new String[] { UPnPDevice.DEVICE_CATEGORY });
		dictionary.put(UPnPDevice.FRIENDLY_NAME, getFriendlyName());
		dictionary.put(UPnPDevice.MANUFACTURER, getManufacturer());
		dictionary.put(UPnPDevice.MANUFACTURER_URL, getManufacterURL());
		dictionary.put(UPnPDevice.MODEL_DESCRIPTION, getModelDescription());
		dictionary.put(UPnPDevice.MODEL_NAME, getModelName());
		dictionary.put(UPnPDevice.MODEL_NUMBER, getModelNumber());
		dictionary.put(UPnPDevice.MODEL_URL, getModelURL());
		dictionary.put(UPnPDevice.SERIAL_NUMBER, getSerialNumber());
		dictionary.put(UPnPDevice.TYPE,
				"urn:schemas-upnp-org:device:detector-presence:1");
		dictionary.put(UPnPDevice.UDN, getDeviceId());
		dictionary.put(UPnPDevice.UPC, getUPC());
		// dictionary.put(UPnPService.TYPE, types.toArray(new String[]{}));
		dictionary.put(UPnPService.TYPE, new String[] {});
		dictionary.put(UPnPService.ID, new String[] {});
	}

	protected abstract String getManufacterURL();
	
	protected abstract String getModelNumber();

	protected abstract String getModelURL();

	protected abstract String getSerialNumber();

	protected abstract String getModelName();

	protected abstract String getManufacturer();

	protected abstract String getFriendlyName();

	protected abstract String getUPC();

	protected abstract String getModelDescription();

	protected boolean mustBeNetworkExported() {
		return false;
	}
	
	protected boolean isSimulated() {
		return false;
	}

	protected abstract String getDeviceId();

	private void buildEventNotifyer() {
		UPnPEventNotifier notifier = new UPnPEventNotifier(_context,
				this, presenceService);
		presenceService.setNotifier(notifier);
	}

	protected UPnPService getPresenceDetectorService() {
		return presenceService;
	}

	protected UPnPService getMedicalDeviceService() {
		return medicalService;
	}

	protected final PresenceSensor getPresenceSensor() {
		return _presenceSensor;
	}

	protected final void setPresenceSensor(PresenceSensor presenceSensor) {
		presenceService.setPresenceSensor(presenceSensor);
		this._presenceSensor = presenceSensor;
	}
	
	protected void start() {
		setupDeviceProperties();
		buildEventNotifyer();
		Dictionary desc = this.getDescriptions(null);
		m_serviceRegistration = _context.registerService(UPnPDevice.class.getName(), this, desc);
	}
	
	protected void stop() {
		presenceService.setNotifier(null);
		m_serviceRegistration.unregister();
	}

}
