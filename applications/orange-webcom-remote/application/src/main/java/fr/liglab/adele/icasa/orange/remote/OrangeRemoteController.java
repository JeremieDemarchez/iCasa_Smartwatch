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
import fr.liglab.adele.icasa.orange.service.TestRunningException;
import fr.liglab.adele.icasa.orange.service.ZwaveTestResult;
import fr.liglab.adele.icasa.orange.service.ZwaveTestStrategy;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Body;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.websockets.Publisher;

import java.util.List;

@org.wisdom.api.annotations.Controller
@Path(value = "/orange")
public class OrangeRemoteController extends DefaultController {

    @Requires(id="zwaveDevices",specification = ZwaveDevice.class,optional = true)
    List<ZwaveDevice> zwaveDevices;



    @Requires(specification = ZwaveTestStrategy.class,optional = true)
    List<ZwaveTestStrategy> testStrategies ;

    @Requires
    Publisher webSocketPublisher;

    @Requires
    Json json;

    private final String websocketURI = "/orange/devices";

    @Bind(id="zwaveDevices")
    public void bindZwaveDevices(ZwaveDevice device){
        webSocketPublisher.publish(websocketURI,buildDiscoveryZwaveEvent(device,ZwaveEvent.DEVICE_ADDED));
    }

    @Unbind(id="zwaveDevices")
    public void unbindZwaveDevices(ZwaveDevice device){
        webSocketPublisher.publish(websocketURI,buildDiscoveryZwaveEvent(device,ZwaveEvent.DEVICE_REMOVED));
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


    @Route(method = HttpMethod.PUT,uri = "/zwaves/{id}")
    public Result updateZwaveDevice(@Parameter("id") String zwaveId, @Body WebcomRequestBody data){
        if (zwaveId == null){
            return notFound();
        }

        if (data == null){
            return internalServerError();
        }


        DiscoveryMode mode = DiscoveryMode.getMode(data.discoveryMode);
        if (mode != null){
            // Check if the device id correspond to a controller, and change the mode
        }


        if (data.beginTest != null){

            if (data.beginTest) {
                boolean testLaunch = false;
                for (ZwaveTestStrategy testStrategy : testStrategies) {
                    if (testStrategy.getTestTargets().contains(zwaveId)) {
                        testLaunch = true;
                        try {
                            testStrategy.beginTest(zwaveId,
                                    (String zwaveID, ZwaveTestResult testResult) -> {
                                        webSocketPublisher.publish(websocketURI,buildZwaveTestEvent(zwaveID,testResult,""));
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
        result.put("manufacturerId", 1);
        result.put("deviceId", 1);
        result.put("deviceType", 1);
        result.put("event", event.eventType);

        return result;
    }

    private enum DiscoveryMode{
        DISCOVERY("discovery"),
        NORMAL("normal"),
        UNDISCOVERY("undiscovery");

        public final String mode;

        DiscoveryMode(String mode){
            this.mode = mode;
        }

        public static DiscoveryMode getMode(String mode){
            if (DISCOVERY.mode.equalsIgnoreCase(mode)){
                return DISCOVERY;
            }
            else if (NORMAL.mode.equalsIgnoreCase(mode)){
                return NORMAL;
            }
            else if (UNDISCOVERY.mode.equalsIgnoreCase(mode)) {
                return UNDISCOVERY;
            }
            return null;
        }
    }

    private enum ZwaveEvent {
        DEVICE_ADDED("zwave-added"),
        DEVICE_REMOVED("zwave-removed"),
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
