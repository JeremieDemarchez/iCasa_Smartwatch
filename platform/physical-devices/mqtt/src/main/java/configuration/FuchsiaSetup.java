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
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(&(scope=generic)(protocol=mqtt)(port=*))")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=mqttServiceImporter)");

   /* Instance zwaveDeviceImporterLinker = instance()
            .of(FuchsiaConstants.DEFAULT_IMPORTATION_LINKER_FACTORY_NAME)
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(&(scope=generic)(zwave.device.manufacturer.id=*)(zwave.device.type.id=*)(zwave.device.id=*)(zwave.home.id=*)(zwave.node.id=*))")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=zwaveDeviceImporter)");
	*/
}
