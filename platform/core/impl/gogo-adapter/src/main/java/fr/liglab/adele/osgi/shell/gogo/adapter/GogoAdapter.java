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
package fr.liglab.adele.osgi.shell.gogo.adapter;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import fr.liglab.adele.icasa.commands.ICasaCommand;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Function;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * This component exposes all iCasaCommand services as gogo shell commands.
 * </p>
 * 
 * <p>
 * Exposed command can be used twofold :
 * <ul>
 * <li><b>Using a JSON string as parameter :</b> <code> ns:name "{\"key\":\"value\", ... }"</code>. Pay attention to the
 * quotes and escaped characters.</li>
 * <li><b>Using a map as parameter :</b> <code> ns:name "[key:value]"</code>. Pay attention to the quotes and escaped
 * characters.</li>
 * </ul>
 * 
 */
@Component(immediate = true)
@Instantiate
public class GogoAdapter {

	/**
	 * Keep a track of the registered command so as to be able to unregister them
	 */
	private final Map<String, ServiceRegistration> m_functions = new HashMap<String, ServiceRegistration>();

	/**
	 * Lock for the functions map
	 */
	private final Object _functionsLock = new Object();

	/**
	 * The bundle context used to register services.
	 */
	private final BundleContext m_context;

	/**
	 * Get the context from iPOJO
	 * 
	 * @param context : the bundle context
	 */
	GogoAdapter(BundleContext context) {
		m_context = context;
	}

	@Bind(aggregate = true)
	void bindCommand(ICasaCommand command) {

		try {
			// Create an adapter for the command
			AdaptedCommandFunction function = new AdaptedCommandFunction(command);

			// Read the adapted command properties
			String commandName = command.getName();
			String commandNamespace = ICasaCommand.DEFAULT_NAMESPACE;
			String commandDescription = command.getDescription();

			// Register the command
			Dictionary  commandProperties = new Properties();
			commandProperties.put(CommandProcessor.COMMAND_FUNCTION, new String[] { commandName });
			commandProperties.put(CommandProcessor.COMMAND_SCOPE, commandNamespace);
			commandProperties.put(ICasaCommand.PROP_DESCRIPTION, commandDescription);

			ServiceRegistration commandRegistration = m_context.registerService(new String[] { Function.class.getName() },
			      function, commandProperties);

			synchronized (_functionsLock) {
				// keep a track of the registration
				m_functions.put(commandNamespace + ":" + commandName, commandRegistration);
			}

		} catch (Exception e) {

		}
	}

	@Unbind
	void unbindCommand(ICasaCommand command) {
		try {
			// Read the adapted command properties
			String commandName = command.getName();
			String commandNamespace = ICasaCommand.DEFAULT_NAMESPACE;
			// Unregister the adapted command
			ServiceRegistration commandRegistration = m_functions.get(commandNamespace + ":" + commandName);
			if (commandRegistration != null) {
				commandRegistration.unregister();

				synchronized (_functionsLock) {
					m_functions.remove(commandNamespace + ":" + commandName);
				}
			}
		} catch (Exception e) {

		}

	}

}
