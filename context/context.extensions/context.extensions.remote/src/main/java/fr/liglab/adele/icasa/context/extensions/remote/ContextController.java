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
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

import java.util.List;
import java.util.Map;

@Controller
public class ContextController extends DefaultController {

    @Requires
    Json json;

    /**
     * Injects a template named 'welcome'.
     */
    @View("ContextMainPage")
    Template welcome;

    @Requires(specification = ContextEntity.class,optional = true)
    List<ContextEntity> entities;

    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "/context")
    public Result welcome() {
        return ok(render(welcome, "welcome", "Welcome to Wisdom Framework!"));
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities")
    public Result getEntities(){
        ObjectNode result = json.newObject();

        int i = 0;
        for (ContextEntity entity : entities){
            result.put(entity.getId(),true);

        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}")
    public Result getEntity(@Parameter(value = "id") String id){
        if (id == null){
            return internalServerError(new NullPointerException(" id is null"));
        }

        ObjectNode result = json.newObject();

        for (ContextEntity entity : entities){
            if (entity.getId().equals(id)){
                for (Map.Entry<String,Object> entry : entity.dumpState(null).entrySet()){
                    result.put(entry.getKey(),entry.getValue().toString());
                }
                return ok(result);
            }
        }
        return notFound();
    }
}
