package fr.liglab.icasa.self.star.temperature.management.exercice.two.temperature.manager;

import fr.liglab.adele.icasa.clock.Clock;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by aygalinc on 20/03/14.
 */
@Component(name="temperatureManager")
@Instantiate(name="temperatureManagerImpl-0")
@Provides
public class TemperatureManagerAdministrationImpl implements TemperatureManagerAdministration {

    @Requires
    private Clock clock;

    private Duration duration = Duration.millis(5*60*1000); //Five minutes

    /**
     * The name of the location for unknown value
     */
    private Map<String,Double> mapTemperatureTarget  ;


    private long lastUpdateHight = 0 ;

    private long lastUpdatelow =0 ;

    @Override
    public void temperatureIsTooHigh(String roomName) {
        long time = clock.currentTimeMillis();
        if (time >= lastUpdateHight){
            long periodOfUpdate =  (time - lastUpdateHight);
            Duration durationTemp = Duration.millis(periodOfUpdate);
            if (durationTemp.isLongerThan(durationTemp)){

            }
        }else{
            lastUpdateHight = time;
        }
    }

    @Override
    public void temperatureIsTooLow(String roomName) {
        long time = clock.currentTimeMillis();
        if (time >= lastUpdatelow){
            long periodOfUpdate =  (time - lastUpdatelow);
            Duration durationTemp = Duration.millis(periodOfUpdate);
            if (durationTemp.isLongerThan(durationTemp)){

            }
        }else{
            lastUpdatelow = time;
        }
    }

    public TemperatureManagerAdministrationImpl() {

        mapTemperatureTarget = new HashMap<String, Double>();

        mapTemperatureTarget.put("kitchen",288.15);
        mapTemperatureTarget.put("livingroom",291.15);
        mapTemperatureTarget.put("bedroom",293.15);
        mapTemperatureTarget.put("bathroom",296.15);
    }


}
