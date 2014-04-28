package fr.liglab.icasa.self.star.temperature.management.exercice.four.temperature.manager;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.moment.of.the.day.MomentOfTheDay;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.moment.of.the.day.MomentOfTheDayListener;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.moment.of.the.day.MomentOfTheDayService;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.room.occupancy.RoomOccupancy;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.room.occupancy.RoomOccupancyListener;
import fr.liglab.icasa.self.star.temperature.management.exercice.four.temperature.controller.TemperatureConfiguration;
import org.apache.felix.ipojo.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by aygalinc on 20/03/14.
 */
@Component(name="temperatureManager")
@Instantiate(name="temperatureManagerImpl-0")
@Provides(specifications = TemperatureManagerAdministration.class)
public class TemperatureManagerAdministrationImpl implements TemperatureManagerAdministration,RoomOccupancyListener,MomentOfTheDayListener {

    @Requires
    private Clock clock;

    @Requires
    private TemperatureConfiguration m_configuration;

    @Requires
    private RoomOccupancy m_roomOccupancy;

    @Requires
    private MomentOfTheDayService m_momentOfTheDay;


    private Duration duration = Duration.millis(5*60*1000); //Five minutes

    /**
     * The name of the location for unknown value
     */
    private Map<String,Float> mapTemperatureTarget  = new HashMap<String, Float>() ;

    private  boolean energySavingMode = false;


    private long lastUpdateHight = 0 ;

    private long lastUpdatelow =0 ;

    private double occupancyThreshold = 0.2 ;

    private EnergyGoal energyGoal = EnergyGoal.HIGH ;

    private MomentOfTheDay momentOfTheDay ;


    @Override
    public synchronized void temperatureIsTooHigh(String roomName) {
        if (mapTemperatureTarget.containsKey(roomName)){
            long time = clock.currentTimeMillis();
            if (time >= lastUpdateHight){
                long periodOfUpdate =  (time - lastUpdateHight);
                Duration durationTemp = Duration.millis(periodOfUpdate);
                if (durationTemp.isLongerThan(duration)){
                    float currentTemp = mapTemperatureTarget.get(roomName);
                    mapTemperatureTarget.put(roomName,(currentTemp + 1));
                    lastUpdateHight = time;
                    m_configuration.setTargetedTemperature(roomName,mapTemperatureTarget.get(roomName));
                    System.out.println(" Preferences are now set to " + (currentTemp + 1) + " for " + roomName);
                }else {
                    System.out.println(" Waiting period");
                }
            }else{
                lastUpdateHight = time;
            }
        }else {
            System.out.println(" INVALID ZONE !");
        }
    }

    @Override
    public synchronized void temperatureIsTooLow(String roomName) {
        if (mapTemperatureTarget.containsKey(roomName)){
            long time = clock.currentTimeMillis();
            if (time >= lastUpdatelow){

                long periodOfUpdate =  (time - lastUpdatelow);
                Duration durationTemp = Duration.millis(periodOfUpdate);
                System.out.println(" DUR TEMP " + durationTemp.getStandardSeconds());
                if (durationTemp.isLongerThan(duration)){
                    float currentTemp = mapTemperatureTarget.get(roomName);
                    mapTemperatureTarget.put(roomName,(currentTemp-1));
                    lastUpdateHight = time;
                    m_configuration.setTargetedTemperature(roomName,mapTemperatureTarget.get(roomName));
                    System.out.println(" Preferences are now set to " + (currentTemp-1) + " for " + roomName);
                }else{
                    System.out.println(" Waiting period");
                }
            }else{
                lastUpdatelow = time;
            }
        }else {
            System.out.println(" INVALID ZONE !");
        }
    }

    @Override
    public synchronized void turnOnEnergySavingMode() {
        for(String zoneId : mapTemperatureTarget.keySet()){
            long time = clock.currentTimeMillis();
            DateTime date = new DateTime(time);
            if (m_roomOccupancy.getRoomOccupancy(zoneId,date.getMinuteOfDay()) < occupancyThreshold ){
                m_configuration.turnOn(zoneId);
            }
        }
        energySavingMode = true;
    }

    @Override
    public synchronized void turnOffEnergySavingMode() {
        for(String zoneId : mapTemperatureTarget.keySet()){
            m_configuration.turnOff(zoneId);
        }
        energySavingMode = false;
    }

    @Override
    public synchronized boolean isPowerSavingEnabled() {
        return energySavingMode;
    }

    @Override
    public synchronized void setTemperatureEnergyGoal(EnergyGoal goal) {
        m_configuration.setMaximumAllowedEnergyInRoom(goal.getMaximumEnergyInRoom());
    }

    @Override
    public synchronized EnergyGoal getTemperatureEnergyGoal() {
        return energyGoal;
    }

    @Override
    public synchronized double getRoomOccupancy(String room, int minute) {
        return m_roomOccupancy.getRoomOccupancy(room,minute);
    }

    public TemperatureManagerAdministrationImpl() {

        m_configuration.setMaximumAllowedEnergyInRoom(energyGoal.getMaximumEnergyInRoom());


        mapTemperatureTarget.put("kitchen",288.15f);
        mapTemperatureTarget.put("livingroom",291.15f);
        mapTemperatureTarget.put("bedroom",293.15f);
        mapTemperatureTarget.put("bathroom",296.15f);
        for (String stringLocation : mapTemperatureTarget.keySet()){
            m_configuration.setTargetedTemperature(stringLocation,mapTemperatureTarget.get(stringLocation));
            m_configuration.turnOn(stringLocation);
        }
    }

    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        System.out.println("Component is stopping...");
        m_roomOccupancy.removeListener(this);
        m_momentOfTheDay.unregister(this);
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        System.out.println("Component is starting...");

        m_roomOccupancy.setThreshold(this.occupancyThreshold);
        m_roomOccupancy.addListener(this);
        m_momentOfTheDay.register(this);
        momentOfTheDay = m_momentOfTheDay.getMomentOfTheDay();
    }

    @Override
    public synchronized void occupancyCrossDownThreshold(String room) {
        System.out.println(" CROSS DOWN IN " + room);
        if (energySavingMode){
            m_configuration.turnOff(room);
        }
    }

    @Override
    public synchronized void occupancyCrossUpThreshold(String room) {
        System.out.println(" CROSS UP IN " + room);
        if (energySavingMode){
            m_configuration.turnOn(room);
        }
    }

    @Override
    public synchronized void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
        if(newMomentOfTheDay == MomentOfTheDay.AFTERNOON ){
            for (String room : mapTemperatureTarget.keySet()){
                temperatureIsTooHigh(room);
            }
        }else if (newMomentOfTheDay == MomentOfTheDay.MORNING){
            for (String room : mapTemperatureTarget.keySet()){
                temperatureIsTooLow(room);
            }
        }else if (newMomentOfTheDay == MomentOfTheDay.NIGHT){
            for (String room : mapTemperatureTarget.keySet()){
                temperatureIsTooLow(room);
            }
        }else if (newMomentOfTheDay == MomentOfTheDay.EVENING){
            for (String room : mapTemperatureTarget.keySet()){
                temperatureIsTooHigh(room);
            }
        }
    }
}
