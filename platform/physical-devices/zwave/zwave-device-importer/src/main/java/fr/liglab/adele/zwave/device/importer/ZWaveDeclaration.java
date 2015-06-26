/*
 * #%L
 * OW2 Chameleon - Fuchsia Framework
 * %%
 * Copyright (C) 2009 - 2015 OW2 Chameleon
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
package fr.liglab.adele.zwave.device.importer;

import org.osgi.framework.Filter;

import org.ow2.chameleon.fuchsia.core.FuchsiaUtils;

import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.ow2.chameleon.fuchsia.core.exceptions.InvalidFilterException;

import java.util.Map;

public class ZWaveDeclaration {

    private final static Filter MATCHING_DECLARATION_FILTER = buildMatchFilter();

    private String id;
    private String port;


    private static Filter buildMatchFilter() {
    	
        try {
        	return FuchsiaUtils.getFilter("(&(scope=generic)(protocol=zwave)(id=*)(port=*))");
        } catch (InvalidFilterException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ZWaveDeclaration from(ImportDeclaration importDeclaration) throws BinderException {
        
    	Map<String, Object> metadata = importDeclaration.getMetadata();

        if (!MATCHING_DECLARATION_FILTER.matches(metadata)) {
            throw new BinderException("Not enough information in the metadata to be used by the zwave importer");
        }
        
        ZWaveDeclaration declaration = new ZWaveDeclaration();

        declaration.id		= (String) metadata.get("id");
        declaration.port	= (String) metadata.get("port");
        
        return declaration;
    }


    private ZWaveDeclaration() {
    }
    
    public String getId() {
        return id;
    }
    
    public String getPort() {
        return port;
    }
    
}
