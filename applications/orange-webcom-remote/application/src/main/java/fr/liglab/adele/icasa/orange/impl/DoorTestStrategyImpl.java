package fr.liglab.adele.icasa.orange.impl;

import fr.liglab.adele.cream.event.handler.annotation.ContextUpdate;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
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
    public List<DoorWindowSensor> doorWindowSensors;

    @Requires(filter = "(name=" + ManagedScheduledExecutorService.SYSTEM + ")", proxy = false)
    ManagedScheduledExecutorService scheduler;

    private final Map<String,ManagedFutureTask> futureTaskMap = new ConcurrentHashMap<>();
    @ContextUpdate(specification = DoorWindowSensor.class,stateId = DoorWindowSensor.DOOR_WINDOW_SENSOR_OPENING_DETECTCION)
    public void updateDoorSensor(DoorWindowSensor doorWindowSensor,Object newP,Object old){
        if (isTestRunning(doorWindowSensor)){
            String id = getZwaveIdFromDoorSensor(doorWindowSensor);
            if (futureTaskMap.get(id) != null){
                futureTaskMap.remove(id).cancel(true);
            }
            finishTest(getZwaveIdFromDoorSensor(doorWindowSensor),ZwaveTestResult.SUCCESS);
        }
    }

    @Invalidate
    public void invalidate(){
        super.stop();
    }

    @Override
    public void beginTest(String nodeId, BiConsumer<String, ZwaveTestResult> callback, boolean interrupt) throws TestRunningException {
        super.beginTest(nodeId, callback, interrupt);

        futureTaskMap.put(nodeId, scheduler.schedule(()-> finishTest(nodeId,ZwaveTestResult.FAILED),20, TimeUnit.SECONDS));
    }
    @Override
    public List<String> getTestTargets() {
        List<String> returnList = new ArrayList<>();

        if (doorWindowSensors != null){
            return returnList;
        }
        for (DoorWindowSensor doorWindowSensor : doorWindowSensors){
            if (doorWindowSensor instanceof ZwaveDevice){
                returnList.add(String.valueOf( ((ZwaveDevice)doorWindowSensor).getNodeId()));
            }
        }
        return returnList;
    }

    private boolean isTestRunning(DoorWindowSensor doorWindowSensor){
        if (! (doorWindowSensor instanceof ZwaveDevice)){
            return false;
        }
        if (getLastTestResult(getZwaveIdFromDoorSensor(doorWindowSensor) ).equals(ZwaveTestResult.RUNNING) ){
            return true;
        }
        return false;
    }

    private String getZwaveIdFromDoorSensor(DoorWindowSensor doorWindowSensor){
        return String.valueOf(((ZwaveDevice)doorWindowSensor).getNodeId());
    }
}
