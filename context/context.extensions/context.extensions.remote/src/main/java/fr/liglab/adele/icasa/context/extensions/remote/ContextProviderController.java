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
package fr.liglab.adele.icasa.context.extensions.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.context.model.introspection.EntityCreatorHandlerIntrospection;
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ContextProviderController extends DefaultController {

    @Requires
    Json json;

//    @Requires(specification = EntityCreatorHandlerIntrospection.class,optional = true)
//    List<EntityCreatorHandlerIntrospection> creatorHandlers;

    @Route(method = HttpMethod.GET, uri = "/context/providers")
    public Result getContextProviders(){
        ObjectNode result = json.newObject();


        for (EntityCreatorHandlerIntrospection creatorHandler : fake){
            result.put(creatorHandler.getAttachedComponentInstanceName(),true);
        }

        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/providers/{id}")
    public Result getContextProvider(@Parameter(value = "id") String id){
        if (id == null){
            return internalServerError(new NullPointerException("Provider id is null"));
        }

        ObjectNode result = json.newObject();

        for (EntityCreatorHandlerIntrospection creatorHandler : fake){
            if (creatorHandler.getAttachedComponentInstanceName().equals(id)){
                for (String implemSpecification : creatorHandler.getImplementations()){
                    result.put(implemSpecification,creatorHandler.getImplentationState(implemSpecification));
                }
                return ok(result);
            }
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/providers/{id}/{implem}")
    public Result getContextProviderImpl(@Parameter(value = "id") String id, @Parameter(value = "implem") String implem){
        if (id == null){
            return internalServerError(new NullPointerException("Provider id is null"));
        }

        ObjectNode result = json.newObject();

        for (EntityCreatorHandlerIntrospection creatorHandler : fake) {
            if (creatorHandler.getAttachedComponentInstanceName().equals(id)) {
                for (String implemSpecification : creatorHandler.getImplementations()) {
                    if (implemSpecification.equals(implem)) {
                        result.put(implemSpecification, creatorHandler.getImplentationState(implemSpecification));
                    }
                }
                return ok(result);
            }
        }

        return notFound();
    }

    List<EntityCreatorHandlerIntrospection> fake = new ArrayList<EntityCreatorHandlerIntrospection>();

    ContextProviderController(){
        fake.add(0,new FakeCreator("Creator1","fr.entity.1"));
        fake.add(1,new FakeCreator("Creator2","fr.entity.2"));
        fake.add(1,new FakeCreator("German","fr.entity.sd2"));
    }

    @Route(method = HttpMethod.POST, uri = "/context/providers/{id}/{implem}/{state}")
    public Result switchContextProviderState(@Parameter(value = "id") String id, @Parameter(value = "implem") String implem, @Parameter(value = "state") boolean state){
        if (id == null){
            return internalServerError(new NullPointerException("Provider id is null"));
        }

        ObjectNode result = json.newObject();


        for (EntityCreatorHandlerIntrospection creatorHandler : fake) {
            if (creatorHandler.getAttachedComponentInstanceName().equals(id)) {
                for (String implemSpecification : creatorHandler.getImplementations()) {
                    if (implemSpecification.equals(implem)) {
                        result.put(implemSpecification, creatorHandler.switchCreation(implem, state));
                    }
                }
                return ok(result);
            }
        }

        return notFound();
    }
}
