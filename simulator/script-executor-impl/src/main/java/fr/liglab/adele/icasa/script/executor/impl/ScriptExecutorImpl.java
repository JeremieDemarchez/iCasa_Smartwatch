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
package fr.liglab.adele.icasa.script.executor.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.script.executor.SimulatorCommand;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 * Implementation of the ScriptExecutor specification
 */
@Component(name="script-executor")
@Instantiate(name="script-executor-0")
@Provides
public class ScriptExecutorImpl implements ScriptExecutor, ArtifactInstaller {


	private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorImpl.class);

	/**
	 * The clock use for simulation
	 */
    @Requires
	private Clock clock;

	/**
	 * Simulator commands added to the platorm
	 */
	private Map<String, SimulatorCommand> commands;
	
	/**
	 * Scripts added to the platform
	 */
	private Map<String, File> scriptMap = new HashMap<String, File>();

	/**
	 * Simulation Execution Thread 
	 */
	private Thread executorThread;

	/**
	 * Flag to determine if a current execution script is paused 
	 */
	private boolean paused = false;

	private float executedPercentage;
	
	private String currentScript;


	@Override
	public State getState() {
		if (executorThread != null)
			if (executorThread.isAlive())
				if (!paused)
					return ScriptExecutor.State.EXECUTING;
				else
					return ScriptExecutor.State.PAUSED;
		return ScriptExecutor.State.STOPPED;
	}

	@Override
	public void execute(String scriptName) {
		File scriptFile = scriptMap.get(scriptName);
		if (scriptFile != null) {
			currentScript = scriptName;
			executeScript(scriptFile);
		}
			
	}

	@Override
	public void execute(String scriptName, final Date startDate, final int factor) {
		File scriptFile = scriptMap.get(scriptName);
		if (scriptFile != null) {
			currentScript = scriptName;
			executeScript(scriptFile, startDate, factor);
		}
	}
	
	@Override
    @Invalidate
	public void stop() {
		currentScript = null;
		stopExecutionThread();
	}

	@Override
	public void pause() {
		synchronized (clock) {
			clock.pause();
			paused = true;
		}
	}

	@Override
	public void resume() {
		synchronized (clock) {
			clock.resume();
			paused = false;
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
	
	private void executeScript(File file) {
		ScenarioSAXHandler handler = parseFile(file);
		if (handler != null) {
			startExecutionThread(handler.getActionList(), handler.getStartDate(), handler.getFactor());
		}
	}

	private void executeScript(File file, final Date startDate, final int factor) {
		ScenarioSAXHandler handler = parseFile(file);
		if (handler != null) {
			startExecutionThread(handler.getActionList(), startDate.getTime(), factor);
		}
	}

	
	private void startExecutionThread(List<ActionDescription> actions, final long startDate, final int factor) {
		if (actions.isEmpty()) // Nothing to execute
			return;

		executorThread = new Thread(new CommandExecutorRunnable(actions));
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


	/**
	 * Parses the script file 
	 * @param file
	 * @return
	 */
	private ScenarioSAXHandler parseFile(File file) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			ScenarioSAXHandler handler = new ScenarioSAXHandler(this);
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

	
	// -- Component bind methods -- //
    @Bind(id="commands", aggregate=true, optional=true)
	public void bindCommand(SimulatorCommand commandService, ServiceReference reference) {
		String name = (String) reference.getProperty("name");
		if (commands == null)
			commands = new HashMap<String, SimulatorCommand>();
		commands.put(name, commandService);
	}

    @Unbind(id="commands")
	public void unbindCommand(ServiceReference reference) {
		String name = (String) reference.getProperty("name");
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
		logger.info("--------------------  New Script File added : " + artifact.getName());
		scriptMap.put(artifact.getName(), artifact);
	}

	@Override
	public void update(File artifact) throws Exception {
		// Nothing to do
	}

	@Override
	public void uninstall(File artifact) throws Exception {
		scriptMap.remove(artifact);
	}


	@Override
   public float getExecutedPercentage() {
      return executedPercentage;
   }

    /**
	 * Command executor Thread (Runnable) class
	 * 
	 * @author Gabriel
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
		}

		private List<ActionDescription> calculeToExecute(int index, long elapsedTime) {
			List<ActionDescription> toExecute = new ArrayList<ActionDescription>();

			for (int i = index; i < actionDescriptions.size(); i++) {
				ActionDescription action = actionDescriptions.get(i);
				int actionDelay = action.getDelay() * 60 * 1000; // action delay in virtual milliseconds
				if (elapsedTime >= actionDelay)
					toExecute.add(action);
				else
					break;
			}
			return toExecute;
		}

		private void executeActions(List<ActionDescription> toExecute) {
			if (!toExecute.isEmpty()) {
				logger.info("Init time ---> " + getDate(clock.currentTimeMillis()));
				logger.info("To Execute ---> " + toExecute.size() + " Actions");
				synchronized (clock) {
					clock.pause();
					for (ActionDescription actionDescription : toExecute) {
						SimulatorCommand command = commands.get(actionDescription.getCommandName());
						if (command != null) {
							try {
								command.execute(null, null, actionDescription.getConfiguration());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					clock.resume();
				}
				logger.info("End time ---> " + getDate(clock.currentTimeMillis()));
			}
		}

		private String getDate(long timeInMs) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
			return format.format(new Date(timeInMs));
		}
	}


    @Override
    public long currentTimeMillis() {
        return clock.currentTimeMillis();
    }

    @Override
    public void setStartDate(long startDate) {
        clock.setStartDate(startDate);
    }

    @Override
    public void setFactor(int factor) {
        clock.setFactor(factor);
    }

    @Override
    public long getElapsedTime() {
        return clock.getElapsedTime();
    }

    @Override
    public int getFactor() {
        return clock.getFactor();
    }

    @Override
    public Date getStartDate() {
        return clock.getStartDate();
    }






}
