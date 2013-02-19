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
package fr.liglab.adele.icasa.clock.system.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.clock.api.ClockListener;

/**
 * @author Gabriel Pedraza Ferreira
 *
 */

@Component(name="SystemClock")
@Provides
public class SystemClockImpl implements Clock {

   
	
	private long initDate;
	
   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#currentTimeMillis()
    */
   public long currentTimeMillis() {     
      return System.currentTimeMillis(); 
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#getFactor()
    */
   public int getFactor() {
      return 1;
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setFactor(int)
    */
   public void setFactor(int factor) {
      // do nothing, cannot change factor or system clock
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setStartDate(long)
    */
   public void setStartDate(long startDate) {
   	initDate = startDate;
   }

	@Override
   public void pause() {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void resume() {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void reset() {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public long getElapsedTime() {
	   return currentTimeMillis() - initDate;
   }

    public long getStartDate() {
   	 return initDate;
    }

	@Override
   public boolean isPaused() {
	   return false;
   }

	@Override
   public void addListener(ClockListener listener) {

	   
   }
	
	@Override
	public void removeListener(ClockListener listener) {
	   
	}

}
