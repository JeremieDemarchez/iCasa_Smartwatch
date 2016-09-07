package fr.liglab.adele.icasa.orange.impl;

import fr.liglab.adele.cream.facilities.ipojo.annotation.ContextRequirement;
import fr.liglab.adele.cream.facilities.ipojo.annotation.ContextUpdate;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
import fr.liglab.adele.icasa.orange.service.TestReport;
import fr.liglab.adele.icasa.orange.service.TestRunningException;
import fr.liglab.adele.icasa.orange.service.ZwaveTestResult;
import fr.liglab.adele.icasa.orange.service.ZwaveTestStrategy;
import fr.liglab.adele.icasa.orange.utils.AbstractZwaveTestStrategy;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import org.apache.felix.ipojo.annotations.*;
import org.wisdom.api.concurrent.ManagedFutureTask;
import org.wisdom.api.concurrent.ManagedScheduledExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Component
@Instantiate
@Provides(specifications = ZwaveTestStrategy.class)
public class DoorTestStrategyImpl extends AbstractZwaveTestStrategy implements ZwaveTestStrategy {

    @Requires(specification = DoorWindowSensor.class,proxy = false)
    @ContextRequirement(spec = ZwaveDevice.class)
    public List<DoorWindowSensor> doorWindowSensors;

    @Requires(filter = "(name=" + ManagedScheduledExecutorService.SYSTEM + ")", proxy = false)
    ManagedScheduledExecutorService scheduler;

    private final Map<String,ManagedFutureTask> futureTaskMap = new ConcurrentHashMap<>();

    @ContextUpdate(specification = DoorWindowSensor.class,stateId = DoorWindowSensor.DOOR_WINDOW_SENSOR_OPENING_DETECTCION)
    public void updateDoorSensor(DoorWindowSensor doorWindowSensor,Object newP,Object old){
        System.out.println(" test Event detected");
        if (isTestRunning(doorWindowSensor)){
            System.out.println(" test Event must be annalyzed");
            String id = getZwaveIdFromDoorSensor(doorWindowSensor);
            if (futureTaskMap.get(id) != null){
                futureTaskMap.remove(id).cancel(true);
            }
            finishTest(getZwaveIdFromDoorSensor(doorWindowSensor),ZwaveTestResult.SUCCESS,"success");
        }
    }

    @Invalidate
    public void invalidate(){
        super.stop();
    }

    @Override
    public void beginTest(String nodeId, BiConsumer<String, TestReport> callback, boolean interrupt) throws TestRunningException {
        super.beginTest(nodeId, callback, interrupt);

        futureTaskMap.put(nodeId, scheduler.schedule(()-> finishTest(nodeId,ZwaveTestResult.FAILED,"No event detected during 20 seconds"),20, TimeUnit.SECONDS));
    }

    @Override
    public String getStrategyName() {
        return "Zwave DoorSensor Test Strategy";
    }

    @Override
    public List<String> getTestTargets() {
        List<String> returnList = new ArrayList<>();

        if (doorWindowSensors == null){
            return returnList;
        }
        for (DoorWindowSensor doorWindowSensor : doorWindowSensors){
                returnList.add(String.valueOf( ((ZwaveDevice)doorWindowSensor).getNodeId()));
        }
        return returnList;
    }

    private boolean isTestRunning(DoorWindowSensor doorWindowSensor){
        if (getLastTestResult(getZwaveIdFromDoorSensor(doorWindowSensor) ).equals(ZwaveTestResult.RUNNING) ){
            return true;
        }
        return false;
    }

    private String getZwaveIdFromDoorSensor(DoorWindowSensor doorWindowSensor){
        return String.valueOf(((ZwaveDevice)doorWindowSensor).getNodeId());
    }
}
