/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.liglab.adele.icasa.commands.impl.ScriptLanguage;
import fr.liglab.adele.icasa.simulator.PersonType;
import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.util.DateTextUtil;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;
import fr.liglab.adele.icasa.ICasaCommand;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 *         Implementation of the ScriptExecutor specification
 */
@Component(name = "script-executor")
@Instantiate(name = "script-executor-0")
@Provides
public class ScriptExecutorImpl implements ScriptExecutor, ArtifactInstaller {

	private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorImpl.class);
	private static final String SCRIPTS_DIRECTORY = "scripts";
	/**
	 * The clock use for simulation
	 */
	@Requires
	private Clock clock;

	@Requires
	private SimulationManager simulationManager;

	/**
	 * Simulator commands added to the platorm
	 */
	private Map<String, ICasaCommand> commands;

	/**
	 * Scripts added to the platform
	 */
	private Map<String, ScriptSAXHandler> scriptMap = new HashMap<String, ScriptSAXHandler>();

	/**
	 * Simulation Execution Thread
	 */
	private Thread executorThread;

	private float executedPercentage;

	private String currentScript;

	private List<ScriptExecutorListener> listeners = new ArrayList<ScriptExecutorListener>();

	@Override
	public State getCurrentScriptState() {
		if (executorThread != null)
			if (executorThread.isAlive())
				if (!clock.isPaused())
					return ScriptExecutor.State.STARTED;
				else
					return ScriptExecutor.State.PAUSED;
		return ScriptExecutor.State.STOPPED;
	}

	@Override
	public void execute(String scriptName) {
		internalExecute(scriptName, 0, 0, true);
	}

	@Override
	public void execute(String scriptName, final Date startDate, final int factor) {
		internalExecute(scriptName, startDate.getTime(), factor, false);
	}

	private void internalExecute(String scriptName, long startDate, int factor, boolean useInternal) {
		if (scriptInExecution())
			return;

		ScriptSAXHandler handler = scriptMap.get(scriptName);
		if (handler != null) {
			if (useInternal)
				startExecutionThread(handler.getActionList(), handler.getStartDate(), handler.getFactor());
			else
				startExecutionThread(handler.getActionList(), startDate, factor);
			currentScript = scriptName;

			for (ScriptExecutorListener listener : getListenersCopy()) {
				listener.scriptStarted(getCurrentScript());
			}
		}
	}

	@Override
	@Invalidate
	public void stop() {
		if (!scriptInExecution())
			return;

		String stoppedScript = currentScript;

		currentScript = null;
		stopExecutionThread();

		for (ScriptExecutorListener listener : getListenersCopy()) {
			listener.scriptStopped(stoppedScript);
		}
	}

	@Override
	public void pause() {
		if (!scriptInExecution())
			return;

		synchronized (clock) {
			clock.pause();
		}

		for (ScriptExecutorListener listener : getListenersCopy()) {
			listener.scriptPaused(getCurrentScript());
		}
	}

	@Override
	public void resume() {
		if (!scriptInExecution())
			return;

		synchronized (clock) {
			clock.resume();
		}

		for (ScriptExecutorListener listener : getListenersCopy()) {
			listener.scriptResumed(getCurrentScript());
		}
	}

	@Override
	public String getCurrentScript() {
		return currentScript;
	}

	@Override
	public List<String> getScriptList() {
		List<String> list = new ArrayList<String>(scriptMap.keySet());
		return list;
	}

	private void startExecutionThread(List<ActionDescription> actions, final long startDate, final int factor) {
		if (actions.isEmpty()) // Nothing to execute
			return;

		executorThread = new Thread(new CommandExecutorRunnable(actions));
		clock.reset();
		clock.setStartDate(startDate);
		clock.setFactor(factor);
		clock.resume();
		executorThread.start();
	}

	private void stopExecutionThread() {
		logger.info("Stopping Executor Thread");
		try {
			executorThread.interrupt();
			executorThread.join();
			clock.reset(); // Stop the clock
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// -- Component bind methods -- //
	@Bind(id = "commands", aggregate = true, optional = true)
	public void bindCommand(ICasaCommand commandService) {
		String name = commandService.getName();
		if (commands == null)
			commands = new HashMap<String, ICasaCommand>();
		commands.put(name, commandService);
	}

	@Unbind(id = "commands")
	public void unbindCommand(ICasaCommand commandService) {
		String name = commandService.getName();
		commands.remove(name);
	}

	// -- File Install methods -- //

	@Override
	public boolean canHandle(File artifact) {
		if (artifact.getName().endsWith(".bhv")) {
			return true;
		}
		return false;
	}

	@Override
	public void install(File artifact) throws Exception {
		ScriptSAXHandler handler = parseFile(artifact);
		if (handler != null) {
			String scriptName = artifact.getName();
			scriptMap.put(scriptName, handler);
			for (ScriptExecutorListener listener : getListenersCopy()) {
				try {
					listener.scriptAdded(scriptName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void update(File artifact) throws Exception {
		ScriptSAXHandler handler = parseFile(artifact);
		if (handler != null) {
			String scriptName = artifact.getName();
			scriptMap.put(scriptName, handler);
			for (ScriptExecutorListener listener : getListenersCopy()) {
				try {
					listener.scriptUpdated(scriptName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void uninstall(File artifact) throws Exception {
		String scriptName = artifact.getName();
		scriptMap.remove(scriptName);
		for (ScriptExecutorListener listener : getListenersCopy()) {
			try {
				listener.scriptRemoved(scriptName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public float getExecutedPercentage() {
		return executedPercentage;
	}

	@Override
	public int getFactor(String scriptName) {
		ScriptSAXHandler handler = scriptMap.get(scriptName);
		if (handler != null)
			return handler.getFactor();
		return 1;
	}

	@Override
	public long getStartDate(String scriptName) {
		ScriptSAXHandler handler = scriptMap.get(scriptName);
		if (handler != null)
			return handler.getStartDate();
		return 0;
	}

	@Override
	public int getActionsNumber(String scriptName) {
		ScriptSAXHandler handler = scriptMap.get(scriptName);
		if (handler != null)
			if (handler.getActionList() != null)
				return handler.getActionList().size();
		return 0;
	}

	@Override
	public int getExecutionTime(String scriptName) {
		ScriptSAXHandler handler = scriptMap.get(scriptName);
		if (handler != null)
			return handler.getExecutionTime();
		return 0;
	}

	@Override
	public State getState(String scriptName) {
		if (scriptName.equals(scriptName))
			return getCurrentScriptState();
		return ScriptExecutor.State.STOPPED;
	}

	@Override
	public void saveSimulationScript(String fileName) {
		FileWriter outFile;
		PrintWriter out;
		try {

			String dateStr = DateTextUtil.getTextDate(System.currentTimeMillis());
			File scriptsDir = new File(SCRIPTS_DIRECTORY);
			if (!scriptsDir.exists()) {
				logger.info("creating directory: " + SCRIPTS_DIRECTORY);
				boolean result = scriptsDir.mkdir();
				if (!result) {
					logger.error("Unable to create directory: " + SCRIPTS_DIRECTORY);
				}
			}
			outFile = new FileWriter(SCRIPTS_DIRECTORY + System.getProperty("file.separator") + fileName);
			out = new PrintWriter(outFile);

			out.println("<behavior startdate=\"" + dateStr + "\" factor=\"1440\">");
			out.println();
			out.println("\t<!-- Zone Section -->");
			out.println();

			for (Zone zone : simulationManager.getZones()) {
				String id = zone.getId();
				int leftX = zone.getLeftTopAbsolutePosition().x;
				int topY = zone.getLeftTopAbsolutePosition().y;
                int bottomZ = zone.getLeftTopAbsolutePosition().z;
				int width = zone.getXLength();
				int height = zone.getYLength();
                int depth = zone.getZLength();
                StringBuilder instruction = new StringBuilder();
                instruction.append("\t<create-zone ").
                        append(' ').
                        append(ScriptLanguage.ID).append(" =\"").append(id).append("\"").
                        append(' ').
                        append(ScriptLanguage.LEFT_X).append(" =\"").append(leftX).append("\"").
                        append(' ').
                        append(ScriptLanguage.TOP_Y).append(" =\"").append(topY).append("\"").
                        append(' ').
                        append(ScriptLanguage.BOTTOM_Z).append(" =\"").append(bottomZ).append("\"").
                        append(' ').
                        append(ScriptLanguage.X_LENGTH).append(" =\"").append(width).append("\"").
                        append(' ').
                        append(ScriptLanguage.Y_LENGTH).append(" =\"").append(height).append("\"").
                        append(' ').
                        append(ScriptLanguage.Z_LENGTH).append(" =\"").append(depth).append("\"");
                instruction.append("/>");

				out.println(instruction.toString());

				for (String variable : zone.getVariableNames()) {
					Object value = zone.getVariableValue(variable);

					out.println("\t<add-zone-variable zoneId=\"" + id + "\" variable=\"" + variable + "\" />");
					out.println("\t<modify-zone-variable zoneId=\"" + id + "\" variable=\"" + variable + "\" value=\""
					      + value + "\" />");
				}
				out.println();
			}

			out.println("\t<!-- Device Section -->");
			out.println();

			for (LocatedDevice device : simulationManager.getDevices()) {
				String id = device.getSerialNumber();
				String type = device.getType();

				out.println("\t<create-device id=\"" + id + "\" type=\"" + type + "\" />");

				String location = (String) device.getPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME);
				if (location != null && !(location.equals(SimulatedDevice.LOCATION_UNKNOWN)))
					out.println("\t<move-device-zone deviceId=\"" + id + "\" zoneId=\"" + location + "\" />");

				out.println();

			}

			out.println();
			out.println("\t<!-- Person Section -->");
			out.println();

			for (Person person : simulationManager.getPersons()) {
				String id = person.getName();
				PersonType type = person.getPersonType();

				out.println("\t<create-person id=\"" + id + "\" type=\"" + type.getName() + "\" />");

				Zone zone = simulationManager.getZoneFromPosition(person.getCenterAbsolutePosition());

				if (zone != null)
					out.println("\t<move-person-zone personId=\"" + id + "\" zoneId=\"" + zone.getId() + "\" />");

				out.println();

			}

			out.println("</behavior>");

			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Parses the script file
	 * 
	 * @param file
	 * @return
	 */
	private ScriptSAXHandler parseFile(File file) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			ScriptSAXHandler handler = new ScriptSAXHandler();
			saxParser.parse(file, handler);
			return handler;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Command executor Thread (Runnable) class
	 * 
	 * @author Gabriel Pedraza Ferreira
	 * 
	 */
	private final class CommandExecutorRunnable implements Runnable {

		private List<ActionDescription> actionDescriptions;

		public CommandExecutorRunnable(List<ActionDescription> actionDescriptions) {
			this.actionDescriptions = actionDescriptions;
		}

		@Override
		public void run() {
			int index = 0;
			boolean execute = true;
			executedPercentage = 0;
			while (execute) {
				long elapsedTime = clock.getElapsedTime();

				List<ActionDescription> toExecute = calculeToExecute(index, elapsedTime);
				index += toExecute.size();

				if (index >= actionDescriptions.size())
					execute = false;

				executeActions(toExecute);

				// Computes the execution percentage
				executedPercentage = index / actionDescriptions.size();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					execute = false;
				}
			}
			currentScript = null; // Script execution finished
		}

		private List<ActionDescription> calculeToExecute(int index, long elapsedTime) {
			List<ActionDescription> toExecute = new ArrayList<ActionDescription>();

			for (int i = index; i < actionDescriptions.size(); i++) {
				ActionDescription action = actionDescriptions.get(i);
				int actionDelay = action.getDelay() * 60 * 1000; // action delay in
				                                                 // virtual
				                                                 // milliseconds
				if (elapsedTime >= actionDelay)
					toExecute.add(action);
				else
					break;
			}
			return toExecute;
		}

		private void executeActions(List<ActionDescription> toExecute) {
			if (!toExecute.isEmpty()) {
				synchronized (clock) {
					clock.pause();
					for (ActionDescription actionDescription : toExecute) {
						ICasaCommand command = commands.get(actionDescription.getCommandName());
						if (command != null) {
							try {
								command.execute(System.in, System.out, actionDescription.getConfiguration());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					clock.resume();
				}
			}
		}
	}

	@Override
	public void addListener(ScriptExecutorListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		synchronized (listeners) {
			try {
				listeners.add(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void removeListener(ScriptExecutorListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		synchronized (listeners) {
			try {
				listeners.remove(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<ScriptExecutorListener> getListenersCopy() {
		List<ScriptExecutorListener> listenersCopy;
		synchronized (listeners) {
			listenersCopy = Collections.unmodifiableList(new ArrayList<ScriptExecutorListener>(listeners));
		}
		return listenersCopy;
	}

	private boolean scriptInExecution() {
		return (currentScript != null && getCurrentScriptState() != ScriptExecutor.State.STOPPED);
	}

}
