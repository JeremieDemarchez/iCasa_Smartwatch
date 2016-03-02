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
import fr.liglab.adele.icasa.context.model.Relation;
import fr.liglab.adele.icasa.context.model.introspection.EntityProvider;
import fr.liglab.adele.icasa.context.model.introspection.RelationProvider;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ContextProviderController extends DefaultController {

    @Requires
    Json json;

    @Requires(specification = EntityProvider.class, optional = true)
    List<EntityProvider> entityProviders;

    @Requires(id= "relation.providers", specification = RelationProvider.class, optional = true)
    List<RelationProvider> relationProviders;

    Map<String, RelationProvider> relationProviderMap = new HashMap<String, RelationProvider>();

    @Bind(id= "relation.providers", optional = true, aggregate = true)
    public void bindRelationProvider(RelationProvider relationProvider){
        relationProviderMap.put(relationProvider.getName(), relationProvider);
    }

    @Unbind(id= "relation.providers")
    public void unbindRelationProvider(RelationProvider relationProvider){
        relationProviderMap.remove(relationProvider.getName());
    }

    @Route(method = HttpMethod.GET, uri = "/context/providers")
    public Result getEntityProviders() {
        ObjectNode result = json.newObject();


        for (EntityProvider provider : entityProviders) {
            result.put(provider.getName(), true);
        }

        for (RelationProvider provider : relationProviders) {
            result.put(provider.getName(), true);
        }

        return ok(result);
    }

    @Route(method = HttpMethod.GET, uri = "/context/providers/{id}")
    public Result getEntityProvider(@Parameter(value = "id") String id) {
        if (id == null) {
            return internalServerError(new NullPointerException("Provider id is null"));
        }

        ObjectNode result = json.newObject();

        for (EntityProvider provider : entityProviders) {
            if (! provider.getName().equals(id)) {
                continue;
            }
            for (String implemSpecification : provider.getProvidedEntities()) {
                result.put(implemSpecification, provider.isEnabled(implemSpecification));
            }
            return ok(result);
        }
        return notFound();
    }

    @Route(method = HttpMethod.GET, uri = "/context/providers/relations/{id}")
    public Result getRelationProvider(@Parameter(value = "id") String id) {
        if (id == null) {
            return internalServerError(new NullPointerException("Provider id is null"));
        }

        ObjectNode result = json.newObject();

        for (RelationProvider provider : relationProviders) {
            if (! provider.getName().equals(id)) {
                continue;
            }
            for (String relation : provider.getProvidedRelations()) {
                result.put(relation, provider.isEnabled(relation));
            }

            return ok(result);
        }
        return notFound();
    }

    @Route(method = HttpMethod.POST, uri = "/context/providers/{id}/{implem}/{state}")
    public Result switchEntityProviderState(@Parameter(value = "id") String id, @Parameter(value = "implem") String implem, @Parameter(value = "state") boolean state) {
        if ((id == null)||(implem == null)) {
            return internalServerError(new NullPointerException("Provider param is null"));
        }

        ObjectNode result = json.newObject();


        for (EntityProvider provider : entityProviders) {
            if (!provider.getName().equals(id)) {
                continue;
            }
            for (String implemSpecification : provider.getProvidedEntities()) {
                if (implemSpecification.equals(implem)) {
                    if (state){
                        provider.enable(implem);
                    }
                    else {
                        provider.disable(implem);
                    }
                    result.put(implemSpecification, provider.isEnabled(implemSpecification));
                    return ok(result);
                }
            }

            RelationProvider relationProvider = relationProviderMap.get(provider.getName());

            if (!relationProvider.getName().equals(id)) {
                continue;
            }
            for (String relation : relationProvider.getProvidedRelations()) {
                if (relation.equals(implem)) {
                    if (state){
                        relationProvider.enable(implem);
                    }
                    else {
                        relationProvider.disable(implem);
                    }
                    result.put(relation, provider.isEnabled(relation));
                    return ok(result);
                }
            }
        }
        return notFound();
    }
}