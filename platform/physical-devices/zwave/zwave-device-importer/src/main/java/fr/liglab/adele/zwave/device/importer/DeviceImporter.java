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
package fr.liglab.adele.zwave.device.importer;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.provider.Creator;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;



import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.component.ImporterIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Component
@Provides(specifications = {ImporterService.class,ImporterIntrospection.class})
public class DeviceImporter extends AbstractImporterComponent  {

	public enum SupportedDeviceType {
		
		FibaroGFMS001 {
			
			public String getDescription() {
				return "Fibaro Multi Sensor";
			};
			
			public boolean matches(DeviceDeclaration importDeclaration) {
				
				int manufactererId 	= importDeclaration.getZwaveManufacturerId();
				int deviceType 		= importDeclaration.getzwaveDeviceType();
				int deviceId 		= importDeclaration.getZwaveDeviceId();
				
				return (manufactererId == 0x10F) &&
					   (deviceType == 0x0800 || deviceType == 0x0801) &&
					   (deviceId == 0x1001 || deviceId == 0x2001 || (deviceId == 0x3001 && deviceType == 0x0800));
				
			}
		},
		
		FibaroFGWPE101 {

			public String getDescription() {
				return "Fibaro Wall Plug";
			};
			
			public boolean matches(DeviceDeclaration importDeclaration) {
				
				int manufactererId 	= importDeclaration.getZwaveManufacturerId();
				int deviceType 		= importDeclaration.getzwaveDeviceType();
				int deviceId 		= importDeclaration.getZwaveDeviceId();
				
				return (manufactererId == 0x10F) &&
					   (deviceType == 0x0600) &&
					   (deviceId == 0x1000);
				
			}
			
		},
		
		FibaroFGK101 {
			
			public String getDescription() {
				return "Fibaro Door Window Sensor";
			};
			
			public boolean matches(DeviceDeclaration importDeclaration) {

				int manufactererId 	= importDeclaration.getZwaveManufacturerId();
				int deviceType 		= importDeclaration.getzwaveDeviceType();
				int deviceId 		= importDeclaration.getZwaveDeviceId();
				
				return (manufactererId == 0x10F) &&
					   (deviceType == 0x0700) &&
					   (deviceId == 0x1000 || deviceId == 0x2000 || deviceId == 0x3000);				
			}
		},
		
		
		FibaroFGSD002 {
			
			public String getDescription() {
				return "Fibaro Smoke Sensor";
			};
			
			public boolean matches(DeviceDeclaration importDeclaration) {
				
				int manufactererId 	= importDeclaration.getZwaveManufacturerId();
				int deviceType 		= importDeclaration.getzwaveDeviceType();
				int deviceId 		= importDeclaration.getZwaveDeviceId();
				
				return (manufactererId == 0x10F) &&
					   (deviceType == 0x0C02) &&
					   (deviceId == 0x1002);				
			}
		};
	
		/**
		 * The description of the device type
		 */
		public abstract String  getDescription();

		/**
		 * Whether this type matches the import declaration
		 */
		public abstract boolean matches(DeviceDeclaration importDeclaration);
		
