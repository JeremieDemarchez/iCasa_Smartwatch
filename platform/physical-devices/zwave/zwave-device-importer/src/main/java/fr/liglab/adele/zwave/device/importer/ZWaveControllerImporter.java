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

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.zwave.device.api.ZwaveControllerICasa;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.proxyes.*;
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
public class ZWaveControllerImporter extends AbstractImporterComponent  {

	private static final Logger LOG = LoggerFactory.getLogger(ZWaveControllerImporter.class);

	private final BundleContext context;

	@Creator.Field Creator.Entity<ZwaveControllerICasaImpl> controllerCreator;

	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	@ServiceProperty(name = "target", value = "(&(scope=generic)(protocol=zwave)(port=*))")
	private String filter;

	/**
	 * The properties used to match the proxy factory
	 */
	public final static String FACTORY_PROPERTY_MANUFACTURER 	= "zwave.manufacturer";
	public final static String FACTORY_PROPERTY_DEVICE_ID 		= "zwave.device.id";
	public final static String FACTORY_PROPERTY_DEFAULT_PROXY	= "zwave.default.proxy";

	/**
	 *
	 */
	public final static String PROXY_PROPERTY_CONTROLLER 		= "zwave.controller";
	public final static String PROXY_PROPERTY_NODE 				= "zwave.node";
	public final static String PROXY_PROPERTY_ENDPOINT 			= "zwave.endpoint";

	@Validate
	protected void start() {
		super.start();
	}

	@Invalidate
	protected void stop() {
		super.stop();
	}

	public ZWaveControllerImporter(BundleContext context) {
		this.context = context;
	}

	@Override
	protected void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		ZWaveControllerDeclaration declaration = ZWaveControllerDeclaration.from(importDeclaration);

		LOG.info("Importing declaration for zwave device '{}' at port {}",declaration.getId(),declaration.getPort());

		Map<String,Object> properties= new HashMap<>();
		properties.put(ContextEntity.State.ID(ZwaveControllerICasa.class,ZwaveControllerICasa.SERIAL_PORT),declaration.getPort());
		properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.ZWAVE_NEIGHBORS),new ArrayList<>());

		/**
		 * HACK...
		 */
		controllerCreator.create("ZwaveDevice#"+1,properties);


		handleImportDeclaration(importDeclaration);

	}

	@Override
	protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		ZWaveControllerDeclaration declaration = ZWaveControllerDeclaration.from(importDeclaration);

		LOG.info("Removing imported declaration for zwave device '{}' at port {}",declaration.getId(),declaration.getPort());

		/**
		 * HACK...
		 */
		controllerCreator.delete("ZwaveDevice#"+0);

		unhandleImportDeclaration(importDeclaration);
	}


	public String getName() {
		return name;
	}

}
