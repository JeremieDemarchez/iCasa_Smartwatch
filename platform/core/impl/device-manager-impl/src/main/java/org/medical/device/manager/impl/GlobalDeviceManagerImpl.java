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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.util.Logger;
import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DependRegistration;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.DeviceExporter;
import org.medical.device.manager.DeviceRequest;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.ServPropManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * This component is the coordinator of all device access and exposed services.
 * 
 * @author Thomas Leveque
 *
 */
@Instantiate(name="global-device-manager-1")
@Component(name="global-device-manager")
@Provides(specifications=GlobalDeviceManager.class)
public class GlobalDeviceManagerImpl implements GlobalDeviceManager, DeviceListener, StateVariableListener {
	
	@Requires
	private ApplicationManager _appMgr;
	
	@Requires
	private PolicyManager _policyMgr;
	
	@Requires(optional=true)
	private DeviceExporter[] _exporters; //TODO should manage removal of exporters
	
	private Logger _log;
	
	/**
	 * Build on demand, represents a cache of mapping between device proxies accessible to application and their representation.
	 */
	//TODO manage cleaning of this cache
	private final Map<ApplicationDevice /* protected device */, ApplicationDevice /* unprotected device */> _appDevs = new HashMap<ApplicationDevice, ApplicationDevice>();
	
	private final Map<String, KnownDevice> _knownDevs = new HashMap<String, KnownDevice>();
	
	private final Map<String, AvailableDevice> _availableDevs = new HashMap<String, AvailableDevice>();
	
	private final Map<Application, List<DependRegistrationImpl>> _appDeps = new HashMap<Application, List<DependRegistrationImpl>>();

	private final BundleContext _context;

	private Map<ApplicationDevice, List<ServiceRegistration>> _appDevRegistServs = new HashMap<ApplicationDevice, List<ServiceRegistration>>();
	
	private Map<ApplicationDevice, List<ServiceRegistration>> _devproxyRegistServs = new HashMap<ApplicationDevice, List<ServiceRegistration>>();
	
	private final List<DeviceListener> _devListeners = new Vector<DeviceListener>();
	
	private GlobalDeviceManagerImpl(BundleContext context) {
		_context = context;
		_log = new Logger(_context, "icasa.device.manager");
	}
	
	@Override
	public List<ApplicationDevice> getDeviceDescriptions(DeviceRequest request, Application app) {
		if (request == null) {
			request = new DeviceRequest();
			request.requiresAll().includes().all();
		}
		
		DependRegistrationImpl depReg = new DependRegistrationImpl(request, app, this);
		depReg.start();
		final List<ApplicationDevice> resolvedDevices = depReg.getResolvedDevices();
		depReg.stop();
		
		return resolvedDevices;
	}

	public ApplicationDevice createApplicationDevice(Application app,
			KnownDevice knownDev) {
		ApplicationDevice appDev = _policyMgr.createApplicationDevice((KnownDeviceImpl) knownDev, app);
		boolean useUnprotected = _policyMgr.canGiveUnprotectedDevTo(app);
		ApplicationDevice protectedAppDev = null;
		if (!useUnprotected)
			protectedAppDev = appDev.getProtectedDevice();
		
		List<ServiceRegistration> registeredServs = new ArrayList<ServiceRegistration>();
		ServiceRegistration registAppDev = _context.registerService(ApplicationDevice.class.getName(), 
				useUnprotected ? appDev : protectedAppDev, 
				new Hashtable());
		registeredServs.add(registAppDev);
		
		_appDevRegistServs.put(appDev, registeredServs);
		
		notifyDeviceEvent(new DeviceEvent(EventType.ADD, appDev));
		if (useUnprotected)
			return appDev;
		
		notifyDeviceEvent(new DeviceEvent(EventType.ADD, protectedAppDev)); //TODO check that it is required
		return protectedAppDev;
	}

	public List<ApplicationDevice> getApplicationDevices(Application app) {
		List<ApplicationDevice> devices = new ArrayList<ApplicationDevice>();
		
		List<KnownDevice> knwonDevices = getKnownDevices();
		for (KnownDevice dev : knwonDevices) {
			if (app == null) {
				devices.addAll(dev.getApplicationDevices());
			} else {
				ApplicationDevice visibleDev = dev.getApplicationDevice(app);
				if (visibleDev != null)
					devices.add(visibleDev);
			}
		}
		
		return devices;
	}
	
