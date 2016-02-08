/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.philips.device.util;

/*
 * #%L
 * OW2 Chameleon - Fuchsia Importer Philips Hue
 * %%
 * Copyright (C) 2009 - 2014 OW2 Chameleon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgi.framework.Filter;
import org.ow2.chameleon.fuchsia.core.FuchsiaUtils;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.ow2.chameleon.fuchsia.core.exceptions.InvalidFilterException;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.Map;

import static org.ow2.chameleon.fuchsia.core.declaration.Constants.ID;
import static fr.liglab.adele.philips.device.util.Constants.DISCOVERY_PHILIPS_DEVICE_OBJECT;
import static fr.liglab.adele.philips.device.util.Constants.DISCOVERY_PHILIPS_DEVICE_NAME;
import static fr.liglab.adele.philips.device.util.Constants.DISCOVERY_PHILIPS_DEVICE_TYPE;
import static fr.liglab.adele.philips.device.util.Constants.DISCOVERY_PHILIPS_DEVICE_BRIDGE;

public class PhilipsHueLightDeclarationWrapper {

    private static Filter declarationFilter = buildFilter();

    private String id;
    private String name;
    private String type;
    private PHLight light;
    private PHBridge bridge;

    private PhilipsHueLightDeclarationWrapper() {

    }

    private static Filter buildFilter() {
        Filter filter;
        String stringFilter = String.format("(&(%s=*)(%s=*)(%s=*)(%s=*))",
                ID,
                DISCOVERY_PHILIPS_DEVICE_NAME, DISCOVERY_PHILIPS_DEVICE_TYPE, DISCOVERY_PHILIPS_DEVICE_OBJECT);
        try {
            filter = FuchsiaUtils.getFilter(stringFilter);
        } catch (InvalidFilterException e) {
            throw new IllegalStateException(e);
        }
        return filter;
    }

    public static PhilipsHueLightDeclarationWrapper create(ImportDeclaration importDeclaration) throws BinderException {
        Map<String, Object> metadata = importDeclaration.getMetadata();

        if (!declarationFilter.matches(metadata)) {
            throw new BinderException("Not enough information in the metadata to be used by the phillips hue importer");
        }
        PhilipsHueLightDeclarationWrapper wrapper = new PhilipsHueLightDeclarationWrapper();

        wrapper.id = (String) metadata.get(ID);
        wrapper.name = (String) metadata.get(DISCOVERY_PHILIPS_DEVICE_NAME);
        wrapper.type = (String) metadata.get(DISCOVERY_PHILIPS_DEVICE_TYPE);
        wrapper.light = (PHLight) metadata.get(DISCOVERY_PHILIPS_DEVICE_OBJECT);
        wrapper.bridge = (PHBridge) metadata.get(DISCOVERY_PHILIPS_DEVICE_BRIDGE);

        return wrapper;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public PHLight getLight() {
        return light;
    }

    public PHBridge getBridge() {
        return bridge;
    }
}
