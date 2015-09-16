package fr.liglab.adele.icasa.context.model.example;


import fr.liglab.adele.icasa.device.GenericDevice;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Component
@Instantiate
public class DeviceDiscovery {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscovery.class);

    private final Map<String,ServiceRegistration> deviceEntities = new HashMap<>();

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.DeviceContextEntity)")
    Factory deviceEntityFactory;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }


    @Bind(id = "devices",optional = true,aggregate = true)
    public synchronized void bindDevices(GenericDevice device){
        ComponentInstance instance;



        Hashtable properties = new Hashtable();
        properties.put("context.entity.id", device.getSerialNumber());

        Hashtable<String,String> state = new Hashtable<String, String>();
        for (String property : device.getProperties()){
            /**
             * TODO : Maybe the second parameter type has to be changed in generic object
             */
            state.put(property, device.getPropertyValue(property).toString());
        }
        properties.put("context.entity.state", state);

        Hashtable filters = new Hashtable();
        filters.put("context.entity.relation", "(relation.source.id ="+device.getSerialNumber()+")");
        properties.put("requires.filters", filters);

        try {
            instance = deviceEntityFactory.createComponentInstance(properties);
            ServiceRegistration sr = new IpojoServiceRegistration(
                    instance,
                    state);

            deviceEntities.put(device.getSerialNumber(),sr);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Relation instantiation failed",unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Relation instantiation failed",e);
        } catch (ConfigurationException e) {
            LOG.error("Relation instantiation failed",e);
        }

    }

    @Unbind(id = "devices",optional = true,aggregate = true)
    public synchronized void unbindDevices(GenericDevice device){
        try {
            deviceEntities.remove(device.getSerialNumber()).unregister();
        }catch(IllegalStateException e){
            LOG.error("failed unregistering device", e);
        }
    }


    class IpojoServiceRegistration implements ServiceRegistration {

        ComponentInstance instance;
        private final Hashtable<String, String> state;

        public IpojoServiceRegistration(ComponentInstance instance, Hashtable<String,String>state) {
            super();
            this.instance = instance;
            this.state = state;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.osgi.framework.ServiceRegistration#getReference()
         */
        public ServiceReference getReference() {
            try {
                ServiceReference[] references;
                references = instance.getContext().getServiceReferences(
                        instance.getClass().getCanonicalName(),
                        "(instance.name=" + instance.getInstanceName()
                                + ")");
                if (references.length > 0)
                    return references[0];
            } catch (InvalidSyntaxException e) {
                LOG.error(" Invalid syntax Exception " , e);
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

        //TODO : To implement
        public void updateState(){}

    }
}


