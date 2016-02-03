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
package fr.liglab.adele.philips.device;

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

import fr.liglab.adele.philips.device.util.PhilipsHueBridgeDeclarationWrapper;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.component.ImporterIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Component
@Provides(specifications = {ImporterService.class,ImporterIntrospection.class})
public class PhilipsHueBridgeImporter extends AbstractImporterComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PhilipsHueBridgeImporter.class);

    private final BundleContext context;

    @Requires(filter = "((factory.name=philipsHueBridge))")
    Factory philipsBridgeFactory;

    private Map<String, ServiceRegistration> bridges = new HashMap<String, ServiceRegistration>();

    @ServiceProperty(name = "target", value = "(&(discovery.philips.bridge.type=*)(scope=generic))")
    private String filter;

    @ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
    private String name;

    public PhilipsHueBridgeImporter(BundleContext context) {
        this.context = context;
    }

    @PostRegistration
    public void registration(ServiceReference serviceReference) {
        super.setServiceReference(serviceReference);
    }

    @Validate
    public void validate() {
        super.start();
        LOG.info("Philips hue Importer is up and running");
    }

    @Invalidate
    public void invalidate() {
        super.stop();
        LOG.info("Cleaning up instances into Philips hue Importer");

        cleanup();

    }

    private void cleanup() {

        for(Map.Entry<String,ServiceRegistration> bridgeEntry:bridges.entrySet()){
            bridges.remove(bridgeEntry.getKey()).unregister();
        }

    }

    @Override
    protected void useImportDeclaration(final ImportDeclaration importDeclaration) throws BinderException {

        LOG.info("philips hue bridge importer triggered");

        PhilipsHueBridgeDeclarationWrapper pojo= PhilipsHueBridgeDeclarationWrapper.create(importDeclaration);

        ComponentInstance instance;

        Hashtable properties = new Hashtable();

        try {
            properties.put("philips.bridge",pojo.getBridgeObject());
            properties.put("bridge.id",pojo.getId());
            instance = philipsBridgeFactory.createComponentInstance(properties);
            if (instance != null) {
                ServiceRegistration sr = new IpojoServiceRegistration(
                        instance);
                bridges.put(pojo.getId(),sr);
                super.handleImportDeclaration(importDeclaration);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("failed registering lamp", unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("failed registering lamp", e);
        } catch (ConfigurationException e) {
            LOG.error("failed registering lamp", e);
        }


    }

    @Override
    protected void denyImportDeclaration(final ImportDeclaration importDeclaration) throws BinderException {

        LOG.info("philips hue bridge importer removal triggered");

        PhilipsHueBridgeDeclarationWrapper pojo= PhilipsHueBridgeDeclarationWrapper.create(importDeclaration);

        try {
            ServiceRegistration sr = bridges.remove(pojo.getId());
            if (sr != null) {
                sr.unregister();
            }
        } catch (IllegalStateException e) {
            LOG.error("failed unregistering bridge", e);
        }

        unhandleImportDeclaration(importDeclaration);
    }


    public String getName() {
        return name;
    }

    /**
     * A wrapper for ipojo Component instances
     *
     *
     */
    class IpojoServiceRegistration implements ServiceRegistration {

        ComponentInstance instance;

        public IpojoServiceRegistration(ComponentInstance instance) {
            super();
            this.instance = instance;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.osgi.framework.ServiceRegistration#getReference()
         */
        public ServiceReference getReference() {
            try {
                ServiceReference[] references = instance.getContext()
                        .getServiceReferences(
                                instance.getClass().getCanonicalName(),
                                "(instance.name=" + instance.getInstanceName()
                                        + ")");
                if (references.length > 0)
                    return references[0];
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.osgi.framework.ServiceRegistration#setProperties(java.util.Dictionary
         * )
         */
        public void setProperties(Dictionary properties) {
            instance.reconfigure(properties);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.osgi.framework.ServiceRegistration#unregister()
         */
        public void unregister() {
            instance.dispose();
        }

    }
}

