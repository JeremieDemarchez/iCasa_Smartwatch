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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.command.ICommandService;

public class CommandExecutor {

	private List<ActionDescription> actionDescriptions;

	private Clock clock;

	private Thread executorThread;

	private long startDate;

	ScriptExecutorImpl scriptExecutorImpl;

	public CommandExecutor(ScriptExecutorImpl scriptExecutorImpl) {
		this.scriptExecutorImpl = scriptExecutorImpl;
		this.clock = scriptExecutorImpl.getClock();
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public void start() {
		executorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				int index = 0;
				boolean execute = true;
				while (execute) {
					
					List<ActionDescription> toExecute = new ArrayList<ActionDescription>();
					long elapsedTime = clock.getElapsedTime();
					//System.out.println("\t\tDelay ---> " + elapsedTime);
					for (int i = index; i < actionDescriptions.size(); i++) {
						ActionDescription action = actionDescriptions.get(i);
						int actionDelay = action.getDelay() * 60 * 1000;
						if (elapsedTime >= actionDelay)
							toExecute.add(action);
						else
							break;
					}

					index += toExecute.size();

					if (index >= actionDescriptions.size() - 1)
						execute = false;
					
					if (!toExecute.isEmpty()) {
						clock.pause();
						System.out.println("\t\tInit time ---> " + getDate(clock.currentTimeMillis()));
						System.out.println("\t\tTo Execute ---> " + toExecute.size() + " Actions");
						for (ActionDescription actionDescription : toExecute) {
							ICommandService command = scriptExecutorImpl.getCommand(actionDescription.getCommandName());
							if (command != null) {
								try {
									command.execute(null, null, actionDescription.getConfiguration());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						clock.resume();
						System.out.println("\t\tEnd time ---> " +  getDate(clock.currentTimeMillis()));
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						execute = false;
					}
				}

			}
			
			private String getDate(long timeInMs) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
				return format.format(new Date(timeInMs));
			}
			
		});

		clock.setStartDate(startDate);
		clock.resume();
		executorThread.start();

	}

	public void stop() {

	}

	public void setActionDescriptions(List<ActionDescription> actionDescriptions) {
		this.actionDescriptions = actionDescriptions;
	}

}
