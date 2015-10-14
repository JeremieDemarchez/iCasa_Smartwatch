package fr.liglab.adele.icasa.context.model.example.device;


import fr.liglab.adele.icasa.context.model.RelationFactory;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Instantiate
public class DeviceDiscovery{

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscovery.class);

    private final Map<String,ServiceRegistration> deviceEntities = new HashMap<>();

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.device.BinaryContextEntityImpl)")
    Factory binaryEntityFactory;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.device.DimmerContextEntityImpl)")
    Factory dimmerEntityFactory;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.device.PresenceContextEntityImpl)")
    Factory presenceEntityFactory;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.device.ToogleSwitchContextEntityImpl)")
    Factory toogleEntityFactory;

    @Requires
    private RelationFactory m_relationFactory;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    private void createEntity(Factory facto,GenericDevice device){
        ComponentInstance instance;

        Hashtable properties = new Hashtable();
        properties.put("context.entity.id", device.getSerialNumber());
        properties.put("instance.name", "fr.liglab.adele.icasa.context.model.example."+device.getSerialNumber());

        try {
            instance = facto.createComponentInstance(properties);
            ServiceRegistration sr = new IpojoServiceRegistration(
                    instance);

            deviceEntities.put(device.getSerialNumber(),sr);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Relation instantiation failed",unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Relation instantiation failed",e);
        } catch (ConfigurationException e) {
            LOG.error("Relation instantiation failed",e);
        }
    }

    private void removeEntity(String serialNumber){
        try {
            for(UUID uuid : m_relationFactory.findIdsByEndpoint(serialNumber)){
                m_relationFactory.deleteRelation(uuid);
            }
            deviceEntities.remove(serialNumber).unregister();
        }catch(IllegalStateException e){
            LOG.error("failed unregistering device", e);
        }
    }

    @Bind(id = "binary",optional = true,aggregate = true)
    public synchronized void bindBinary(BinaryLight device){
      createEntity(binaryEntityFactory, device);
    }

    @Unbind(id = "binary",optional = true,aggregate = true)
    public synchronized void unbindBinary(BinaryLight device){
        removeEntity(device.getSerialNumber());
    }

    @Bind(id = "dimmer",optional = true,aggregate = true)
    public synchronized void bindDimmer(DimmerLight device){
        createEntity(dimmerEntityFactory,device);

    }

    @Unbind(id = "dimmer",optional = true,aggregate = true)
    public synchronized void unbindDimmer(DimmerLight device){
        removeEntity(device.getSerialNumber());
    }

    @Bind(id = "presence",optional = true,aggregate = true)
    public synchronized void bindPresence(PresenceSensor device){
        createEntity(presenceEntityFactory,device);
    }

    @Unbind(id = "presence",optional = true,aggregate = true)
    public synchronized void unbindPresence(PresenceSensor device){
        removeEntity(device.getSerialNumber());
    }

    @Bind(id = "toogle",optional = true,aggregate = true)
    public synchronized void bindToogle(PowerSwitch device){
        createEntity(toogleEntityFactory,device);
    }

    @Unbind(id = "toogle",optional = true,aggregate = true)
    public synchronized void unbindToogle(PowerSwitch device){
        removeEntity(device.getSerialNumber());
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

    }
}