	private List<DependRegistration> getAppDependencies(Application app) {
		List<DependRegistration> deps = new ArrayList<DependRegistration>();
		synchronized (_appDeps) {
			deps.addAll(_appDeps.get(app));
		}
		
		return deps;
	}

	public List<KnownDevice> getKnownDevices() {
		return new ArrayList<KnownDevice>(_knownDevs.values());
	}

	@Override
	public DependRegistration addDependencies(DeviceDependencies appDeps, Application app) {
		DependRegistrationImpl depReg = new DependRegistrationImpl(appDeps, app, this);
		return addDependenciesInternal(app, depReg);
	}

	private DependRegistrationImpl addDependenciesInternal(Application app,
			DependRegistrationImpl depReg) {
		List<DependRegistrationImpl> deps = null;
		synchronized (_appDeps) {
			deps = _appDeps.get(app);
			if (deps == null) {
				deps = new ArrayList<DependRegistrationImpl>();
				_appDeps.put(app, deps);
			}
			deps.add(depReg);
			
			depReg.start();
		}
		_log.log(Logger.DEBUG, "The device dependency " + depReg.getDependencies().toString() + " has been registered.");
		
		return depReg;
	}

	@Bind(optional=true, aggregate=true)
	private void bindDevice(ProvidedDevice device) {
		notifyDeviceEvent(new DeviceEvent(EventType.ADD, device));
	}
	
	@Unbind(optional=true, aggregate=true)
	private void unbindDevice(ProvidedDevice device) {
		notifyDeviceEvent(new DeviceEvent(EventType.REMOVE, device));
	}
	
	@Validate
	private void start() {
		_log.log(Logger.INFO, "The device manager is starting.");
		//TODO implement persistency
		_log.log(Logger.INFO, "The device manager is started.");
	}
	
	@Invalidate
	private void stop() {
		_log.log(Logger.INFO, "The device manager is stoping.");
		//TODO implement persistency
		synchronized (_appDeps) {
			// copy list to avoid comodification errors
			List<List<DependRegistrationImpl>> appDepRegs = new ArrayList<List<DependRegistrationImpl>>();
			for (List<DependRegistrationImpl> depRegs : _appDeps.values()) {
				appDepRegs.add(new ArrayList<DependRegistrationImpl>(depRegs));
			}
			for (List<DependRegistrationImpl> depRegs : appDepRegs) {
				if (depRegs == null)
					continue;
				
				for (DependRegistration depReg : depRegs)
					depReg.unregister();
			}
		}
		
		synchronized(_availableDevs) {
			for (KnownDevice knownDev : _knownDevs.values())
				notifyDeviceEvent(new DeviceEvent(EventType.REMOVE,
						knownDev));
			
			_knownDevs.clear();
			_appDevs.clear();
			_availableDevs.clear();
			_appDeps.clear();
			_appDevRegistServs.clear();
			_devproxyRegistServs.clear();
			_devListeners.clear();
		}
		_log.log(Logger.INFO, "The device manager is stoped.");
	}

	@Override
	public List<AvailableDevice> getAvailableDevices() {
		return new ArrayList<AvailableDevice>(_availableDevs.values());
	}

	@Override
	public ApplicationDevice getUnProtectedApplicationDevice(ApplicationDevice dev) {
		if (!dev.isProtected())
			return dev;
		
		String deviceId = dev.getId();
		final ApplicationDevice appDev = _appDevs.get(dev);
		if (appDev != null)
			return appDev;
		
		KnownDevice knownDev = _knownDevs.get(deviceId);
		if (knownDev == null)
			return null;

		for (ApplicationDevice curAppDev : knownDev.getApplicationDevices()) {
			if (curAppDev.getProtectedDevice() == dev) {
				_appDevs.put(dev, curAppDev);
				return curAppDev;
			}
		}
		
		return null;
	}

