package fr.liglab.adele.icasa.electricity.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.electricity.manager.sample.ConsumptionSample;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.joda.time.DateTime;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.*;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.model.EntityFilter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Component
@Instantiate
@Provides
public class ElectricityController extends DefaultController {

    public class Person {

        public String id;

        public String name;

        public Person() {
        }

        public void setId(@Parameter("id")String id) {
            this.id = id;
        }

        public void setName(@Parameter("name")String name) {
            this.name = name;
        }



        public String toString() {
            return "Data: " + id + " - " + name;
        }


    }

    @Requires
    Json json;

    @Requires
    private ElectricityManager electricityManager;

    @Route(method= HttpMethod.GET, uri="/electricity")
    public Result all() {
        System.out.println(" Filter " + electricityManager.filterSample());
        return ok(" FIlter " + electricityManager + " ! ");
    }

    private class ResourceFilter implements EntityFilter<ConsumptionSample>{

        DateTime dateToCompare;

        public ResourceFilter(long Time){
            dateToCompare = new DateTime(Time);
        }
        @Override
        public boolean accept(ConsumptionSample consumptionSample) {
            return consumptionSample.getDate().isAfter(dateToCompare);
        }
    }


    @Route(method= HttpMethod.GET, uri="/electricity/json")
    public Result allJson() {
        ObjectNode result = json.newObject();
        ObjectNode coco = json.newObject();// 2
        result.put("name", "colin");
        coco.put("11:01:1",33);
        result.put("message", coco);
        return ok(result);
    }

    @Route(method = HttpMethod.POST, uri = "/json/hello")
    public Result hello() {
        JsonNode json = context().body(JsonNode.class);
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String name = json.findPath("name").textValue();
            if (name == null) {
                return badRequest("Missing parameter [name]");
            } else {
                return ok("Hello " + name);
            }
        }
    }

}
