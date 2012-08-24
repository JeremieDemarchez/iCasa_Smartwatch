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

import fr.liglab.adele.icasa.clock.api.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gabriel Pedraza Ferreira
 *
 */
public class SimulatedClockImpl implements Clock {

   private long realDate;
   
   private long fakeDate;
   
   private int factor;
   
   
	private static final Logger logger = LoggerFactory.getLogger(SimulatedClockImpl.class);
   
   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#currentTimeMillis()
    */
   public long currentTimeMillis() {
      long diference = (System.currentTimeMillis() - realDate) * factor;
      long temp = fakeDate + (diference);      
      //logger.info("+++++++ " + temp);
      return temp; 
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#getFactor()
    */
   public int getFactor() {
      return factor;
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setFactor(int)
    */
   public void setFactor(int factor) {
      this.factor = factor;
   }

   /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setStartDate(long)
    */
   public void setStartDate(long startDate) {
      this.fakeDate = startDate;
      this.realDate = System.currentTimeMillis();
   }
   
   public void start() {
   	long startDate = System.currentTimeMillis();
      this.fakeDate = startDate;
      this.realDate = startDate;
   }

}
