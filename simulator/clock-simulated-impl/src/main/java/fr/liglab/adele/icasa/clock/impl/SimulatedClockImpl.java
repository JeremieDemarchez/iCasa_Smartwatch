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
package fr.liglab.adele.icasa.clock.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.clock.api.Clock;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
public class SimulatedClockImpl implements Clock {

	private long initDate;

	private volatile long elapsedTime;

	private int factor;

	private boolean pause = true;
	
	private static final int TIME_THREAD_STEEP = 20;

	Thread timeThread;

	private static final Logger logger = LoggerFactory.getLogger(SimulatedClockImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#currentTimeMillis()
	 */
	public long currentTimeMillis() {
		return initDate + elapsedTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#getFactor()
	 */
	public int getFactor() {
		return factor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setFactor(int)
	 */
	public void setFactor(int factor) {
		this.factor = factor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setStartDate(long)
	 */
	public void setStartDate(long startDate) {
		initDate = startDate;		
	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public void resume() {
		pause = false;		
	}
		

	@Override
	public void reset(){
		pause();
		elapsedTime = 0;		
	}
	
	@Override
	public long getElapsedTime() {
	   return elapsedTime;
	}
	
	public void start() {
		
		timeThread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean execute = true;
				while (execute) {					
					try {						
						long enterTime = System.currentTimeMillis();						
						Thread.sleep(TIME_THREAD_STEEP);						
						if (!pause) {
							long realElapsedTime = System.currentTimeMillis() - enterTime;
							elapsedTime += realElapsedTime * factor;
						}							
					} catch (InterruptedException e) {
						execute = false;
					}
				}
			}
		});
		timeThread.start();
	}
	
	public void stop() {
		try {
			timeThread.interrupt();
	      timeThread.join();
      } catch (InterruptedException e) {
	      e.printStackTrace();
      }
	}

}
