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
package org.mqtt.importer;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.mqtt.MqttServiceDeclaration;
import org.mqtt.services.MqttService;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.SmartwatchOperations;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.provider.Creator;

/**
 * Hello world!
 *
 */
@Component
public class MqttServiceImporter extends AbstractImporterComponent {
	
	private static final Logger LOG = LoggerFactory.getLogger(MqttServiceImporter.class);
	
	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;
	
	@ServiceProperty(name = "target", value = "(&(scope=generic)(protocol=mqtt))")
	private String filter;

	
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
		MqttServiceDeclaration declaration = MqttServiceDeclaration.from(importDeclaration);

		LOG.info("Importing declaration from mqtt provider '{}' of service {}",declaration.getProviderId(),declaration.getServiceCode());

		//si le service distant existe dans iCasa alors on l'instancie
		String serviceName = SmartwatchOperations.getIcasaServiceName((int)Integer.parseInt(declaration.getServiceCode()));
		if(serviceName != null)
		{
			
			Map<String,Object> properties= new HashMap<>();
			properties.put(ContextEntity.State.id(MqttService.class, MqttServiceDeclaration.PROVIDER_ID),declaration.getProviderId());
			properties.put(ContextEntity.State.id(MqttService.class, MqttServiceDeclaration.SERVICE_NAME),declaration.getServiceCode());

			String declarationId = MqttServiceDeclaration.createDeclarationId(declaration.getProviderId(), declaration.getServiceCode());

			IndexCreator.getContextCreator(serviceName).create(declarationId,properties);
			handleImportDeclaration(importDeclaration);		

			LOG.info("MqttServiceImporter : instaciation of remote service : "+declaration.getServiceCode());
		}
	}

	@Override
	protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		MqttServiceDeclaration declaration = MqttServiceDeclaration.from(importDeclaration);
		String serviceName = SmartwatchOperations.getIcasaServiceName((int)Integer.parseInt(declaration.getServiceCode()));

		LOG.info("Removing imported declaration for provider '{}' of service '{}'",declaration.getProviderId(),declaration.getServiceCode());
		
		String declarationId = MqttServiceDeclaration.createDeclarationId(declaration.getProviderId(), declaration.getServiceCode());

		IndexCreator.getContextCreator(serviceName).delete(declarationId);
		unhandleImportDeclaration(importDeclaration);
		
		LOG.info("MqttServiceImporter : deletion of remote service : "+declaration.getServiceCode());
	}
}
