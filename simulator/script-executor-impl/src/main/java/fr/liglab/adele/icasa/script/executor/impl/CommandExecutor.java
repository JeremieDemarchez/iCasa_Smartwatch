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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.script.executor.SimulatorCommand;

public class CommandExecutor {


	private List<ActionDescription> actionDescriptions;

	private Clock clock;

	private Thread executorThread;

	private long startDate;
	
	private boolean paused = false;

	ScriptExecutorImpl scriptExecutorImpl;

	public CommandExecutor(ScriptExecutorImpl scriptExecutorImpl) {
		this.scriptExecutorImpl = scriptExecutorImpl;
		this.clock = scriptExecutorImpl.getClock();
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public void start() {			
		if (actionDescriptions.isEmpty()) // Nothing to execute
			return;
		
				
		executorThread = new Thread(new CommandExecutorRunnable());

		clock.setStartDate(startDate);
		clock.resume();
		executorThread.start();

	}

	public void stop() {
		System.out.println("Stopping executor");
		try {
			executorThread.interrupt();
	      executorThread.join();
	      clock.reset(); // Stop the clock
	      actionDescriptions.clear(); // Clear the activities to execute
      } catch (InterruptedException e) {
	      e.printStackTrace();
      }
	}
	
	public void pause() {
		synchronized (clock) {
			clock.pause();
			paused = true;
      }
	}
	
	public void resume() {
		synchronized (clock) {
			clock.resume();
			paused = false;
      }
	}

	public void setFactor(int factor) {
		clock.setFactor(factor);
	}
	
	public void setActionDescriptions(List<ActionDescription> actionDescriptions) {
		this.actionDescriptions = actionDescriptions;
	}
	
   public int getState() {
   	if (executorThread != null)
   		if (executorThread.isAlive())
   			if (!paused)
   				return ScriptExecutor.EXECUTING;
   			else
   				return ScriptExecutor.PAUSED;
	   return ScriptExecutor.STOPPED;
   }
	
	private final class CommandExecutorRunnable implements Runnable {
	   @Override
	   public void run() {
	   	int index = 0;
	   	boolean execute = true;
	   	while (execute) {
	   		
	   		//List<ActionDescription> toExecute = new ArrayList<ActionDescription>();
	   		long elapsedTime = clock.getElapsedTime();
	   		
	   		List<ActionDescription> toExecute = calculeToExecute(index, elapsedTime);
	   		/*
	   		for (int i = index; i < actionDescriptions.size(); i++) {
	   			ActionDescription action = actionDescriptions.get(i);
	   			int actionDelay = action.getDelay() * 60 * 1000;
	   			if (elapsedTime >= actionDelay)
	   				toExecute.add(action);
	   			else
	   				break;
	   		}
	   		*/
	   		

	   		index += toExecute.size();

	   		if (index >= actionDescriptions.size())
	   			execute = false;
	   		
	   		executeActions(toExecute);
	   		
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
   			int actionDelay = action.getDelay() * 60 * 1000;
   			if (elapsedTime >= actionDelay)
   				toExecute.add(action);
   			else
   				break;
   		}
   		return toExecute;
	   }
	   
	   private void executeActions(List<ActionDescription> toExecute) {
   		if (!toExecute.isEmpty()) {
   			System.out.println("\t\tInit time ---> " + getDate(clock.currentTimeMillis()));
   			System.out.println("\t\tTo Execute ---> " + toExecute.size() + " Actions");
   			synchronized (clock) {
      			clock.pause();
      			for (ActionDescription actionDescription : toExecute) {
      				SimulatorCommand command = scriptExecutorImpl.getCommand(actionDescription.getCommandName());
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
   			System.out.println("\t\tEnd time ---> " +  getDate(clock.currentTimeMillis()));
   		}
	   }
	   
	   private String getDate(long timeInMs) {
	   	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
	   	return format.format(new Date(timeInMs));
	   }
   }


}
