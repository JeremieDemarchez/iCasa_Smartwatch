package fr.liglab.adele.zwave.device;

import org.apache.felix.ipojo.configuration.Configuration;
import org.apache.felix.ipojo.configuration.Instance;
import org.ow2.chameleon.fuchsia.core.FuchsiaConstants;
import org.ow2.chameleon.fuchsia.core.component.ImportationLinker;
import static org.apache.felix.ipojo.configuration.Instance.instance;


@Configuration
public class FuchsiaSetup {

    Instance fileBasedDiscovery = instance()
            .of("org.ow2.chameleon.fuchsia.discovery.filebased.FileBasedDiscoveryImport");

    Instance zwaveImporter = instance()
            .of("fr.liglab.adele.zwave.device.importer.ZWaveImporter");

    Instance zwaveImporterLinker = instance()
            .of(FuchsiaConstants.DEFAULT_IMPORTATION_LINKER_FACTORY_NAME)
            .with(ImportationLinker.FILTER_IMPORTDECLARATION_PROPERTY).setto("(&(scope=generic)(protocol=zwave)(port=*))")
            .with(ImportationLinker.FILTER_IMPORTERSERVICE_PROPERTY).setto("(instance.name=zwaveImporter)");


}
