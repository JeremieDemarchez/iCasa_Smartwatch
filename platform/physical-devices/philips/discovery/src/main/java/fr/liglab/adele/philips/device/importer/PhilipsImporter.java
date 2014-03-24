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
package fr.liglab.adele.philips.device.importer;

import fr.liglab.adele.icasa.device.GenericDevice;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.log.LogService;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.rose.AbstractImporterComponent;
import org.ow2.chameleon.rose.ImporterService;
import org.ow2.chameleon.rose.RoseMachine;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(name = "philips.device.importer")
@Provides(specifications = { ImporterService.class }, properties = { @StaticServiceProperty(type = "java.lang.String", name = "rose.protos.configs", value = "philips") })
public class PhilipsImporter extends AbstractImporterComponent  {

    @Requires(id = "rose.machine")
    private RoseMachine roseMachine;

    @Requires(filter = "(factory.name=philipsHueLight)")
    private Factory philipsHueLightFactory;

    private static final Logger LOG = LoggerFactory.getLogger(PhilipsImporter.class);

    @Override
    protected ServiceRegistration createProxy(EndpointDescription endpointDescription, Map<String, Object> stringObjectMap) {

        ComponentInstance instance;

        if (endpointDescription != null) {

            String endpointId=(String)endpointDescription.getProperties().get(RemoteConstants.ENDPOINT_ID);

            LOG.debug("Creating proxy for the endpoint {}",endpointId);

            Hashtable properties = new Hashtable();
            //properties.putAll(endpointDescription.getProperties());
            properties.put("philips.device.light", endpointDescription.getProperties().get("philips.device.light"));
            properties.put("philips.device.bridge",endpointDescription.getProperties().get("philips.device.bridge"));
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER,endpointId);

            try {
                instance = philipsHueLightFactory.createComponentInstance(properties);
                ServiceRegistration sr = new IpojoServiceRegistration(
                        instance);
                return sr;
            } catch (UnacceptableConfiguration unacceptableConfiguration) {
                LOG.error("Proxy instantiation failed",unacceptableConfiguration);
            } catch (MissingHandlerException e) {
                LOG.error("Proxy instantiation failed",e);
            } catch (ConfigurationException e) {
                LOG.error("Proxy instantiation failed",e);
            }

        }else {
            System.out.println("Endpoint description is null, impossible to import");
        }

        return null;
    }

    @Validate
    private void init(){
        super.start();
        LOG.debug("Philips importer initialized");
    }

    @Override
    protected void destroyProxy(EndpointDescription endpointDescription, ServiceRegistration serviceRegistration) {

    }

    @Override
    protected LogService getLogService() {
        return null;
    }

    @Override
    public List<String> getConfigPrefix() {
        List<String> list = new ArrayList<String>();
        list.add("philips");
        return list;
    }

    @Override
    public RoseMachine getRoseMachine() {
        return roseMachine;
    }

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
