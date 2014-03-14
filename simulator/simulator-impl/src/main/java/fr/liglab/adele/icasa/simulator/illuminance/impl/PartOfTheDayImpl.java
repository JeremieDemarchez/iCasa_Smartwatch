/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.illuminance.impl;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.joda.time.DateTime;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aygalinc on 11/03/14.
 */
//Define this class as an implementation of a component :
@Component(name = "part-of-the-day")
//Create an instance of the component
@Instantiate(name = "part-of-the-day-1")
@Provides
public class PartOfTheDayImpl implements PartOfTheDayService, PeriodicRunnable {

    /**
     * The current moment of the day :
     **/
    PartOfTheDay currentMomentOfTheDay = PartOfTheDay.MORNING;

    /**
     * The current moment of the day :
     **/
    Set<PartOfTheDayListener> listeners = new HashSet<PartOfTheDayListener>();

    @Requires
    Clock clock;

    // Implementation of the MomentOfTheDayService ....
    @Override
    public PartOfTheDay getMomentOfTheDay(){
        return currentMomentOfTheDay;
    }

    @Override
    public void register(PartOfTheDayListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(PartOfTheDayListener listener) {
        listeners.remove(listener);
    }

    // Implementation ot the PeriodicRunnable ...

    public long getPeriod(){
        // The service will be periodically called every hour.
        return 3600 * 1000 ;
    }

    public String getGroup(){
        return "default-2"; // you don't need to understand this part.
    }


    @Override
    public void run() {
        try{ // The method run is called on a regular basis
            // TODO : do something to check the current time of the day and see if
            // it has changed
            System.out.println("exec ");
            DateTime dateTimeEli =new DateTime(clock.currentTimeMillis());
            int hour = dateTimeEli.getHourOfDay();
            PartOfTheDay temp = currentMomentOfTheDay;
            currentMomentOfTheDay = temp.getCorrespondingMoment(hour);
            if (currentMomentOfTheDay != temp ){
                for(PartOfTheDayListener listener : listeners){
                    listener.momentOfTheDayHasChanged(currentMomentOfTheDay);
                }
            }
        }catch(Exception e){
e.printStackTrace();
        }

    }

    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
    }

    /** Component Lifecycle Method */

    @Validate
    public void start() {
    }





}
