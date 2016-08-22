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
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
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

    @Requires(specification = ZwaveTestStrategy.class,optional = true)
    List<ZwaveTestStrategy> testStrategies ;

    @Requires
    Publisher webSocketPublisher;

    @Requires
    Json json;

    private final String websocketURI = "/orange/websocket";

    @Route(method = HttpMethod.GET,uri = "/zwaves")
    public Result getZwaveDevices(){
        return ok();
    }

    @Route(method = HttpMethod.GET,uri = "/zwaves/{id}")
    public Result getZwaveDevice(@Parameter("id") String appId){
        return ok();
    }


    @Route(method = HttpMethod.PUT,uri = "/zwaves/{id}")
    public Result updateZwaveDevice(@Parameter("id") String zwaveId,@Parameter("discoveryMode") String discoveryMode,@Parameter("beginTest") Boolean test){
        if (zwaveId == null){
            return notFound();
        }

        DiscoveryMode mode = DiscoveryMode.getMode(discoveryMode);
        if (mode != null){
            // Check if the device id correspond to a controller, and change the mode
        }


        if (test != null){

            if (test) {
                boolean testLaunch = false;
                for (ZwaveTestStrategy testStrategy : testStrategies) {
                    if (testStrategy.getTestTargets().contains(zwaveId)) {
                        testLaunch = true;
                        try {
                            testStrategy.beginTest(zwaveId,
                                    (String zwaveID, ZwaveTestResult testResult) -> {
                                        ObjectNode result = json.newObject();
                                        result.put("nodeId", zwaveID);
                                        result.put("result", testResult.toString());
                                        webSocketPublisher.publish(websocketURI,result);
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


    private enum DiscoveryMode{
        DISCOVERY("discovery"),
        NORMAL("normal"),
        UNDISCOVERY("undiscovery");

        private final String mode;


        DiscoveryMode(String mode){
            this.mode = mode;
        }

        public static DiscoveryMode getMode(String mode){
            if ("discovery".equalsIgnoreCase(mode)){
                return DISCOVERY;
            }
            else if ("normal".equalsIgnoreCase(mode)){
                return NORMAL;
            }
            else if ("undiscovery".equalsIgnoreCase(mode)) {
                return UNDISCOVERY;
            }
            return null;
        }
    }
}
