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
package fr.liglab.adele.zigbee.device.importer;

import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;
import fr.liglab.adele.zigbee.device.factories.*;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.component.ImporterIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;

/**
 * Fuchsia importer for zigbee devices.
 *
 */
@Component(name = "zigbee.device.importer")
@Provides(specifications = {ImporterService.class, ImporterIntrospection.class})
public class ZigbeeImporter extends AbstractImporterComponent {

    @ServiceProperty(name = ImporterService.TARGET_FILTER_PROPERTY,value ="(protocol=zigbee)")
    private String filter;

    @ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
    private String name;

    private @Creator.Field Creator.Entity<ZigbeePhotometer> photometerFactory;

    private @Creator.Field Creator.Entity<ZigbeeBinaryLight> binaryLightFactory;

    private @Creator.Field Creator.Entity<ZigbeeMotionSensor> motionSensorFactory;

    private @Creator.Field Creator.Entity<ZigbeePowerSwitch> powerSwitchFactory;

    private @Creator.Field Creator.Entity<ZigbeePushButton> pushButtonFactory;

    private @Creator.Field Creator.Entity<ZigbeeThermometer> thermometerFactory;

    private @Creator.Field Creator.Entity<PresenceSensor> presenceSensorFactory;

    private static final Logger logger = LoggerFactory.getLogger(ZigbeeImporter.class);

    @Validate
    protected void start() {
        super.start();
    }

    @Invalidate
    protected void stop() {
        super.stop();
    }

    @PostRegistration
    public void registration(ServiceReference serviceReference) {
        super.setServiceReference(serviceReference);
    }

    @Override
    protected synchronized void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
        ComponentInstance instance;
        try {

            if (importDeclaration != null) {
                Map<String, Object> epdProps = importDeclaration.getMetadata();
                String deviceType = (String) epdProps
                        .get("zigbee.device.type.code");
                String moduleAddress = (String) epdProps.get("id");
                String serialNumber = (String) epdProps
                        .get(RemoteConstants.ENDPOINT_ID);
                logger.debug("endpoint received in importer with module address : "
                        + moduleAddress);


                Hashtable properties = new Hashtable();
                properties.put(ZigbeeDevice.MODULE_ADRESS, moduleAddress);
                properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, serialNumber);

                Creator.Entity creator = getCreator(deviceType);
                if (creator != null){
                    creator.create(serialNumber,properties);
                }
                super.handleImportDeclaration(importDeclaration);

            }

        } catch (Exception ex) {
            logger.error("Error in using import declaration" + importDeclaration.toString(),ex);
        }
    }

    @Override
    protected synchronized void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
        Map<String, Object> epdProps = importDeclaration.getMetadata();
        String deviceType = (String) epdProps.get("zigbee.device.type.code");
        String serialNumber = (String) epdProps.get(RemoteConstants.ENDPOINT_ID);
        Creator.Entity creator = getCreator(deviceType);
        if (creator != null){
            creator.delete(serialNumber);
        }
        unhandleImportDeclaration(importDeclaration);
    }

    @Override
    public String getName() {
        return name;
    }

    private Creator.Entity getCreator(String deviceType){
        if (TypeCode.A001.toString().equals(deviceType)) {
            return binaryLightFactory;
        } else if (TypeCode.C004.toString().equals(deviceType)) {
            return photometerFactory;
        } else if (TypeCode.C001.toString().equals(deviceType)) {
            return pushButtonFactory;
        } else if (TypeCode.C002.toString().equals(deviceType)) {
            return powerSwitchFactory;
        } else if (TypeCode.C003.toString().equals(deviceType)) {
            return motionSensorFactory;
        }  else if (TypeCode.C005.toString().equals(deviceType)) {
            return thermometerFactory;
        }else if (TypeCode.C006.toString().equals(deviceType)) {
            return presenceSensorFactory;
        } else {
            // device type not supported
            return null ;
        }
    }
}
