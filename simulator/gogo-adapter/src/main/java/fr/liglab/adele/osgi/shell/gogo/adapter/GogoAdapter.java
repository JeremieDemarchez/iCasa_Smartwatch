/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.osgi.shell.gogo.adapter;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Function;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import fr.liglab.adele.icasa.command.SimulatorCommand;

/**
 * <p>
 * This component exposes all ICommandService services as gogo shell commands.
 * </p>
 * 
 * <p>
 * Exposed command can be used twofold :
 * <ul>
 * <li><b>Using a JSON string as parameter :</b> <code> ns:name "{\"key\":\"value\", ... }"</code>. Pay attention to the quotes and escaped characters.</li>
 * <li><b>Using a map as parameter :</b> <code> ns:name "[key:value]"</code>. Pay attention to the quotes and escaped characters.</li>
 * </ul>
 * 
 */
@Component(immediate = true)
@Instantiate
public class GogoAdapter {

	/**
	 * Keep a track of the registered command so as to be able to unregister
	 * them
	 */
	final Map<String, ServiceRegistration> m_functions = new HashMap<String, ServiceRegistration>();

	/**
	 * The bundle context used to register services.
	 */
	final BundleContext m_context;

	/**
	 * Get the context from iPOJO
	 * 
	 * @param context
	 *            : the bundle context
	 */
	GogoAdapter(BundleContext context) {
		m_context = context;
	}

	@Bind
	void bindCommand(SimulatorCommand command, Map iCommandProperties) {
		// Create an adapter for the command
		AdaptedFunction function = new AdaptedFunction(command);

		// Read the adapted command properties
		String commandName = (String) iCommandProperties
				.get(SimulatorCommand.PROP_NAME);
		String commandNamespace = (String) iCommandProperties
				.get(SimulatorCommand.PROP_NAMESPACE);


		// Register the command
		Dictionary commandProperties = new Properties();
		commandProperties.put(CommandProcessor.COMMAND_FUNCTION, new String[]{commandName});
		commandProperties.put(CommandProcessor.COMMAND_SCOPE, commandNamespace);
		ServiceRegistration commandRegistration = m_context.registerService(
				new String[]{Function.class.getName()}, function, commandProperties);

		// keep a track of the registration
		m_functions.put(commandNamespace + ":" + commandName,
				commandRegistration);

	}

	@Unbind
	void unbindCommand(SimulatorCommand command, Map iCommandProperties) {
		// Read the adapted command properties
		String commandName = (String) iCommandProperties
				.get(SimulatorCommand.PROP_NAME);
		String commandNamespace = (String) iCommandProperties
				.get(SimulatorCommand.PROP_NAMESPACE);

		// Unregister the adapted command
		ServiceRegistration commandRegistration = m_functions
				.get(commandNamespace + ":" + commandName);
		if (commandRegistration != null) {
			commandRegistration.unregister();
			m_functions.remove(commandNamespace + ":" + commandName);
		}
		
	}

}
