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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.Factory;
import org.json.JSONObject;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.command.ICommandService;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.script.executor.impl.actions.Action;
import fr.liglab.adele.icasa.script.executor.impl.actions.ActivateDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.AddDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.DeactivateDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.FaultDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.ModifyDeviceValueAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.ModifyEnvironmentAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.MoveDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.MovePersonAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.RemoveDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.actions.RepairDeviceAction;
import fr.liglab.adele.icasa.script.executor.impl.commands.RunnableCommandAdapter;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
public class ScriptExecutorImpl implements ScriptExecutor, ArtifactInstaller {

	private Clock clock;

	/**
	 * The ROSE machine
	 */
	// private RoseMachine roseMachine;

	/**
	 * The OSGi ConfigAdmin service
	 */
	private ConfigurationAdmin configAdmin;

	/**
	 * The Simulation Mananeger service provided by iCASA simulation platform
	 */
	private SimulationManager simulationManager;

	private Map<String, Factory> factories = new Hashtable<String, Factory>();

	private List<ScheduledFuture> tasks = new ArrayList<ScheduledFuture>();

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private Map<String, GenericDevice> devices;
		
	private Map<String, ICommandService> commands;
	

	private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorImpl.class);

	private Map<String, File> scriptMap = new HashMap<String, File>();

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
		
		/*
		ScriptParser parser = new ScriptParserImpl();
		List<Action> actions = parser.parse(file);
		final Date startDate = parser.getStartDate();
		final int factor = parser.getFactor();
		executeScript(actions, startDate, factor);
		*/
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
	      SAXParser saxParser = factory.newSAXParser();
	      ScenarioSAXHandler handler = new ScenarioSAXHandler(this);
	      saxParser.parse(file, handler);
	      
	      System.out.println("Start date" + handler.getStartDate());
	      System.out.println("Factor " + handler.getFactor());
	      
	      CommandExecutor commandExecutor = new CommandExecutor(this);
	      commandExecutor.setStartDate(handler.getStartDate());
	      clock.setFactor(handler.getFactor());
	      commandExecutor.setActionDescriptions(handler.getActionList());
	      commandExecutor.start();
	      
	      /*
	      
	      List<ActionDescription> actions = handler.getActionList();
	      
	      
	      
	      for (ActionDescription actionDescription : actions) {
	         String commandName = actionDescription.getCommandName();
	         ICommandService commandService = commands.get(commandName);
	         
	         
	         
	         if (commandService!=null) {
	         	
	         	
	         	RunnableCommandAdapter adapter = new RunnableCommandAdapter(commandService, actionDescription.getConfiguration());
	         	scheduler.schedule(adapter, 2, TimeUnit.SECONDS);

	         }
	      	
         }
	      */
         
	      
      } catch (ParserConfigurationException e) {
	      e.printStackTrace();
      } catch (SAXException e) {
	      e.printStackTrace();
      } catch (IOException e) {
	      e.printStackTrace();
      }
		
      
      
		
		
	}

	private void executeScript(File file, final Date startDate, final int factor) {
		ScriptParser parser = new ScriptParserImpl();
		List<Action> actions = parser.parse(file);
		executeScript(actions, startDate, factor);
	}

	private void executeScript(List<Action> actions, final Date startDate, final int factor) {
		try {
			simulationManager.removeAllUsers();
			cancelTask();

			scheduler.schedule(new Runnable() {

				public void run() {
					startClock(startDate, factor);

				}
			}, 0, TimeUnit.MILLISECONDS);

			for (Action action : actions) {
				ScheduledFuture futureTask = scheduler.schedule(action, action.getDelay(), TimeUnit.MILLISECONDS);
				tasks.add(futureTask);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopExecution() {
		cancelTask();
	}

	public void bindDevice(GenericDevice device) {
		if (devices == null)
			devices = new HashMap<String, GenericDevice>();
		devices.put(device.getSerialNumber(), device);
	}

	public void unbindDevice(GenericDevice device) {
		devices.remove(device.getSerialNumber());
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
	
	

	/**
	 * 
	 */
	private void startClock(final Date startDate, final int factor) {
		clock.setFactor(factor);
		clock.setStartDate(startDate.getTime());
	}

	/**
    * 
    */
	private void cancelTask() {
		if (tasks != null && tasks.size() > 0) {
			for (ScheduledFuture futureTask : tasks) {
				if (!futureTask.isDone()) {
					futureTask.cancel(false);
				}
			}
			tasks.clear();
		}
	}

	/**
	 * @return the devices
	 */
	public Map<String, GenericDevice> getDevices() {
		return devices;
	}

	/**
	 * @return the environmentManager
	 */
	public SimulationManager getSimulationManager() {
		return simulationManager;
	}

	public void stop() {
		scheduler.shutdownNow();
	}


	class ScriptParserImpl implements ScriptParser {

		private String startdateStr;

		private int factor = 360;

		public List<Action> parse(File file) {
			System.out.println("Parsing the file " + file.getName());

			List<Action> list = new ArrayList<Action>();

			try {
				// Create a builder factory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);

				// Create the builder and parse the file
				Document doc = factory.newDocumentBuilder().parse(file);

				Node root = doc.getFirstChild();
				if (root.getNodeName().equals("behavior")) {

					Element rootElement = (Element) root;
					startdateStr = rootElement.getAttribute("startdate");
					String factorStr = rootElement.getAttribute("factor");
					try {
						factor = Integer.parseInt(factorStr);
					} catch (NumberFormatException e) {

					}

					NodeList nodeList = root.getChildNodes();

					int delay = 0;
					Action action = null;
					for (int i = 0; i < nodeList.getLength(); i++) {
						// Get child node
						Node childNode = nodeList.item(i);
						JSONObject param = null;
						if (childNode.getNodeName().equals("move")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("location", element.getAttribute("room"));
								param.put("person", element.getAttribute("person"));
								action = new MovePersonAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("modify-environment")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("environmentId", element.getAttribute("room"));
								param.put("variable", element.getAttribute("variable"));
								param.put("value", element.getAttribute("value"));
								action = new ModifyEnvironmentAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("add-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								param.put("deviceType", element.getAttribute("type"));
								action = new AddDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("remove-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								action = new RemoveDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("move-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								param.put("location", element.getAttribute("room"));
								action = new MoveDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("fault-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								action = new FaultDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else if (childNode.getNodeName().equals("repair-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								action = new RepairDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else if (childNode.getNodeName().equals("activate-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								action = new ActivateDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (childNode.getNodeName().equals("modify-device-value")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								param.put("variable", element.getAttribute("variable"));
								param.put("value", element.getAttribute("value"));
								action = new ModifyDeviceValueAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else if (childNode.getNodeName().equals("deactivate-device")) {
							Element element = (Element) childNode;

							param = new JSONObject();
							try {
								param.put("deviceId", element.getAttribute("deviceID"));
								action = new DeactivateDeviceAction(ScriptExecutorImpl.this, delay);
								action.configure(param);
								list.add(action);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else if (childNode.getNodeName().equals("delay")) {
							Element element = (Element) childNode;
							String delayStr = element.getAttribute("value");
							delay += (Integer.valueOf(delayStr).intValue() * 60 * 1000) / factor;
							logger.info("<<<<<<DELAY>>>>> " + Integer.valueOf(delayStr).intValue() * 60 * 1000);
						}

					}

				}

				logger.info("Ready to be executed .......... ");

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return list;
		}

		public Date getStartDate() {
			if (startdateStr != null && (!startdateStr.isEmpty())) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				Date startDate = null;

				try {
					startDate = formatter.parse(startdateStr);
				} catch (ParseException e) {
					// e.printStackTrace();
				} finally {
					if (startDate == null)
						startDate = new Date(System.currentTimeMillis());
				}
				return startDate;
			} else
				return new Date(System.currentTimeMillis());
		}

		public int getFactor() {
			return factor;
		}
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

	public void bindDeviceFactory(final Factory factory) {
		factories.put(factory.getName(), factory);
	}

	public void unbindDeviceFactory(final Factory factory) {
		factories.remove(factory.getName());
	}

	public Factory getFactory(String name) {
		return factories.get(name);
	}

}
