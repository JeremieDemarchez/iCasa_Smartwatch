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

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.philips.device.util.PhilipsHueLightDeclarationWrapper;
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
public class PhilipsHueImporter extends AbstractImporterComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PhilipsHueImporter.class);

    private final BundleContext context;

    private Map<String, ServiceRegistration> lamps = new HashMap<String, ServiceRegistration>();


    @Requires(optional = false)
    Factory philipsHUELightFactory ;

    @ServiceProperty(name = "target", value = "(&(discovery.philips.device.name=*)(scope=generic))")
    private String filter;

    @ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
    private String name;

    public PhilipsHueImporter(BundleContext context) {
        this.context = context;
    }

    @PostRegistration
    public void registration(ServiceReference serviceReference) {
        setServiceReference(serviceReference);
    }

    @Validate
    public void validate() {
        LOG.info("Philips hue Importer is up and running");
    }

    @Invalidate
    public void invalidate() {

        LOG.info("Cleaning up instances into Philips hue Importer");

        cleanup();

    }

    private void cleanup() {

        for (Map.Entry<String, ServiceRegistration> lampEntry : lamps.entrySet()) {
            lamps.remove(lampEntry.getKey()).unregister();
        }
    }

    @Override
    protected void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
        ComponentInstance instance;
        LOG.info("philips hue importer triggered");

        PhilipsHueLightDeclarationWrapper pojo= PhilipsHueLightDeclarationWrapper.create(importDeclaration);


        Hashtable properties = new Hashtable();


        try {
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER,pojo.getId());
            properties.put("philips.device.light",pojo.getLight());
            properties.put("philips.device.bridge",pojo.getBridge());
            instance = philipsHUELightFactory.createComponentInstance(properties);
            if (instance != null) {
                ServiceRegistration sr = new IpojoServiceRegistration(
                        instance);
                lamps.put(pojo.getId(),sr);
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
    protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

        PhilipsHueLightDeclarationWrapper pojo= PhilipsHueLightDeclarationWrapper.create(importDeclaration);

        try {
            lamps.remove(pojo.getId()).unregister();
        } catch (IllegalStateException e) {
            LOG.error("failed unregistering lamp", e);
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

