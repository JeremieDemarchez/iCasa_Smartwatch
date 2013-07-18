/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.liglab.adele.icasa.commands.ICasaCommand;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.json.JSONException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

@Component(immediate = true)
@Provides(specifications = { UtilCommand.class })
@Instantiate
public final class UtilCommand {

	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String TAB = "\t";

	@ServiceProperty(name = CommandProcessor.COMMAND_SCOPE, value = "icasa")
	private String _commandScope = "icasa";

	@ServiceProperty(name = CommandProcessor.COMMAND_FUNCTION, value = "{help}")
	private String[] _commandFunctions = new String[] { "help" };

	/**
	 * The bundle context used by commands
	 */
	private BundleContext _context;

	/**
	 * A tracker to track all the commands
	 */
	private ServiceTracker _commandTracker = null;

	/**
	 * The list of available commands (Fully Qualified Name, a Description)
	 */
	private final Map<String, String> _availableCommands = new LinkedHashMap<String, String>();

	/**
	 * Lock for the commands' map
	 */
	private final Object _availableCommandsLock = new Object();

	UtilCommand(BundleContext context) {
		_context = context;
	}

	public void help(CommandSession session) throws JSONException {
		StringBuilder builder = new StringBuilder();

		synchronized (_availableCommandsLock) {
			for (Map.Entry<String, String> entry : _availableCommands
					.entrySet()) {
				builder.append(entry.getKey()).append(NEWLINE).append(TAB)
						.append(entry.getValue()).append(NEWLINE);
			}
		}

		// write to the session console (allow piping)
		session.getConsole().print(builder);
	}

	@Validate
	protected void start() {
		try {
			_commandTracker = trackOSGiCommands(_context);
			_commandTracker.open();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	@Invalidate
	protected void stop() {
		assert (_commandTracker != null);
		_commandTracker.close();
		_commandTracker = null;
	}

	private ServiceTracker trackOSGiCommands(final BundleContext context)
			throws InvalidSyntaxException {


        Filter filter = context.createFilter(String.format("(&(%s=%s)(%s=*))",
                CommandProcessor.COMMAND_SCOPE,
                ICasaCommand.DEFAULT_NAMESPACE,
				CommandProcessor.COMMAND_FUNCTION));

		return new ServiceTracker(context, filter, null) {
			@Override
			public Object addingService(ServiceReference ref) {
				try {
					String description = (String) ref
							.getProperty(ICasaCommand.PROP_DESCRIPTION);
					description = (description == null ? "" : description);

					SortedSet<String> funcFQNames = extractFunctionsQualifiedName(ref);

					synchronized (_availableCommandsLock) {
						for (String function : funcFQNames) {
							_availableCommands.put(function, description);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void removedService(ServiceReference ref, Object service) {
				SortedSet<String> funcFQNames = extractFunctionsQualifiedName(ref);
				synchronized (_availableCommandsLock) {
					for (String function : funcFQNames) {
						_availableCommands.remove(function);
					}
				}

			}
		};
	}

	/**
	 * Retrieve the fully qualified names (i.e scope:name) of the functions from
	 * a serviceReference
	 * 
	 * @param ref
	 *            : a service reference of a command
	 * @return the fqn of the command
	 */
	private static final SortedSet<String> extractFunctionsQualifiedName(
			ServiceReference ref) {
		// get the scope
		String scope = (String) ref.getProperty(CommandProcessor.COMMAND_SCOPE);

		Object functionObject = ref
				.getProperty(CommandProcessor.COMMAND_FUNCTION);
		SortedSet<String> result = new TreeSet<String>();
		if (functionObject instanceof String) {
			result.add(scope + ":" + functionObject);
		} else if (functionObject instanceof String[]) {
			for (String functionName : (String[]) functionObject) {
				result.add(scope + ":" + functionName);
			}
		}
		return result;
	}

}
