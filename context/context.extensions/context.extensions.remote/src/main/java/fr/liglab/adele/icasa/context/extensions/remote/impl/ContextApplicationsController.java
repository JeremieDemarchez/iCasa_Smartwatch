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

import java.util.List;

import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.liglab.adele.icasa.context.extensions.remote.impl.ContextApplicationRegistry.Application;
import fr.liglab.adele.icasa.context.extensions.remote.impl.ContextApplicationRegistry.Requirement;


@Controller
public class ContextApplicationsController extends DefaultController {

    @Requires
    Json json;

    @Requires(optional=false, proxy=false)
    ContextApplicationRegistry applicationRegistry;

    @Route(method = HttpMethod.GET, uri = "/context/applications")
    public Result getApplicationFactories(){
        ObjectNode result = json.newObject();
        
        ArrayNode factoryIds = result.arrayNode();
        for (String factory : applicationRegistry.getFactories()) {
        	factoryIds.add(factory);
		}

        result.set("ids", factoryIds);
        return ok(result);
    }
    
    @Route(method = HttpMethod.GET, uri = "/context/applications/{factory}")
    public Result getApplications(@Parameter(value = "factory") String factoryId){
        
    	if (factoryId == null){
            return internalServerError(new NullPointerException("Application factory id is null"));
        }

    	List<String> instances = applicationRegistry.getInstances(factoryId);
        if (instances == null) {
            return notFound("Application factory not found "+factoryId);
        }
    	
        ObjectNode result = json.newObject();
        result.put("factory",factoryId);
        
        ArrayNode instanceIds = result.arrayNode();
        for (String instance  : instances) {
        	instanceIds.add(instance);
		}

        result.set("instances", instanceIds);
        return ok(result);
    }
    
    
    @Route(method = HttpMethod.GET, uri = "/context/applications/{factory}/{instance}")
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
