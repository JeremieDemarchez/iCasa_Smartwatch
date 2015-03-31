/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.electricity.viewer.ElectricityViewer;
import fr.liglab.adele.icasa.electricity.viewer.ElectricityViewerListener;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.joda.time.DateTime;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.*;
import org.wisdom.api.annotations.scheduler.Every;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.websockets.Publisher;
import org.wisdom.api.scheduler.Scheduled;
import org.wisdom.api.templates.Template;

/**
 * Your first Wisdom Controller.
 */
@Controller
public class ElectricityMonitorController extends DefaultController implements ElectricityViewerListener,Scheduled {

    @Requires
    ElectricityViewer viewer;

    @Requires
    Publisher publisher;

    @Requires
    Json json;

    @Requires
    Clock clock;

    @Validate
    public void start(){
        viewer.addListener(this);
    }

    @Invalidate
    public void stop(){
        viewer.removeListener(this);
    }

    @View("ElectricityMonitor")
    Template welcome;

    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "/electricity")
    public Result welcome() {
        return ok(render(welcome, "ElectricityMonitor", "Welcome to Electricity Viewer !"));
    }

    @Route(method = HttpMethod.GET, uri = "/electricity/current")
    public Result current() {
        return ok(new Sample(viewer.getTotalConsumption()).toJson());
    }

    @Opened("/electricityws")
    public void opened(@Parameter("client") String client) {
        logger().info("Client connection on web socket {}", client);
        publisher.send("/electricityws", client, (new Sample(viewer.getTotalConsumption())).toJson());
        /**ObjectNode node = json.newObject();
         for (String string : viewer.getZonesView()){
         node.put(string, viewer.getZoneConsumption(string));
         }
         publisher.publish("/electricity", node);
         **/
    }

    @Override
    public void deviceConsumptionModified(GenericDevice device, double newConsumption, double oldConsumption) {

    }

    @Override
    public void zoneConsumptionModified(String zoneId, double newConsumption, double oldConsumption) {
        logger().info("publish " + viewer.getTotalConsumption());
        publisher.publish("/electricityws",(new Sample(viewer.getTotalConsumption())).toJson());
    }

    @Every("5s")
    public void update() {
        logger().info("publish " + viewer.getTotalConsumption());
        publisher.publish("/electricityws",(new Sample(viewer.getTotalConsumption())).toJson());
    }

    private class Sample{

        private final String date;

        private final String value;

        private Sample(double value){
            this.value = String.valueOf(value);
            DateTime dateTime = new DateTime(clock.currentTimeMillis());
            date = new String(dateTime.getHourOfDay()+":"+dateTime.getMinuteOfHour()+":"+dateTime.getSecondOfMinute());
        }

        private JsonNode toJson(){
            ObjectNode result = json.newObject();
            result.put("date", date);
            result.put("value", value);
            return result;
        }
    }
}
