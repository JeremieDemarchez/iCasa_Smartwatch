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
package fr.liglab.adele.zigbee.fuchsia.set.up;


import org.apache.felix.ipojo.configuration.Configuration;
import org.apache.felix.ipojo.configuration.Instance;
import org.ow2.chameleon.fuchsia.core.FuchsiaConstants;
import org.ow2.chameleon.fuchsia.core.component.ImportationLinker;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;

import static org.apache.felix.ipojo.configuration.Instance.instance;

@Configuration
public class ZigbeeFuchsiaSetUp {

    Instance zigbeeImporter = instance().named("zigbeeImporter")
            .of("fr.liglab.adele.icasa.zigbee.device.importer.ZigbeeImporter")
            .with(ImporterService.TARGET_FILTER_PROPERTY).setto("(protocol=zigbee)");

    Instance zigbeeImporterLinker = instance().named("zigbeeLinker")
            .of(FuchsiaConstants.DEFAULT_IMPORTATION_LINKER_FACTORY_NAME)
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(protocol=zigbee)")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=zigbeeImporter)");


    Instance zigbeeDiscovery = instance().named("zigbeeDiscovery")
            .of("fr.liglab.adele.icasa.zigbee.device.discovery.ZigbeeDeviceDiscoveryImpl");

    Instance zigbeeDriver = instance().named("zigbeeDriver")
            .of("fr.liglab.adele.icasa.device.zigbee.driver.impl.ZigbeeDriverImpl")
            .with("zigbee.driver.port").setto("COM4")
               .with("baud.rate").setto(115200);
}