	public void removeDependencies(DependRegistrationImpl depReg) {
		List<DependRegistrationImpl> deps = null;
		Application app = depReg.getApplication();
		synchronized (_appDeps) {
			deps = _appDeps.get(app);
			if (deps != null) {
				deps.remove(depReg);
			}
			depReg.stop();
		}
		_log.log(Logger.DEBUG, "The device dependency " + depReg.getDependencies().toString() + " has been unregistered.");
	}

	public Application getApplication(String appId) {
		return _appMgr.getApplication(appId);
	}

	public void updatesDependencies(
			DependRegistrationImpl depReg) {
		Application app = depReg.getApplication();
		addDependenciesInternal(app, depReg);
	}
	
	
	
	public void notifyDeviceEvent(DeviceEvent event) {
		_log.log(Logger.DEBUG, "The device event " + event.toString() + " has been sent.");
		
		Device device = event.getDevice();
		String id = device.getId();
		// provided device
		if (event.isProvidedDevice()) {
			if (event.isAdd()) {
				synchronized (_availableDevs) {
					AvailableDeviceImpl availableDev = new AvailableDeviceImpl(
							(ProvidedDevice) device);
					
					notifyDeviceEvent(new DeviceEvent(EventType.ADD,
							availableDev));
				}
				
				notifyDeviceListeners(event);
				return;
			} else if (event.isRemove()) {
				synchronized (_availableDevs) {
					AvailableDevice availableDev = _availableDevs.get(id);
					
					notifyDeviceEvent(new DeviceEvent(EventType.REMOVE,
							availableDev));
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isUpdate()) {
				notifyDeviceListeners(event);
				return;
			}
			return;
		}
		
		// available device
		if (event.isAvailableDevice()) {
			AvailableDeviceImpl availableDev = (AvailableDeviceImpl) device;
			if (event.isAdd()) {
				synchronized (_availableDevs) {
					_availableDevs.put(id, availableDev);
					
					KnownDeviceImpl knownDevice = (KnownDeviceImpl) _knownDevs.get(id);
					if (knownDevice != null) {
						knownDevice.addAvailableDevice((AvailableDeviceImpl) device);
						notifyDeviceEvent(new DeviceEvent(EventType.STATE_UPDATE,
								knownDevice));
						return;
					}
						
					knownDevice = new KnownDeviceImpl((AvailableDeviceImpl) device);
					_knownDevs.put(id, knownDevice);
					
					notifyDeviceEvent(new DeviceEvent(EventType.ADD,
							knownDevice));
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isRemove()) {
				synchronized (_availableDevs) {
					_availableDevs.remove(id);

					KnownDeviceImpl knownDev = (KnownDeviceImpl) _knownDevs
							.get(id);
					knownDev.removeAvailableDevice();
					notifyDeviceEvent(new DeviceEvent(EventType.STATE_UPDATE,
							knownDev));
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isUpdate()) {
				notifyDeviceListeners(event);
				return;
			}
			return;
		}
		
		// known device
		if (event.isKnownDevice()) {
			KnownDeviceImpl knownDev = (KnownDeviceImpl) device;
			if (event.isAdd()) {
				synchronized (_availableDevs) {
					_knownDevs.put(id, knownDev);
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isRemove()) {
				synchronized (_availableDevs) {
					_knownDevs.remove(id);

					knownDev.removeAvailableDevice();
					for (ApplicationDevice appDev : knownDev.getApplicationDevices()) {
						knownDev.removeApplicationDevice(appDev.getApplication());
						notifyDeviceEvent(new DeviceEvent(EventType.REMOVE,
								appDev));
					}
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isUpdate()) {
				notifyDeviceListeners(event);
				return;
			}
			return;
		}
		
		// application device
		if (event.isApplicationDevice()) {
			ApplicationDevice appDev = (ApplicationDevice) device;
			if (event.isAdd()) {
				synchronized (_availableDevs) {
					// do nothing
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isRemove()) {
				synchronized (_availableDevs) {
					// unregister application devices
					List<ServiceRegistration> servRegs = _appDevRegistServs.remove(appDev);
					for (ServiceRegistration servReg : servRegs)
							servReg.unregister();
					
					for (Object devProxy : appDev.getDeviceProxies())
						notifyDeviceEvent(new DeviceEvent(EventType.REMOVE, appDev, devProxy));
				}
				notifyDeviceListeners(event);
				return;
			} else if (event.isUpdate()) {
				notifyDeviceListeners(event);
				return;
			}
			return;
		}
		
		// device proxy
		if (event.isDeviceProxy()) {
			ApplicationDeviceImpl appDev = (ApplicationDeviceImpl) device;
			Object devProxy = event.getProxy();
			if (event.isAdd()) {
				notifyDeviceListeners(event);
				return;
			} else if (event.isRemove()) {
				appDev.removeProxy(devProxy);
				
				// unregister device proxies
				List<ServiceRegistration> proxyServRegs = _devproxyRegistServs.remove(appDev);
				for (ServiceRegistration servReg : proxyServRegs)
						servReg.unregister();
				
				notifyDeviceListeners(event);
				return;
			} else if (event.isUpdate()) {
				notifyDeviceListeners(event);
				return;
			}
			return;
		}
	}

	private void notifyDeviceListeners(DeviceEvent event) {
		synchronized(_devListeners) {
			for (DeviceListener listener : _devListeners)
				listener.notifyDeviceEvent(event);
		}
	}

	public DeviceExporter[] getDeviceExporters() {
		return _exporters;
	}
	
	public void addDeviceListener(DeviceListener listener) {
		_devListeners.add(listener);
	}
	
	public void removeDeviceListener(DeviceListener listener) {
		_devListeners.remove(listener);
	}

	public Object createDeviceProxy(ApplicationDevice appDev, Class... interfaces) {
		Object devProxy = null;
		DeviceExporter usedExporter = null;
		for (DeviceExporter exporter : _exporters) {
			if (!exporter.canExportTo(appDev, interfaces))
				continue;
			
			devProxy = exporter.createsProxy(appDev, interfaces);
			if (devProxy != null) {
				usedExporter = exporter;
				break;
			}
		}
		if (devProxy == null)
			return null;
		
		appDev = getUnProtectedApplicationDevice(appDev);
		((ApplicationDeviceImpl) appDev).addProxy(devProxy);
		
		List<ServiceRegistration> registeredServs = new ArrayList<ServiceRegistration>();
		for (Class interf : interfaces) {
			ServPropManager propMgr = usedExporter.getCustomServicePropManager();
			Properties props = new Properties();
			if (propMgr != null) {
				try {
					propMgr.registerService(appDev, devProxy, props);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ServiceRegistration registAppDev = _context.registerService(interf.getName(), devProxy, props);
			registeredServs.add(registAppDev);
		}
		
		_devproxyRegistServs.put(appDev, registeredServs);
		
		notifyDeviceEvent(new DeviceEvent(EventType.ADD, appDev, devProxy));
		
		return devProxy;
	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue, Object source) {
		if (source == null)
			return; // ignore it
		
		if (source instanceof Device) {
			notifyDeviceEvent(new DeviceEvent(EventType.STATE_UPDATE, (Device) source));
			return;
		}
	}

	@Override
	public void addVariable(StateVariable variable, Object source) {
		if (source instanceof Device) {
			notifyDeviceEvent(new DeviceEvent(EventType.STRUCTURE_UPDATE, (Device) source));
			return;
		}
	}

	@Override
	public void removeVariable(StateVariable variable, Object source) {
		if (source instanceof Device) {
			notifyDeviceEvent(new DeviceEvent(EventType.STRUCTURE_UPDATE, (Device) source));
			return;
		}
	}

	public void notifyNoMoreUse(DependRegistrationImpl depReg,
			ApplicationDevice device) {
		synchronized (_appDeps) {
			for (Application app : _appDeps.keySet()) {
				List<DependRegistrationImpl> depRegs = _appDeps.get(app);
				for (DependRegistrationImpl curDepReg : depRegs) {
					// TODO should compute if we need to keep device proxies
					
					if (curDepReg.getId().equals(depReg.getId()))
						continue;
					
					if (curDepReg.isRegistered() && curDepReg.uses(device))
						return;
				}
			}
			
			notifyDeviceEvent(new DeviceEvent(EventType.REMOVE, device));
		}
	}
}

