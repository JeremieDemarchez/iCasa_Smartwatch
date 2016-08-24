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
package fr.liglab.adele.icasa.orange.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.orange.service.TestReport;
import fr.liglab.adele.icasa.orange.service.TestRunningException;
import fr.liglab.adele.icasa.orange.service.ZwaveTestResult;
import fr.liglab.adele.icasa.orange.service.ZwaveTestStrategy;
import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import org.apache.felix.ipojo.annotations.*;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Body;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.concurrent.ManagedFutureTask;
import org.wisdom.api.concurrent.ManagedScheduledExecutorService;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.websockets.Publisher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Instantiate
@Provides(specifications = org.wisdom.api.Controller.class)
@Path(value = "/orange")
public class OrangeRemoteController extends DefaultController {

    @Requires(id="zwaveDevices",specification = ZwaveDevice.class,optional = true)
    List<ZwaveDevice> zwaveDevices;

    @Requires(id="zwaveController",specification = ZwaveController.class,optional = true)
    List<ZwaveController> zwaveControllers;

    @Requires(specification = ZwaveTestStrategy.class,optional = true)
    List<ZwaveTestStrategy> testStrategies ;

    @Requires
    Publisher webSocketPublisher;

    @Requires
    Json json;

    @Requires(filter = "(name=" + ManagedScheduledExecutorService.SYSTEM + ")", proxy = false)
    ManagedScheduledExecutorService scheduler;

    private int discoveryTime = 20;

    private TimeUnit discoveryTimeUnit = TimeUnit.SECONDS;

    private final String websocketURI = "/orange/devices";

    private final Map<String,ManagedFutureTask> managedFutureTaskMap = new ConcurrentHashMap<>();

    @Bind(id="zwaveDevices")
    public void bindZwaveDevices(ZwaveDevice device){
        webSocketPublisher.publish(websocketURI,buildDiscoveryZwaveEvent(device,ZwaveEvent.DEVICE_ADDED));
    }

    @Unbind(id="zwaveDevices")
    public void unbindZwaveDevices(ZwaveDevice device){
        webSocketPublisher.publish(websocketURI,buildDiscoveryZwaveEvent(device,ZwaveEvent.DEVICE_REMOVED));
    }

    /**
     * Lifecycle
     */

    public void invalidate(){
        for (Map.Entry<String,ManagedFutureTask> entry : managedFutureTaskMap.entrySet()){
            entry.getValue().cancel(true);
        }
        managedFutureTaskMap.clear();
    }


    @Route(method = HttpMethod.GET,uri = "/zwaves")
    public Result getZwaveDevices(){

        //Maybe To Remove
        return ok();
    }

    @Route(method = HttpMethod.GET,uri = "/zwaves/{id}")
    public Result getZwaveDevice(@Parameter("id") String appId){

        //Maybe to remove
        return ok();
    }


    private class ControllerBackToNormalTask implements Runnable{

        private final int zwaveId;

        private ControllerBackToNormalTask(String zwaveId) {
            this.zwaveId = Integer.parseInt(zwaveId);
        }

        private ZwaveController getController(){
            for (ZwaveController controller:zwaveControllers){
                if (controller.getNodeId() == zwaveId){
                    return controller;
                }
            }
            return null;
        }

        @Override
        public void run() {
            ZwaveController controller = getController();
            if (controller != null){
                controller.changeMode(ZwaveController.Mode.NORMAL);
                ObjectNode node = json.newObject();
                node.put("event", ZwaveEvent.DISCOVERY_TIMEOUT.eventType);
                webSocketPublisher.publish(websocketURI,node);
            }
        }
    }
    @Route(method = HttpMethod.PUT,uri = "/zwaves/{id}")
    public Result updateZwaveDevice(@Parameter("id") String zwaveId, @Body WebcomRequestBody data){
        if (zwaveId == null){
            return notFound();
        }

        if (data == null){
            return internalServerError();
        }


        ZwaveController.Mode mode = ZwaveController.Mode.getMode(data.discoveryMode);
        if (mode != null){
            for (ZwaveController controller:zwaveControllers){
                if (controller.getNodeId() == Integer.parseInt(zwaveId)){
                    controller.changeMode(mode);
                    if (mode != ZwaveController.Mode.NORMAL ){
                        if (managedFutureTaskMap.containsKey(zwaveId)){
                            managedFutureTaskMap.remove(zwaveId).cancel(true);
                        }
                        ManagedFutureTask futurTask = scheduler.schedule(new ControllerBackToNormalTask(zwaveId),discoveryTime,discoveryTimeUnit );
                        managedFutureTaskMap.put(zwaveId,futurTask);
                    }
                }
            }
        }


        if (data.beginTest != null){

            if (data.beginTest) {
                boolean testLaunch = false;
                for (ZwaveTestStrategy testStrategy : testStrategies) {
                    if (testStrategy.getTestTargets().contains(zwaveId)) {
                        testLaunch = true;
                        try {
                            testStrategy.beginTest(zwaveId,
                                    (String zwaveID, TestReport testResult) -> {
                                        webSocketPublisher.publish(websocketURI,buildZwaveTestEvent(zwaveID,testResult.testResult,testResult.testMessage));
                                    },
                                    true
                            );
                        } catch (TestRunningException e) {
                            return unauthorized();
                        }
                    }
                }

                if (!testLaunch){
                    return notFound();
                }

            }
        }

        return ok();
    }

    private ObjectNode buildZwaveTestEvent(String zwaveId,ZwaveTestResult testResult,String testMessage){
        ObjectNode node = json.newObject();
        node.put("nodeId",zwaveId);
        node.put("event", ZwaveEvent.DEVICE_TESTED.eventType);
        node.put("test-status",testResult.status);
        node.put("test-message",testMessage);

        return node;
    }

    private ObjectNode buildDiscoveryZwaveEvent(ZwaveDevice device,ZwaveEvent event){
        ObjectNode result = json.newObject();
        result.put("nodeId", device.getNodeId());
        result.put("manufacturerId", device.getManufacturerId());
        result.put("deviceId", device.getDeviceId());
        result.put("deviceType", device.getDeviceType());
        result.put("event", event.eventType);

        return result;
    }

    private enum ZwaveEvent {
        DEVICE_ADDED("zwave-added"),
        DEVICE_REMOVED("zwave-removed"),
        DISCOVERY_TIMEOUT("zwave-discovery-timeout"),
        DEVICE_TESTED("zwave-tested");

        public final String eventType;

        ZwaveEvent(String eventType){
            this.eventType = eventType;
        }
    }

    private static class WebcomRequestBody{
        public String discoveryMode;
        public Boolean beginTest;
    }

}
