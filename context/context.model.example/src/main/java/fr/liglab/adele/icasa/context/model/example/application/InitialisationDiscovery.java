//package fr.liglab.adele.icasa.context.model.example.application;
//
//
//import fr.liglab.adele.icasa.context.handler.creator.entity.EntityCreator;
//import fr.liglab.adele.icasa.context.handler.creator.entity.EntityCreatorInterface;
//import fr.liglab.adele.icasa.context.model.RelationFactory;
//import fr.liglab.adele.icasa.context.model.example.device.BinaryContextEntityImpl;
//import fr.liglab.adele.icasa.context.model.example.device.DimmerContextEntityImpl;
//import fr.liglab.adele.icasa.context.model.example.device.PresenceContextEntityImpl;
//import fr.liglab.adele.icasa.context.model.example.device.ToogleSwitchContextEntityImpl;
//import fr.liglab.adele.icasa.device.light.BinaryLight;
//import fr.liglab.adele.icasa.device.light.DimmerLight;
//import fr.liglab.adele.icasa.device.power.PowerSwitch;
//import fr.liglab.adele.icasa.device.presence.PresenceSensor;
//import org.apache.felix.ipojo.annotations.*;
//import org.osgi.framework.ServiceRegistration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component(immediate = true)
//@Instantiate
//public class InitialisationDiscovery {
//
//    private static final Logger LOG = LoggerFactory.getLogger(InitialisationDiscovery.class);
//
//    private final Map<String,ServiceRegistration> deviceEntities = new HashMap<>();
//
//    @EntityCreator(entity=BinaryContextEntityImpl.class)
//    private EntityCreatorInterface m_creatorBinary;
//
//    @EntityCreator(entity=DimmerContextEntityImpl.class)
//    private EntityCreatorInterface m_creatorDimmer;
//
//    @EntityCreator(entity=PresenceContextEntityImpl.class)
//    private EntityCreatorInterface m_creatorPresence;
//
//    @EntityCreator(entity=ToogleSwitchContextEntityImpl.class)
//    private EntityCreatorInterface m_creatorToggle;
//
//    @Requires
//    private RelationFactory m_relationFactory;
//
//    @Validate
//    public void start(){
//
//    }
//
//    @Invalidate
//    public void stop(){
//
//    }
//
//    @Bind(id = "binary",optional = true,aggregate = true)
//    public synchronized void bindBinary(BinaryLight device){
//        m_creatorBinary.createEntity(device.getSerialNumber());
//    }
//
//    @Unbind(id = "binary",optional = true,aggregate = true)
//    public synchronized void unbindBinary(BinaryLight device){
//        m_creatorBinary.deleteEntity(device.getSerialNumber());
//    }
//
//    @Bind(id = "dimmer",optional = true,aggregate = true)
//    public synchronized void bindDimmer(DimmerLight device){
//        m_creatorDimmer.createEntity(device.getSerialNumber());
//
//    }
//
//    @Unbind(id = "dimmer",optional = true,aggregate = true)
//    public synchronized void unbindDimmer(DimmerLight device){
//        m_creatorDimmer.deleteEntity(device.getSerialNumber());
//    }
//
//    @Bind(id = "presence",optional = true,aggregate = true)
//    public synchronized void bindPresence(PresenceSensor device){
//        m_creatorPresence.createEntity(device.getSerialNumber());
//    }
//
//    @Unbind(id = "presence",optional = true,aggregate = true)
//    public synchronized void unbindPresence(PresenceSensor device){
//        m_creatorPresence.deleteEntity(device.getSerialNumber());
//    }
//
//    @Bind(id = "toogle",optional = true,aggregate = true)
//    public synchronized void bindToogle(PowerSwitch device){
//        m_creatorToggle.createEntity(device.getSerialNumber());
//    }
//
//    @Unbind(id = "toogle",optional = true,aggregate = true)
//    public synchronized void unbindToogle(PowerSwitch device){
//        m_creatorToggle.deleteEntity(device.getSerialNumber());
//    }
//}