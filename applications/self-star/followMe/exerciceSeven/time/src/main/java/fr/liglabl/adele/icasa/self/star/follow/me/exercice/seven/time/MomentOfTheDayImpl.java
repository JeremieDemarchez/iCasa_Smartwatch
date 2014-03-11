package fr.liglabl.adele.icasa.self.star.follow.me.exercice.seven.time;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * Created by aygalinc on 11/03/14.
 */
//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "follow.me.time")
@Provides
public class MomentOfTheDayImpl implements MomentOfTheDayService, PeriodicRunnable {

    /**
     * The current moment of the day :
     **/
    MomentOfTheDay currentMomentOfTheDay;

    @Requires
    Clock clock;

    // Implementation of the MomentOfTheDayService ....
    @Override
    public MomentOfTheDay getMomentOfTheDay(){
        return currentMomentOfTheDay;
    }

    // Implementation ot the PeriodicRunnable ...

    public long getPeriod(){
        // The service will be periodically called every hour.
        return 3600 * 1000 ;
    }

    public String getGroup(){
        return "default"; // you don't need to understand this part.
    }


    @Override
    public void run() {
        // The method run is called on a regular basis

        // TODO : do something to check the current time of the day and see if
        // it has changed

        System.out.println(clock.currentTimeMillis() );
        System.out.println(clock.currentTimeMillis()/1000/60/60 );
        currentMomentOfTheDay = null; // FIXME : change the value
    }

}
