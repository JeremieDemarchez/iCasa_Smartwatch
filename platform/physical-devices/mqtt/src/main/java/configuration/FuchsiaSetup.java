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
package configuration;

import org.apache.felix.ipojo.configuration.Configuration;
import org.apache.felix.ipojo.configuration.Instance;
import org.ow2.chameleon.fuchsia.core.FuchsiaConstants;
import org.ow2.chameleon.fuchsia.core.component.ImportationLinker;
import static org.apache.felix.ipojo.configuration.Instance.instance;


@Configuration
public class FuchsiaSetup {

    Instance mqttServiceDiscovery = instance()
            .of("org.mqtt.MqttServiceDiscovery");

    Instance mqttServiceImporter = instance().named("mqttServiceImporter")
            .of("org.mqtt.MqttServiceImporter");
            //.with("library").setto("openhab");
    		//.with("library").setto("zwave4j");

    Instance mqttImporterLinker = instance()
            .of(FuchsiaConstants.DEFAULT_IMPORTATION_LINKER_FACTORY_NAME)
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(&(scope=generic)(protocol=mqtt))")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=mqttServiceImporter)");

   /* Instance zwaveDeviceImporterLinker = instance()
            .of(FuchsiaConstants.DEFAULT_IMPORTATION_LINKER_FACTORY_NAME)
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(&(scope=generic)(zwave.device.manufacturer.id=*)(zwave.device.type.id=*)(zwave.device.id=*)(zwave.home.id=*)(zwave.node.id=*))")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=zwaveDeviceImporter)");
	*/
}
