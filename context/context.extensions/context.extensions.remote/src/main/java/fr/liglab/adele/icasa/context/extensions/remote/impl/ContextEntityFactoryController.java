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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

import java.util.Set;

@Controller
public class ContextEntityFactoryController extends DefaultController {

    @Requires
    Json json;

    @Requires(optional = false)
    ContextFactoryManager myFactoryManger;

    @Route(method = HttpMethod.GET, uri = "/context/factories")
    public Result getContextEntityTypes(){
        final ObjectNode result = json.newObject();

        Set<String> factoriesIds =  myFactoryManger.getContextFactoriesIds();
        //  factoriesIds.stream().forEach((String id)->result.put(id,"true"));
        for(String ids : factoriesIds){
            result.put(ids,"true");
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/factories/{id}")
    public Result getContextEntityType(@Parameter(value = "id") String id){
        if (id == null){
            return internalServerError(new NullPointerException("Entity factories id is null"));
        }

        ObjectNode result = json.newObject();
        Set<String> factorySpecifications ;
        try {
            factorySpecifications =  myFactoryManger.getSetOfContextServices(id);
        }catch (NullPointerException e ){
            return notFound();
        }
        /**factorySpecifications.stream().forEach((String specification)->result.put(specification,id));


         return ok(result);
         **/
        for(String specification : factorySpecifications){
            result.put(specification,"true");
        }

        return ok(result);
    }

}
