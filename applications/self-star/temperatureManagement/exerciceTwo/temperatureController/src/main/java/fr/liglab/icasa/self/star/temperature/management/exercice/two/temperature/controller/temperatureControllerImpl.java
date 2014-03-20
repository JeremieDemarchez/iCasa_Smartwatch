package fr.liglab.icasa.self.star.temperature.management.exercice.two.temperature.controller;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by aygalinc on 19/03/14.
 */
@Component(name="temperatureController")
@Instantiate(name="temperatureControllerImpl-0")
@Provides
public class temperatureControllerImpl implements PeriodicRunnable,DeviceListener,TemperatureConfiguration{

    /**
     * The name of the LOCATION property
     */
    public static final String LOCATION_PROPERTY_NAME = "Location";

    /**
     * The name of the location for unknown value
     */
    public static final String LOCATION_UNKNOWN = "unknown";


    private  Map<String,Double> mapTemperatureTarget  ;

    private Object m_lock ;


    /** Field for thermometer dependency */
    @RequiresDevice(id="thermometers", type="field", optional=true)
    private Thermometer[] thermometers;

    /** Field for heaters dependency */
    @RequiresDevice(id="heaters", type="field", optional=true)
    private Heater[] heaters;

    /** Field for coolers dependency */
    @RequiresDevice(id="coolers", type="field", optional=true)
    private Cooler[] coolers;

    public temperatureControllerImpl() {
        m_lock = new Object();
        mapTemperatureTarget = new HashMap<String, Double>();

        mapTemperatureTarget.put("kitchen",288.15);
        mapTemperatureTarget.put("livingroom",291.15);
        mapTemperatureTarget.put("bedroom",293.15);
        mapTemperatureTarget.put("bathroom",296.15);
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
    }

    @Override
    public long getPeriod() {
        return 60000;
    }

    @Override
    public String getGroup() {
        return "Tcontroller";
    }

    @Override
    public void run() {
        System.out.println(" TEMP RUN ");
        synchronized (m_lock){
            System.out.println(" SYNCHRO  " );
            Map<String,Double> temperatureMap = new HashMap<String,Double>();
            temperatureMap = temperatureAverageInAllZone();
            for(String zoneId : temperatureMap.keySet()){
                System.out.println(" ZONE " + zoneId + " TEMp " + temperatureMap.get(zoneId));
                double tempInZone = temperatureMap.get(zoneId);
                if (tempInZone > mapTemperatureTarget.get(zoneId) +1 ){
                    System.out.println(" TEMP SUP ");
                    Set<Cooler> coolerSet = coolerInZone(zoneId);
                    for(Cooler cooler : coolerSet){
                        cooler.setPowerLevel(1);
                    }
                    Set<Heater> heaterSet = heaterInZone(zoneId);
                    for(Heater heater : heaterSet){
                        heater.setPowerLevel(0);
                    }
                }else if (tempInZone < mapTemperatureTarget.get(zoneId) - 1 ){
                    System.out.println(" TEMP INF ");
                    Set<Heater> heaterSet = heaterInZone(zoneId);
                    for(Heater heater : heaterSet){
                        heater.setPowerLevel(1);
                    }
                    Set<Cooler> coolerSet = coolerInZone(zoneId);
                    for(Cooler cooler : coolerSet){
                        cooler.setPowerLevel(0);
                    }
                }else if (tempInZone < mapTemperatureTarget.get(zoneId) - 0.5 ){
                    System.out.println(" TEMP INF ");
                    Set<Heater> heaterSet = heaterInZone(zoneId);
                    for(Heater heater : heaterSet){
                        heater.setPowerLevel(0.1);
                    }
                    Set<Cooler> coolerSet = coolerInZone(zoneId);
                    for(Cooler cooler : coolerSet){
                        cooler.setPowerLevel(0);
                    }
                } else if (tempInZone > mapTemperatureTarget.get(zoneId) + 0.5 ){
                    System.out.println(" TEMP SUP ");
                    Set<Cooler> coolerSet = coolerInZone(zoneId);
                    for(Cooler cooler : coolerSet){
                        cooler.setPowerLevel(0.1);
                    }
                    Set<Heater> heaterSet = heaterInZone(zoneId);
                    for(Heater heater : heaterSet){
                        heater.setPowerLevel(0);
                    }
                }
                else{
                    System.out.println(" TEMP IN BORNe ");
                    Set<Heater> heaterSet = heaterInZone(zoneId);
                    Set<Cooler> coolerSet = coolerInZone(zoneId);
                    for(Heater heater : heaterSet){
                        heater.setPowerLevel(0);
                    }
                    for(Cooler cooler : coolerSet){
                        cooler.setPowerLevel(0);
                    }

                }
            }
        }
    }

    private Map<String,Double> temperatureAverageInAllZone(){
        System.out.println(" AVERAGE " );
        Map<String,Double> returnMap= new HashMap<String,Double>();
        Map<String,Integer> countMap= new HashMap<String,Integer>();
        for(Thermometer thermometer : thermometers){
            String thermometerLocation = (String) thermometer.getPropertyValue(LOCATION_PROPERTY_NAME);
            if (!thermometerLocation.equals(LOCATION_UNKNOWN)) {
                if(returnMap.containsKey(thermometerLocation)){
                    double tempSum = returnMap.get(thermometerLocation);
                    tempSum += thermometer.getTemperature();
                    returnMap.put(thermometerLocation,tempSum);
                    int count = countMap.get(thermometerLocation);
                    count += 1;
                    countMap.put(thermometerLocation,count);
                }else{
                    returnMap.put(thermometerLocation,thermometer.getTemperature());
                    countMap.put(thermometerLocation,1);
                }
            }
        }

        for(String location : returnMap.keySet() ){
            double tempSum = returnMap.get(location);
            int count = countMap.get(location);
            returnMap.put(location,(tempSum/count));
        }
        return returnMap;
    }

    private Set<Cooler> coolerInZone(String zoneId){
        System.out.println(" COOLER IN ZONE " + coolers.length);
        Set<Cooler> coolerSet= new HashSet<Cooler>();
        for(Cooler cooler : coolers){
            String coolerLocation = (String) cooler.getPropertyValue(LOCATION_PROPERTY_NAME);
            System.out.println( " Cooler loc " + coolerLocation);
            if(coolerLocation.equals(zoneId)){
                System.out.println(" ADD COOLER ");
                coolerSet.add(cooler);
            }
        }
        return coolerSet;
    }

    private Set<Heater> heaterInZone(String zoneId){
        System.out.println(" HEATER IN ZONE  " + heaters.length);
        Set<Heater> heaterSet= new HashSet<Heater>();
        for(Heater heater : heaters){
            String heaterLocation = (String) heater.getPropertyValue(LOCATION_PROPERTY_NAME);
            System.out.println(" heater loc " + heaterLocation);
            if(heaterLocation.equals(zoneId)){
                System.out.println(" ADD HEATER ");
                heaterSet.add(heater);
            }
        }
        return heaterSet;
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


    @Override
    public void setTargetedTemperature(String targetedRoom, float temperature) {
        double temp = temperature;
        synchronized (m_lock){
            mapTemperatureTarget.put(targetedRoom,temp);
        }
    }

    @Override
    public float getTargetedTemperature(String room) {
        synchronized (m_lock){
            double temp = mapTemperatureTarget.get(room);
            float temperature = (float) temp;
            return temperature;
        }

    }

}
