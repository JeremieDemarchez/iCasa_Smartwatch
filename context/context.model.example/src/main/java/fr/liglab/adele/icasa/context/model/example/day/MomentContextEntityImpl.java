package fr.liglab.adele.icasa.context.model.example.day;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@Entity(spec = MomentOfTheDayService.class)
public class MomentContextEntityImpl implements ContextEntity, MomentOfTheDayService, PeriodicRunnable{

    private static final Logger LOG = LoggerFactory.getLogger(MomentContextEntityImpl.class);

    /**
     * The current moment of the day :
     **/
    MomentOfTheDay currentMomentOfTheDay = MomentOfTheDay.MORNING;

    @Requires(specification = Clock.class, id = "clock", optional = false)
    Clock clock;

    @ServiceProperty(name = "context.entity.id", mandatory = true, value = "MomentOfTheDay")
    private String name;

    @Pull(state = MOMENT_OF_THE_DAY)
    Function getMomentOfTheDay = (Object obj) ->{
        return getMomentOfTheDay();
    };


    @Validate
    public void start(){
        DateTime dateTimeEli =new DateTime(clock.currentTimeMillis());
        int hour = dateTimeEli.getHourOfDay();
        MomentOfTheDay temp = currentMomentOfTheDay;
        currentMomentOfTheDay = temp.getCorrespondingMoment(hour);
        if (currentMomentOfTheDay != temp ){
            pushState(MOMENT_OF_THE_DAY,currentMomentOfTheDay);
        }
    }

    @Invalidate
    public void stop(){

    }


    @Override
    public MomentOfTheDay getMomentOfTheDay(){
        return currentMomentOfTheDay;
    }

    @Override
    public long getPeriod(){
        // The service will be periodically called every hour.
        return 1 ;
    }

    @Override
    public TimeUnit getUnit() {
        return TimeUnit.HOURS;
    }

    @Override
    public void run() {
        DateTime dateTimeEli =new DateTime(clock.currentTimeMillis());
        int hour = dateTimeEli.getHourOfDay();
        MomentOfTheDay temp = currentMomentOfTheDay;
        currentMomentOfTheDay = temp.getCorrespondingMoment(hour);
        if (currentMomentOfTheDay != temp ){
            pushState(MOMENT_OF_THE_DAY,currentMomentOfTheDay);
        }
    }












    private final Map<String,Object> injectedState = new HashMap<>();

    private final Map<String,Object> injectedExtensionState =new HashMap<>();

    @Override
    public String getId() {
        return name;
    }

    @Override
    public Object getStateValue(String property) {
        return injectedState.get(property);
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getState() {
        return Collections.unmodifiableMap(injectedState);
    }

    @Override
    public Object getStateExtensionValue(String property) {
        return injectedExtensionState.get(property);
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return Collections.unmodifiableMap(injectedExtensionState);
    }

    @Override
    public void pushState(String state, Object value) {

    }
}