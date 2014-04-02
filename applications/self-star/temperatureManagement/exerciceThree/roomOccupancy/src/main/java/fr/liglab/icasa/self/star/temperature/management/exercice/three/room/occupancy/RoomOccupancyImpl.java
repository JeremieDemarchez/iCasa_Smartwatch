package fr.liglab.icasa.self.star.temperature.management.exercice.three.room.occupancy;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.ClockListener;
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Created by aygalinc on 20/03/14.
 */
@Component(name="RoomOccupancyImpl")
@Instantiate(name="RoomOccupancyImpl-0")
@Provides(specifications = {RoomOccupancy.class,PeriodicRunnable.class})
@CommandProvider(namespace = "roomOccupancy")
public class RoomOccupancyImpl implements RoomOccupancy,PeriodicRunnable,ClockListener,DeviceListener {

    private List<String> listOfZone ;

    Map<String,Map<Integer,Double>> mapProbaPerRoomPerMinute =new HashMap<String, Map<Integer, Double>>();

    Set<RoomOccupancyListener> listenerList = new HashSet<RoomOccupancyListener>();
    /**
     * The name of the LOCATION property
     */
    public static final String LOCATION_PROPERTY_NAME = "Location";

    private  double threshold = 0.2;

    @Requires
    private Clock clock;

    /** Field for presenceSensors dependency */
    @RequiresDevice(id="presenceSensors", type="field", optional=true)
    private PresenceSensor[] presenceSensors;

    private long startCurrentMilli ;

    private DateTime startDate;

    private int yearOfStart;

    private int dayOfStart;

    private int minuteOfStart;

    private int lastUpdate = -1 ;

    private final Object m_lock ;

    RoomOccupancyImpl(){
        m_lock = new Object();
    }

    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        System.out.println("Component is stopping...");


    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        System.out.println("Component is starting...");

        startDate = new DateTime(clock.currentTimeMillis());
        yearOfStart = startDate.getYear();
        dayOfStart = startDate.getDayOfYear();
        minuteOfStart = startDate.getMinuteOfDay();
        listOfZone = new ArrayList<String>();
        listOfZone.add("kitchen");
        listOfZone.add("bedroom");
        listOfZone.add("livingroom");
        listOfZone.add("bathroom");

        for(String location : listOfZone){
            Map<Integer,Double> tempMap = new HashMap<Integer, Double>();
            for(int i = 0 ; i < 1439 ; i ++){
                tempMap.put(i,0.0d);
            }
            mapProbaPerRoomPerMinute.put(location,tempMap);
        }
    }

    @Override
    public double getRoomOccupancy(int minuteOfTheDay, String room) {
        return mapProbaPerRoomPerMinute.get(room).get(minuteOfTheDay);
    }

    @Override
    public void addListener(RoomOccupancyListener roomOccupancyListener) {
        listenerList.add(roomOccupancyListener);
    }

    @Override
    public void removeListener(RoomOccupancyListener roomOccupancyListener) {
        listenerList.remove(roomOccupancyListener);
    }

    @Override
    public long getPeriod() {
        return 60000;
    }

    @Override
    public String getGroup() {
        return "Room-Occupancy-Thread";
    }

    @Override
    public void run() {
        long updateTime = clock.currentTimeMillis();
        DateTime date = new DateTime(updateTime);
        int minuteOfUpdate = date.getMinuteOfDay();
        if(lastUpdate != minuteOfUpdate){
            int dayOfUpdate = date.getDayOfYear();

            int yearOfUpdate = date.getYear();


            synchronized (m_lock){

                double ponderation = 1 + ((dayOfUpdate - dayOfStart) + 360*(yearOfUpdate-yearOfStart)) ;
                for(String location : mapProbaPerRoomPerMinute.keySet()){
                    Map<Integer,Double> tempMap = mapProbaPerRoomPerMinute.get(location);

                    if (presenceFromLocation(location)){
                        if ( ponderation != 0){
                            boolean up;
                            if (tempMap.get(minuteOfUpdate) >=  threshold ){
                                up = true;
                            }else {
                                up = false;
                            }
                            double newProba = tempMap.get(minuteOfUpdate)*((ponderation-1)/ponderation) + (1/ponderation);
                            tempMap.put(minuteOfUpdate,newProba);

                            if (newProba >= threshold){
                                if (!up){
                                    for(RoomOccupancyListener listener : listenerList){
                                        listener.occupancyCrossUpThreshold(location);
                                    }
                                }
                            }else{
                                if (up){
                                    for(RoomOccupancyListener listener : listenerList){
                                        listener.occupancyCrossDownThreshold(location);
                                    }
                                }
                            }
                        }
                    }else{
                        if ( ponderation != 0){

                            boolean up;
                            if (tempMap.get(minuteOfUpdate) >=  threshold ){
                                up = true;
                            }else {
                                up = false;
                            }

                            double newProba = tempMap.get(minuteOfUpdate)*((ponderation-1)/ponderation);
                            tempMap.put(minuteOfUpdate,newProba);

                            if (newProba >= threshold){
                                if (!up){
                                    for(RoomOccupancyListener listener : listenerList){
                                        listener.occupancyCrossUpThreshold(location);
                                    }
                                }
                            }else{
                                if (up){
                                    for(RoomOccupancyListener listener : listenerList){
                                        listener.occupancyCrossDownThreshold(location);
                                    }
                                }
                            }
                        }
                    }
                }
                lastUpdate = minuteOfUpdate;
            }
        }
    }




    private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
        List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
        for (PresenceSensor presenceSensor : presenceSensors) {
            if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
                presenceSensorLocation.add(presenceSensor);
            }
        }
        return presenceSensorLocation;
    }

    private synchronized boolean presenceFromLocation(String location) {
        int switchOn = 0;
        int switchOff = 0;
        List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
        presenceSensorLocation = getPresenceSensorFromLocation(location);
        for (PresenceSensor presenceSensor : presenceSensorLocation) {
            if (presenceSensor.getSensedPresence()) {
                switchOn +=1;
            }
            else{
                switchOff +=1;
            }
        }
        if (switchOn > switchOff){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void factorModified(int oldFactor) {

    }

    @Override
    public void startDateModified(long oldStartDate) {
        resetClock();
    }

    @Override
    public void clockPaused() {

    }

    @Override
    public void clockResumed() {

    }

    @Override
    public void clockReset() {
        resetClock();
    }

    private synchronized void resetClock(){
        startDate = new DateTime(clock.currentTimeMillis());
        yearOfStart = startDate.getYear();
        dayOfStart = startDate.getDayOfYear();
        minuteOfStart = startDate.getMinuteOfDay();
        lastUpdate = -10;

        for(String location : listOfZone){
            Map<Integer,Double> tempMap = new HashMap<Integer, Double>();
            for(int i = 0 ; i < 1439 ; i ++){
                tempMap.put(i,0.0d);
            }
            mapProbaPerRoomPerMinute.put(location,tempMap);
        }
    }

    @Override
    public void deviceAdded(GenericDevice device) {

    }

    @Override
    public void deviceRemoved(GenericDevice device) {

    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {

    }

    @Override
    public void devicePropertyAdded(GenericDevice device, String propertyName) {

    }

    @Override
    public void devicePropertyRemoved(GenericDevice device, String propertyName) {

    }

    @Override
    public void deviceEvent(GenericDevice device, Object data) {

    }
    @Command
    public void roomOccupancy(String location , int minute ) {
        System.out.println(" OCCUPANCY OF  " + location + " IS " + getRoomOccupancy(minute, location));
    }
}
