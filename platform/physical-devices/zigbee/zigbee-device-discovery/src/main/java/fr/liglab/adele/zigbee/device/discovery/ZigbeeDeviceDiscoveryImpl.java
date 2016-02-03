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
/**
 *
 */
package fr.liglab.adele.zigbee.device.discovery;

import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryIntrospection;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclarationBuilder;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
@Component(name = "zigbee.fuchsia.discovery", immediate = true)
@Provides(specifications={ZigbeeDeviceTracker.class, DiscoveryService.class, DiscoveryIntrospection.class})
public class ZigbeeDeviceDiscoveryImpl extends AbstractDiscoveryComponent implements ZigbeeDeviceTracker {

	private static final Logger logger = LoggerFactory.getLogger(ZigbeeDeviceDiscoveryImpl.class);

	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	protected ZigbeeDeviceDiscoveryImpl(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	public void start(){
		super.start();
	}

	@Override
	public void stop(){
		super.stop();
	}

	@Override
	public synchronized void deviceAdded(DeviceInfo deviceInfo) {
		logger.debug("new zigbee device added in discovery : " + deviceInfo.getModuleAddress());
		registerZigbeeImportDeclaration(deviceInfo);
	}

	@Override
	public synchronized void deviceRemoved(DeviceInfo deviceInfo) {
		String serial = computeSerialNumber(deviceInfo.getModuleAddress());
		ImportDeclaration importToRemove = null;
		for (ImportDeclaration declaration : super.getImportDeclarations()){
			Map<String,Object> metadatas = declaration.getMetadata();
			if (metadatas.get("id") != null && metadatas.get("id").equals(deviceInfo.getModuleAddress())){
				importToRemove = declaration;
				break;
			}
		}
		if (importToRemove !=null) {
			super.unregisterImportDeclaration(importToRemove);
		}
	}

	@Override
	public void deviceDataChanged(String moduleAddress, Data oldData,
								  Data newData) {
		// do nothing

	}

	@Override
	public void deviceBatteryLevelChanged(String moduleAddress,
										  float oldBatteryLevel, float newBatteryLevel) {
		// do nothing

	}

	private void registerZigbeeImportDeclaration(DeviceInfo deviceInfo) {

		String serialNumber = computeSerialNumber(deviceInfo.getModuleAddress());

		ImportDeclaration zigbeeDeclaration = ImportDeclarationBuilder.empty()
				.key(RemoteConstants.ENDPOINT_ID).value(serialNumber)
				.key("protocol").value("zigbee")
				.key("objectClass").value(new String[] { "fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo" })
				.key("id").value(deviceInfo.getModuleAddress())
				.key("zigbee.device.type.code").value( deviceInfo.getTypeCode().toString())
				.build();

		super.registerImportDeclaration(zigbeeDeclaration);
	}

	private String computeSerialNumber(String moduleAddress){
		return "zigbee#"+moduleAddress;
	}

	@Override
	public String getName() {
		return name;
	}
}
