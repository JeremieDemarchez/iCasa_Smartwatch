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
package fr.liglab.adele.zwave.device.importer.openhab;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.provider.Creator;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.importer.DeviceDeclaration;
import fr.liglab.adele.zwave.device.proxies.openhab.FibaroDoorWindowSensor;
import fr.liglab.adele.zwave.device.proxies.openhab.FibaroMotionSensor;
import fr.liglab.adele.zwave.device.proxies.openhab.FibaroSmokeSensor;
import fr.liglab.adele.zwave.device.proxies.openhab.FibaroWallPlug;

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
public class ZWaveDeviceImporter extends AbstractImporterComponent  {

	private static final Logger LOG = LoggerFactory.getLogger(ZWaveDeviceImporter.class);

	@Creator.Field Creator.Entity<FibaroMotionSensor> motionSensorCreator;

	@Creator.Field Creator.Entity<FibaroWallPlug> wallPlugCreator;

	@Creator.Field Creator.Entity<FibaroSmokeSensor> smokeSensorCreator;

	@Creator.Field Creator.Entity<FibaroDoorWindowSensor> doorWindowSensorCreator;

	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	@ServiceProperty(name = "target", value = "(&(scope=generic)(zwave.device.manufacturer.id=*)(zwave.device.id=*)(zwave.node.id=*)(zwave.device.type.id=*))")
	private String filter;

	public ZWaveDeviceImporter(BundleContext context) {
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
		DeviceDeclaration deviceDeclaration = new DeviceDeclaration(importDeclaration);

		Creator.Entity<? extends ZwaveDevice> creator = getCreator(deviceDeclaration.getZwaveManufacturerId(),deviceDeclaration.getzwaveDeviceType(),deviceDeclaration.getZwaveDeviceId());

		if (creator == null){
			LOG.warn("Zwave device"+ deviceDeclaration.getZwaveNodeId() +" not support by iCasa");
			return;
		}

		LOG.info(" Try to create ZWave Device");
		Map<String,Object> properties = new HashMap<>();
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.HOME_ID),deviceDeclaration.getZwaveHomeId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.NODE_ID),deviceDeclaration.getZwaveNodeId());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.NEIGHBORS),new ArrayList<>());
		properties.put(ContextEntity.State.ID(GenericDevice.class,GenericDevice.DEVICE_SERIAL_NUMBER),"ZwaveDevice#"+deviceDeclaration.getZwaveNodeId());

		creator.create("ZwaveDevice#"+deviceDeclaration.getZwaveNodeId(),properties);
	}

	@Override
	protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		DeviceDeclaration deviceDeclaration = new DeviceDeclaration(importDeclaration);
		LOG.debug("Zwave destroying proxy for end point "+deviceDeclaration.getZwaveNodeId());

		/**
		 * Looking for the appropriate Creator
		 */
		Creator.Entity<? extends ZwaveDevice> creator = getCreator(deviceDeclaration.getZwaveManufacturerId(),deviceDeclaration.getzwaveDeviceType(),deviceDeclaration.getZwaveDeviceId());

		if (creator == null){
			LOG.error("Unable to destroy iCasa proxy related to Node " + deviceDeclaration.getZwaveNodeId());
			return;
		}
		creator.delete("ZwaveDevice#"+deviceDeclaration.getZwaveNodeId());
	}

	private Creator.Entity<? extends ZwaveDevice> getCreator(int manufacturerId,int deviceType,int deviceId){
		if (isFibaroGFMS001(manufacturerId, deviceType, deviceId)){
			LOG.info("Fibaro Multi Sensor detected");
			return motionSensorCreator;
		}
		else if (isFibaroFGWPE101( manufacturerId, deviceType, deviceId)){
			LOG.info("Fibaro Wall Plug detected");
			return wallPlugCreator;
		}
		else if (isFibaroFGK101( manufacturerId, deviceType, deviceId)){
			LOG.info("Fibaro Door Window Sensor detected");
			return doorWindowSensorCreator;
		}
		else if (isFibaroFGSD002(manufacturerId, deviceType, deviceId)){
			LOG.info("Fibaro Smoke Sensor detected");
			return smokeSensorCreator;
		}
		return null;
	}

	/**
	 * Detect if ZwaveNode is a MultiSensor
	 *
	 * @return
	 */
	private boolean isFibaroGFMS001(int manufacturerId,int deviceType,int deviceId){
		if (manufacturerId != Integer.valueOf("010F",16)){
			return false;
		}
		if (deviceType == Integer.valueOf("0800",16)){
			if (deviceId == Integer.valueOf("1001",16) || deviceId == Integer.valueOf("2001",16) || deviceId == Integer.valueOf("3001",16) ){
				return true;
			}
		}else if (deviceType == Integer.valueOf("0801",16)){
			if ( deviceId == Integer.valueOf("1001",16) || deviceId == Integer.valueOf("2001",16)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a smart Plug
	 *
	 * @return
	 */
	private boolean isFibaroFGWPE101(int manufacturerId,int deviceType,int deviceId){
		if (manufacturerId != Integer.valueOf("010F",16)){
			return false;
		}
		if (deviceType == Integer.valueOf("0600",16)) {
			if (deviceId == Integer.valueOf("1000", 16)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a Fibaro Door Sensor
	 *
	 * @return
	 */
	private boolean isFibaroFGK101(int manufacturerId,int deviceType,int deviceId){
		if (manufacturerId != Integer.valueOf("010F",16)){
			return false;
		}
		if (deviceType == Integer.valueOf("0700",16)) {
			if (deviceId == Integer.valueOf("1000", 16) || deviceId ==Integer.valueOf("2000",16) || deviceId ==Integer.valueOf("3000",16)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a Fibaro Smoke sensor
	 *
	 * @return
	 */
	private boolean isFibaroFGSD002(int manufacturerId,int deviceType,int deviceId){
		if (manufacturerId != Integer.valueOf("010F",16)){
			return false;
		}
		if (deviceType == Integer.valueOf("0C02",16)) {
			if (deviceId == Integer.valueOf("1002", 16)) {
				return true;
			}
		}
		return false;
	}


}
