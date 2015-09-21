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
package fr.liglab.adele.cream.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
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

@Controller
public class ContextController extends DefaultController {

    @Requires
    Json json;

    /**
     * Injects a template named 'welcome'.
     */
    @View("welcome")
    Template welcome;

    @Requires(specification = Relation.class,optional = true)
    List<Relation> relations;

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
        result.put("size", entities.size());
        int i = 0;
        for (ContextEntity entity : entities){
            result.put("entity"+i,entity.getId());
            i++;
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}")
    public Result getEntityStateExtended(@Parameter("id") String id){
        System.out.println(" GET " + id);
        ObjectNode result = json.newObject();
        for (ContextEntity entity : entities){
            String entityId = entity.getId();
            if (entityId.equals(id)){
                for (String key : entity.getStateAsMap().keySet()){
                    result.put(key,entity.getStateAsMap().get(key).toString());
                }
                for (String key : entity.getStateExtensionAsMap().keySet()){
                    result.put(key,entity.getStateExtensionAsMap().get(key).toString());
                }
                return ok(result);
            }
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}/extensions")
    public Result getEntityState(@Parameter("id") String id){
        System.out.println(" GET " + id);
        ObjectNode result = json.newObject();
        for (ContextEntity entity : entities){
            String entityId = entity.getId();
            if (entityId.equals(id)){
                for (String key : entity.getStateExtensionAsMap().keySet()){
                    result.put(key,entity.getStateExtensionAsMap().get(key).toString());
                }
                return ok(result);
            }
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/entities/{id}/state")
    public Result getEntityStateExtension(@Parameter("id") String id){
        System.out.println(" GET " + id);
        ObjectNode result = json.newObject();
        for (ContextEntity entity : entities){
            String entityId = entity.getId();
            if (entityId.equals(id)){
                for (String key : entity.getStateAsMap().keySet()){
                    result.put(key,entity.getStateAsMap().get(key).toString());
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
            result.put("relation"+i+"end",relation.getEnd());
            i++;
        }
        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/relations/{id}")
    public Result getRelation(@Parameter("id") String id){
        System.out.println(" GET " + id);
        ObjectNode result = json.newObject();
        for (Relation relation : relations){
            String relationId = relation.getId();
            if (relationId.equals(id)){
                result.put("relation.name",relation.getName());
                result.put("relation.source",relation.getSource());
                result.put("relation.end",relation.getEnd());
                result.put("relation.state.extension.name",relation.getExtendedState().getName());
                result.put("relation.state.extension.value",relation.getExtendedState().getValue().toString());
                result.put("relation.state.extension.isAggregate",relation.getExtendedState().isAggregate());
                return ok(result);
            }
        }
        return notFound();
    }

}
