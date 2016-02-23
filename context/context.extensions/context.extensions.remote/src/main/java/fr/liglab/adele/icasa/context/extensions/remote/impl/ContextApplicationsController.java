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
package fr.liglab.adele.icasa.context.extensions.remote.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.context.extensions.remote.impl.ContextApplicationRegistry.Application;
import fr.liglab.adele.icasa.context.extensions.remote.impl.ContextApplicationRegistry.Requirement;
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

import java.util.List;


@Controller
public class ContextApplicationsController extends DefaultController {

    @Requires
    Json json;

    @Requires(optional=false, proxy=false)
    ContextApplicationRegistry applicationRegistry;

    @Route(method = HttpMethod.GET, uri = "/context/applications")
    public Result getApplicationFactories(){
        ObjectNode result = json.newObject();

        for(String appIds :applicationRegistry.getApplicationIds()){
            result.put(appIds,"true");
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/applications/{appId}")
    public Result getApplicationFactories(@Parameter(value = "appId") String appId){
        if (appId == null){
            return internalServerError(new NullPointerException("Application id is null"));
        }

        ObjectNode result = json.newObject();

        List<String> factoIds = applicationRegistry.getFactoriesByApplicationId(appId);
        if (factoIds == null){
            return notFound("Application not found "+appId);
        }
        for (String factory : factoIds) {
            result.put(factory,"true");
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/applications/{appId}/{factory}")
    public Result getApplications(@Parameter(value = "factory") String factoryId){

        if (factoryId == null){
            return internalServerError(new NullPointerException("Application factory id is null"));
        }

        List<String> instances = applicationRegistry.getInstances(factoryId);
        if (instances == null) {
            return notFound("Application factory not found "+factoryId);
        }

        ObjectNode result = json.newObject();

        for (String instance  : instances) {
            result.put(instance,"true");
        }
        return ok(result);
    }


    @Route(method = HttpMethod.GET, uri = "/context/applications/{appId}/{factory}/{instance}")
    public Result getApplication(@Parameter(value = "factory") String factoryId, @Parameter(value = "instance") String instanceId){
        if (factoryId == null){
            return internalServerError(new NullPointerException("Application factory id is null"));
        }

        if (instanceId == null){
            return internalServerError(new NullPointerException("Application instance id is null"));
        }

        Application application = applicationRegistry.getInstance(factoryId, instanceId);
        if (application == null) {
            return notFound("Application not found "+instanceId+" factory "+factoryId);
        }

        ObjectNode result = json.newObject();
        result.put("instance", instanceId)
                .put("factory", factoryId)
                .put("state", application.getState().toString());

        ArrayNode requires = result.arrayNode();
        for (Requirement requirement  : application.getRequirements()) {
            ObjectNode require = json.newObject();

            require.put("id", requirement.getId())
                    .put("service", requirement.getSpecification())
                    .put("state", requirement.getState().toString())
                    .put("optional", requirement.isOptional())
                    .put("aggregate", requirement.isAggregate());

            requires.add(require);
        }

        result.set("requires", requires);
        return ok(result);
    }


}
