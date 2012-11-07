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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.command.ICommandService;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
public class ScriptExecutorImpl implements ScriptExecutor, ArtifactInstaller {

	private Clock clock;


	/**
	 * The OSGi ConfigAdmin service
	 */
	private ConfigurationAdmin configAdmin;

		
	private Map<String, ICommandService> commands;
	

	private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorImpl.class);

	private Map<String, File> scriptMap = new HashMap<String, File>();

	private CommandExecutor commandExecutor = new CommandExecutor(this);
	
	
	public void executeScript(String scriptName) {
		File scriptFile = scriptMap.get(scriptName);
		if (scriptFile != null)
			executeScript(scriptFile);			
	}

	
	
	public void executeScript(String scriptName, final Date startDate, final int factor) {
		File scriptFile = scriptMap.get(scriptName);
		if (scriptFile != null) {
			executeScript(scriptFile, startDate, factor);
		}
	}
	
	public ICommandService getCommand(String commandName) {
		return commands.get(commandName);
	}
	
	public Clock getClock() {
		return clock;
	}

	private void executeScript(File file) {		
		ScenarioSAXHandler handler = parseFile(file);
		if (handler!=null) {
			executeScript(handler.getActionList(), handler.getStartDate(), handler.getFactor());
		}	
	}

	private void executeScript(File file, final Date startDate, final int factor) {
		ScenarioSAXHandler handler = parseFile(file);
		if (handler!=null) {
			executeScript(handler.getActionList(), startDate.getTime(), factor);
		}
	}
	


	private void executeScript(List<ActionDescription> actions, final long startDate, final int factor) {
      if (commandExecutor!=null) {
         commandExecutor.setStartDate(startDate);
         commandExecutor.setFactor(factor);
         commandExecutor.setActionDescriptions(actions);
         commandExecutor.start();
      }
	}

	public void stopExecution() {
		if (commandExecutor!=null)
			commandExecutor.stop();
	}
	
	

	@Override
   public void pauseExecution() {
		
	}



	@Override
   public void resumeExecution() {
   }


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
	
	public void bindCommand(ICommandService commandService, ServiceReference reference) {
		String name = (String) reference.getProperty("name");
		if (commands == null)
			commands = new HashMap<String, ICommandService>();
		commands.put(name, commandService);
	}
	
	public void unbindCommand(ServiceReference reference) {
		String name = (String) reference.getProperty("name");
		commands.remove(name);
	}
	
	
	public ConfigurationAdmin getConfigAdmin() {
		return configAdmin;
	}

	public boolean canHandle(File artifact) {
		if (artifact.getName().endsWith(".bhv")) {
			return true;
		}
		return false;
	}

	public void install(File artifact) throws Exception {
		System.out.println("--------------------  New Script File added : " + artifact.getName());
		scriptMap.put(artifact.getName(), artifact);
	}

	public void update(File artifact) throws Exception {
		// Nothing to be done!
	}

	public void uninstall(File artifact) throws Exception {
		scriptMap.remove(artifact);

	}

	public List<String> getScriptList() {
		List<String> list = new ArrayList<String>(scriptMap.keySet());
		return list;
	}




}