		/**
		 * Get the supported device type, if any, matching a giving declaration
		 */
		public static final SupportedDeviceType getMatching(DeviceDeclaration importDeclaration) {
		
			for (SupportedDeviceType deviceType : SupportedDeviceType.values()) {
				if (deviceType.matches(importDeclaration)) {
					return deviceType;
				}
			}
			
			return null;
		}
		
	}
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DeviceImporter.class);

	private final Map<SupportedDeviceType, Creator.Entity<? extends ZwaveDevice>> openhabCreators; 
	
	@Creator.Field Creator.Entity<fr.liglab.adele.zwave.device.proxies.openhab.FibaroMotionSensor> 		openhabMotionSensorCreator;

	@Creator.Field Creator.Entity<fr.liglab.adele.zwave.device.proxies.openhab.FibaroWallPlug> 			openhabWallPlugCreator;

	@Creator.Field Creator.Entity<fr.liglab.adele.zwave.device.proxies.openhab.FibaroSmokeSensor> 		openhabSmokeSensorCreator;

	@Creator.Field Creator.Entity<fr.liglab.adele.zwave.device.proxies.openhab.FibaroDoorWindowSensor> 	openhabDoorWindowSensorCreator;


	
	private final Map<SupportedDeviceType, Creator.Entity<? extends ZwaveDevice>> zwave4jCreators; 

	@Creator.Field Creator.Entity<fr.liglab.adele.zwave.device.proxies.zwave4j.FibaroMotionSensor> 		zwave4jMotionSensorCreator;
			
	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	@ServiceProperty(name = "target", value = "(&(scope=generic)(zwave.device.manufacturer.id=*)(zwave.device.id=*)(zwave.node.id=*)(zwave.device.type.id=*))")
	private String filter;

	public DeviceImporter(BundleContext context) {
		
		openhabCreators = new HashMap<>();
		
		openhabCreators.put(SupportedDeviceType.FibaroGFMS001,openhabMotionSensorCreator);
		openhabCreators.put(SupportedDeviceType.FibaroFGWPE101,openhabWallPlugCreator);
		openhabCreators.put(SupportedDeviceType.FibaroFGK101,openhabDoorWindowSensorCreator);
		openhabCreators.put(SupportedDeviceType.FibaroFGSD002,openhabSmokeSensorCreator);
		
		zwave4jCreators = new HashMap<>();

		zwave4jCreators.put(SupportedDeviceType.FibaroGFMS001,zwave4jMotionSensorCreator);
		
	}

	@Validate
	protected void start() {
		super.start();
	}

	@Invalidate
	protected void stop() {
		super.stop();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
		
		
		if (library == null) {
			LOG.warn("Zwave library"+ libraryName +" not supported by iCasa");
			return;
		}

		Map<SupportedDeviceType, Creator.Entity<? extends ZwaveDevice>> creators = null;
		if 	(library == ZWaveLibrary.openhab) {
			creators	= openhabCreators;
		}
		else if (library == ZWaveLibrary.zwave4j) {
			creators	= zwave4jCreators;
		}

		if (creators == null) {
			LOG.warn("Zwave library"+ libraryName +" not supported by iCasa");
			return;
		}
		
		DeviceDeclaration deviceDeclaration = new DeviceDeclaration(importDeclaration);
		SupportedDeviceType deviceType		= SupportedDeviceType.getMatching(deviceDeclaration);

		if (deviceType == null) {
			LOG.warn("Zwave device type for node "+ deviceDeclaration.getZwaveNodeId() +" not supported by iCasa");
			return;
		}
		
		Creator.Entity<? extends ZwaveDevice> creator = creators.get(deviceType);

		if (creator == null){
			LOG.warn("Zwave device type for node "+ deviceDeclaration.getZwaveNodeId() +" not supported by iCasa using library "+library);
			return;
		}

		LOG.info(" Creating ZWave device : "+deviceType.getDescription()+ " node "+deviceDeclaration.getZwaveNodeId());
		
		Map<String,Object> properties = new HashMap<>();
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.HOME_ID),deviceDeclaration.getZwaveHomeId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.NODE_ID),deviceDeclaration.getZwaveNodeId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.DEVICE_ID),deviceDeclaration.getZwaveDeviceId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.DEVICE_TYPE),deviceDeclaration.getzwaveDeviceType());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.MANUFACTURER_ID),deviceDeclaration.getZwaveManufacturerId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.NEIGHBORS),new ArrayList<>());
		properties.put(ContextEntity.State.ID(GenericDevice.class,GenericDevice.DEVICE_SERIAL_NUMBER),"ZwaveDevice#"+deviceDeclaration.getZwaveNodeId());

		creator.create("ZwaveDevice#"+deviceDeclaration.getZwaveNodeId(),properties);
	}

	@Override
	protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		String libraryName = (String) importDeclaration.getMetadata().get("library");
		ZWaveLibrary library = ZWaveLibrary.valueOf(libraryName);
		
		if (library == null) {
			LOG.warn("Zwave library"+ libraryName +" not supported by iCasa");
			return;
		}

		Map<SupportedDeviceType, Creator.Entity<? extends ZwaveDevice>> creators = null;
		if 	(library == ZWaveLibrary.openhab) {
			creators	= openhabCreators;
		}
		else if (library == ZWaveLibrary.zwave4j) {
			creators	= zwave4jCreators;
		}

		if (creators == null) {
			LOG.warn("Zwave library"+ libraryName +" not supported by iCasa");
			return;
		}
		
		DeviceDeclaration deviceDeclaration = new DeviceDeclaration(importDeclaration);
		SupportedDeviceType deviceType		= SupportedDeviceType.getMatching(deviceDeclaration);

		if (deviceType == null) {
			LOG.warn("Zwave device type for node "+ deviceDeclaration.getZwaveNodeId() +" not supported by iCasa");
			return;
		}
		
		Creator.Entity<? extends ZwaveDevice> creator = creators.get(deviceType);

		if (creator == null){
			LOG.warn("Zwave device type for node "+ deviceDeclaration.getZwaveNodeId() +" not supported by iCasa using library "+library);
			return;
		}

		LOG.info(" Destroying ZWave device : "+deviceType.getDescription()+ " node "+deviceDeclaration.getZwaveNodeId());
		creator.delete("ZwaveDevice#"+deviceDeclaration.getZwaveNodeId());
	}


}
