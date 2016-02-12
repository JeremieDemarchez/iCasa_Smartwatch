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
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class ContextModelController extends DefaultController {

    @Requires
    Json json;

    /**
     * Injects a template named 'welcome'.
     */
    @View("ContextMainPage")
    Template welcome;

    @Requires(specification = ContextEntity.class, optional = true)
    List<ContextEntity> entities;

    @Requires(specification = Relation.class, optional = true)
    List<Relation> relations;

    Map<ContextEntity, Object> entities_factories = new HashMap<ContextEntity, Object>();

    @Bind(specification = ContextEntity.class, aggregate = true, optional = true)
    void bindContextEntity(ContextEntity contextEntity, Map<String, Object> properties){
        entities_factories.put(contextEntity, properties.get("factory.name"));

    }

    @Unbind(specification = ContextEntity.class)
    void unbindContextEntity(ContextEntity contextEntity){
        entities_factories.remove(contextEntity);
    }

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
            if (id.equals(entity.getId())){
                for (Map.Entry<String,Object> entry : entity.dumpState().entrySet()){
                    result.put(entry.getKey(),entry.getValue().toString());
                }
                return ok(result);
            }
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}/services")
    public Result getEntityServices(@Parameter(value = "id") String id){
        if (id == null){
            return internalServerError(new NullPointerException(" id is null"));
        }

        ObjectNode result = json.newObject();

        for (ContextEntity entity : entities){
            if (id.equals(entity.getId())){
                Set<String> services = entity.getServices();
                for(String service : services){
                    result.put(service, true);
                }
                return ok(result);
            }
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/relations")
    public Result getRelations(){

        ObjectNode result = json.newObject();

        result.put("size", relations.size());
        int i = 0;
        for (Relation relation : relations){
            result.put("relation"+i+"name",relation.getName());
            result.put("relation"+i+"source",relation.getSource());
            result.put("relation"+i+"target",relation.getTarget());
            i++;
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}/relations")
    public Result getEntityRelations(@Parameter(value = "id") String id){
        if (id == null){
            return internalServerError(new NullPointerException(" id is null"));
        }

        ObjectNode result = json.newObject();

        for (Relation relation : relations){
            String source = relation.getSource();
            if (id.equals(source)){
                result.put(relation.getName(),relation.getTarget());

            }

        }
        return ok(result);
    }

}
