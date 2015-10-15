package fr.liglab.adele.icasa.context.handler.relation;

import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.architecture.PropertyDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Handler(name = "ContextEntity", namespace = RelationsHandler.RELATIONS_HANDLER_NAMESPACE)
public class RelationsHandler extends PrimitiveHandler{

    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${end.id})",proxy = false)
    List<Relation> relations;

    @Property(name = "end.id")
    String endId;


    private static final Logger LOG = LoggerFactory.getLogger(RelationsHandler.class);

    private ProvidedServiceHandler m_providedServiceHandler;

    public static final String RELATIONS_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.relation";

    private Map<String,Object> m_stateExtensions = new HashMap<>();

    private final Object m_stateExtensionLock = new Object();

    private final Map<Long,Object> m_stateExtensionByServiceId = new HashMap<>();

    private final Object m_stateExtensionByServiceLock = new Object();

    private InstanceManager m_instanceManager;

    @Bind(id = "context.entity.relation")
    public synchronized void bindRelations (Relation relation,ServiceReference serviceReference) {
        LOG.info(" BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        if(relation.getExtendedState().getValue() != null) {

            synchronized (m_stateExtensionByServiceId) {
                m_stateExtensionByServiceId.put((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
            }

            String relationName = relation.getExtendedState().getName();
            synchronized (m_stateExtensionLock) {
                if (m_stateExtensions.containsKey(relationName)) {
                    if (relation.getExtendedState().isAggregate()) {
                        List<Object> stateExtension = (List) m_stateExtensions.get(relationName);
                        stateExtension.add(relation.getExtendedState().getValue());
                        updateExtensionState(relationName, m_stateExtensions.get(relationName));

                    } else {
                        m_stateExtensions.put(relationName, relation.getExtendedState().getValue());
                        updateExtensionState(relationName, m_stateExtensions.get(relationName));
                    }

                } else {
                    if (relation.getExtendedState().isAggregate()) {
                        List<Object> stateExtension = new ArrayList<>();
                        stateExtension.add(relation.getExtendedState().getValue());
                        m_stateExtensions.put(relationName, stateExtension);
                        addExtensionState(relationName, stateExtension);

                    } else {
                        m_stateExtensions.put(relationName, relation.getExtendedState().getValue());
                        addExtensionState(relationName, relation.getExtendedState().getValue());
                    }
                }
            }
        }else {
            LOG.error(" Error Bind relation " + relation.getName() + " on context entity " + relation.getEnd() + " because extended State function return a null value");
        }

    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation,ServiceReference serviceReference) {
        LOG.info(" MODIFIED relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() + " old value " + m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID)));
        String relationName = relation.getExtendedState().getName();
        if(relation.getExtendedState().getValue() != null) {

            synchronized (m_stateExtensionByServiceId) {
                Object oldStateExtension = m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID));
                if (oldStateExtension != null) {
                    if (oldStateExtension.equals(relation.getExtendedState().getValue())) {
                        LOG.info(" Modified call but same value ");
                        return;
                    }
                    LOG.info(" MODIFIED PROPERTY !!!!!! ");
                    synchronized (m_stateExtensionLock) {
                        if (relation.getExtendedState().isAggregate()) {
                            List<Object> stateExtension = (List) m_stateExtensions.get(relationName);
                            int oldValueIndex = stateExtension.indexOf(oldStateExtension);
                            stateExtension.add(oldValueIndex, relation.getExtendedState().getName());
                            m_stateExtensions.put(relationName, stateExtension);
                            updateExtensionState(relationName, stateExtension);
                            m_stateExtensionByServiceId.replace((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
                        } else {
                            m_stateExtensions.put(relationName, relation.getExtendedState().getValue());
                            updateExtensionState(relationName, relation.getExtendedState().getValue());
                            m_stateExtensionByServiceId.replace((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
                        }
                    }
                } else {
                    if (relation.getExtendedState().getValue() != null) {
                        m_stateExtensionByServiceId.put((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
                        synchronized (m_stateExtensionLock) {
                            if (m_stateExtensions.containsKey(relationName)) {
                                if (relation.getExtendedState().isAggregate()) {
                                    List<Object> stateExtension = (List) m_stateExtensions.get(relationName);
                                    stateExtension.add(relation.getExtendedState().getValue());
                                    updateExtensionState(relationName, m_stateExtensions.get(relationName));

                                } else {
                                    m_stateExtensions.put(relationName, relation.getExtendedState().getValue());
                                    updateExtensionState(relationName, m_stateExtensions.get(relationName));
                                }

                            } else {
                                if (relation.getExtendedState().isAggregate()) {
                                    List<Object> stateExtension = new ArrayList<>();
                                    stateExtension.add(relation.getExtendedState().getValue());
                                    m_stateExtensions.put(relationName, stateExtension);
                                    addExtensionState(relationName, stateExtension);

                                } else {
                                    m_stateExtensions.put(relationName, relation.getExtendedState().getValue());
                                    addExtensionState(relationName, relation.getExtendedState().getValue());
                                }
                            }
                        }
                    } else {
                        LOG.error(" Error Modified relation " + relation.getName() + " on context entity " + relation.getEnd() + " because extended State function return a null value");
                    }
                }
            }
        }else {
            LOG.error(" Error Modified relation " + relation.getName() + " on context entity " + relation.getEnd() + " because extended State function return a null value" );
        }
    }

    @Unbind(id = "context.entity.relation")
    public synchronized void unbindRelations(Relation relation,ServiceReference serviceReference) {
        LOG.info(" UNBIND Relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName());
        String relationName = relation.getExtendedState().getName();
        synchronized (m_stateExtensionByServiceLock) {

            Object oldStateExtension = m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID));
            if (oldStateExtension != null) {
                synchronized (m_stateExtensionLock) {
                    if (relation.getExtendedState().isAggregate()) {
                        List<Object> stateExtension = (List) m_stateExtensions.get(relationName);
                        int oldValueIndex = stateExtension.indexOf(oldStateExtension);
                        stateExtension.remove(oldValueIndex);
                        if (stateExtension.isEmpty()) {
                            m_stateExtensions.remove(relationName);
                            removeExtensionState(relationName);
                            m_stateExtensionByServiceId.remove((Long) serviceReference.getProperty(Constants.SERVICE_ID));
                        } else {
                            m_stateExtensions.put(relationName, stateExtension);
                            updateExtensionState(relationName, stateExtension);
                            m_stateExtensionByServiceId.remove((Long) serviceReference.getProperty(Constants.SERVICE_ID));
                        }
                    } else {
                        m_stateExtensions.remove(relationName);
                        removeExtensionState(relationName);
                        m_stateExtensionByServiceId.remove((Long) serviceReference.getProperty(Constants.SERVICE_ID));
                    }
                }
            }
        }
    }


    private void addExtensionState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        if (m_providedServiceHandler != null){
            m_providedServiceHandler.addProperties(hashtable);
        }
    }

    private void removeExtensionState(String propertyId){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, new Object());
        if (m_providedServiceHandler != null) {
            m_providedServiceHandler.removeProperties(hashtable);
        }
    }

    private void updateExtensionState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        if (m_providedServiceHandler != null){
            m_providedServiceHandler.reconfigure(hashtable);
        }
    }

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
        m_instanceManager = getInstanceManager();
        PojoMetadata pojoMetadata = getPojoMetadata();

        String id =  (String)configuration.get("context.entity.id");

        //If not in configuration, find in component type description
        if(id == null){
            boolean findProperties = false;
            for(PropertyDescription description : m_instanceManager.getInstanceDescription().getComponentDescription().getProperties()){
                if (description.getName().equals("context.entity.id")){
                    id = (String)description.getCurrentValue();
                    findProperties=true;
                    break;
                }
            }
            if(findProperties == false){
                LOG.error(" Property context.entity.id missing");
            }
        }

        Properties properties = new Properties();
        properties.put("end.id",id);
        getHandlerManager().reconfigure(properties);

        FieldMetadata injectedState = pojoMetadata.getField("injectedExtensionState");
        m_instanceManager.register(injectedState, this);
    }

    @Override
    public synchronized void stop() {
        m_providedServiceHandler = null;
    }

    @Override
    public synchronized void start() {
        m_providedServiceHandler = (ProvidedServiceHandler) getHandler(HandlerFactory.IPOJO_NAMESPACE + ":provides");

    }

    public Object onGet(Object pojo, String fieldName, Object value){
        synchronized (m_stateExtensionLock) {
            return new HashMap<>(m_stateExtensions);
        }
    }

    @Override
    public HandlerDescription getDescription() {
        return new RelationHandlerDescription(this);
    }

    private class RelationHandlerDescription extends HandlerDescription {
        public RelationHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        public synchronized Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();
            Element extendedStateElement = new Element("Extended State","");
            synchronized (m_stateExtensionLock) {
                for (String extendedName : m_stateExtensions.keySet()) {
                    Element extensionElement = new Element("state extension", "");
                    extensionElement.addAttribute(new Attribute("Name", extendedName));
                    extensionElement.addAttribute(new Attribute("Value", m_stateExtensions.get(extendedName).toString()));
                    extendedStateElement.addElement(extensionElement);
                }
            }

            elem.addElement(extendedStateElement);

            Element relationsElement = new Element("Relations","");
            for (Relation relation : relations){
                Element relationElement = new Element("Relation","");
                relationElement.addAttribute(new Attribute("Name",relation.getName()));
                relationElement.addAttribute(new Attribute("Source",relation.getSource()));
                relationElement.addAttribute(new Attribute("End",relation.getEnd()));
                Element extensionElement = new Element("ExtensionState","");
                extensionElement.addAttribute(new Attribute("Name",relation.getExtendedState().getName()));
                extensionElement.addAttribute(new Attribute("Value",relation.getExtendedState().getValue().toString()));
                extensionElement.addAttribute(new Attribute("Is Aggregate", String.valueOf(relation.getExtendedState().isAggregate())));
                relationElement.addElement(extensionElement);
                relationsElement.addElement(relationElement);
            }

            elem.addElement(relationsElement);

            return elem;
        }
    }
}
