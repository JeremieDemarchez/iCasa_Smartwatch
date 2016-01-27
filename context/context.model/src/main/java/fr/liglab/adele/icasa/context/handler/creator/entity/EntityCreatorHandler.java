package fr.liglab.adele.icasa.context.handler.creator.entity;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/*UN HANDLER PAR COMPOSANT? --> */
@Handler(name = "EntityCreator", namespace = EntityCreatorHandler.ENTITY_CREATOR_HANDLER_NAMESPACE)
@Provides(specifications = CreatorHandlerIntrospection.class)
public class EntityCreatorHandler extends PrimitiveHandler implements CreatorHandlerIntrospection {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCreatorHandler.class);

    public static final String ENTITY_CREATOR_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.creator.entity";

    private InstanceManager m_instanceManager;

    private HandlerManager m_handlerManager;

    private final Map<String, String> impl_by_field = new HashMap<>();

    private final Map<String, EntityCreatorImpl> creators_by_impl = new HashMap<>();

    @Requires(id ="entity.factory", filter = "(factory.name=${factory.filter})", optional = false, proxy = false)
    private Factory m_entityFactory;

    @Property(name = "factory.filter")
    private String m_factoryFilter;

    @Override
    public synchronized void start() {
        LOG.info(" Handler Start");
    }

    @Override
    public synchronized void stop() {
        LOG.info(" Handler Stop");
    }

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {

        m_instanceManager = getInstanceManager();
        m_handlerManager = getHandlerManager();

        Element[] creatorElements = metadata.getElements("EntityCreator", ENTITY_CREATOR_HANDLER_NAMESPACE);

        for (Element e: creatorElements){
            String entity = e.getAttribute("entity");
            String field = e.getAttribute("field");
            creators_by_impl.put(entity, new EntityCreatorImpl(entity));
            impl_by_field.put(field, entity);

            PojoMetadata pojoMetadata = getPojoMetadata();
            FieldMetadata creator = pojoMetadata.getField(field);
            m_instanceManager.register(creator, this);

            /**
             * Need to reconfigure the handler with the right spec
             */
            Properties properties = new Properties();
            properties.put("factory.filter",entity);
            getHandlerManager().reconfigure(properties);

            /*TODO default : ENABLED?*/
            switchCreation(entity, true);
        }


    }


    public Object onGet(Object pojo, String fieldName, Object value){

        String implementation = impl_by_field.get(fieldName);
        if (implementation != null){
            return creators_by_impl.get(implementation);
        } else {
            return null;
        }
    }

    @Override
    public HandlerDescription getDescription() {
        return new EntityCreatorHandlerDescription(this);
    }

    @Override
    public Set<String> getImplementations() {

        return new HashSet<>(impl_by_field.values());
    }

    @Override
    public Set<String> getPendingInstances(String implementation) {
        EntityCreatorImpl entityCreator = creators_by_impl.get(implementation);
        if (entityCreator != null){
            return entityCreator.getPendingEntities();
        } else {
            return null;
        }
    }

    @Override
    public boolean getImplentationState(String implementation) {
        EntityCreatorImpl entityCreator = creators_by_impl.get(implementation);
        if (entityCreator != null){
            return entityCreator.getState();
        } else {return false;}
    }

    @Override
    public boolean switchCreation(String implementation, boolean enable) {

        EntityCreatorImpl entityCreator = creators_by_impl.get(implementation);
        boolean result = false;
        if (entityCreator != null){
            if (enable){
                entityCreator.enableCreation();
            } else {
                entityCreator.disableCreation();
            }
            result = true;
        } else {}

        return result;
    }

    @Override
    public boolean deleteAllInstancesOf(String implementation) {
        EntityCreatorImpl entityCreator = creators_by_impl.get(implementation);
        if (entityCreator != null){
            entityCreator.deleteAllEntities();
            return true;
        } else {return false;}
    }

    private class EntityCreatorHandlerDescription extends HandlerDescription {

        public EntityCreatorHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        public synchronized Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();

            Element creatorElements = new Element("Entity Creators","");

            for (String field : impl_by_field.keySet()){
                Element creatorElement = new Element("Entity Creator","");
                creatorElement.addAttribute(new Attribute("field : ", field));
                creatorElement.addAttribute(new Attribute("Spec ", impl_by_field.get(field)));
                creatorElements.addElement(creatorElement);
            }

            elem.addElement(creatorElements);
            for (org.apache.felix.ipojo.Handler handler : getHandlerManager().getRegisteredHandlers()){
                HandlerDescription description = handler.getDescription();
                creatorElements.addElement(description.getHandlerInfo());
            }
            return elem;
        }
    }

    private class EntityCreatorImpl implements Creator {

        private String m_entityImplementation;

        private String m_entityPackage;

        private boolean m_switch;

        private final Map<String,ServiceRegistration> entities_reg = new HashMap<>();

        private final Map<String, Map<String, Object>> created_entities = new HashMap<>();

        private final Map<String, Map<String, Object>> pending_entities = new HashMap<>();


        protected EntityCreatorImpl(String entity){
            m_entityImplementation = entity;
            int lastPoint = entity.lastIndexOf(".");
            m_entityPackage = entity.substring(0, lastPoint) + ".";
            m_switch = false;
        }

        protected String getImplementation(){
            return m_entityImplementation;
        }

        protected Set<String> getPendingEntities(){
            return pending_entities.keySet();
        }

        protected boolean getState(){return m_switch;}

        protected synchronized void enableCreation() {

            synchronized (pending_entities){
                synchronized (created_entities){
                    Set<String> entities_to_remove = new HashSet<>();
                    Map<String, Object> initialization;
                    String id;
                    for(Map.Entry<String, Map<String, Object>> entity : pending_entities.entrySet()){
                        id = entity.getKey();
                        initialization = entity.getValue();
                        createInstance(id, initialization);
                        created_entities.put(id, initialization);
                        entities_to_remove.add(id);
                    }

                    for (String id_r : entities_to_remove){
                        pending_entities.remove(id_r);
                    }

                    m_switch = true;
                }
            }
        }

        protected synchronized void disableCreation() {

            synchronized (pending_entities){
                synchronized (created_entities) {
                    m_switch = false;

                    Set<String> entities_to_remove = new HashSet<>();
                    Map<String, Object> initialization;
                    String id;

                    for (Map.Entry<String, Map<String, Object>> entity : created_entities.entrySet()) {
                        id = entity.getKey();
                        initialization = entity.getValue();
                        deleteInstance(id);
                        entities_to_remove.add(id);
                        pending_entities.put(id, initialization);
                    }

                    for (String id_r : entities_to_remove){
                        created_entities.remove(id_r);
                    }
                }
            }
        }

        @Override
        public Set<String> getEntityIdsCreated() {
            synchronized (created_entities){
                return new HashSet<>(created_entities.keySet());
            }
        }

        @Override
        public synchronized void createEntity(String id){
            createEntity(id, null);
        }

        @Override
        public void createEntity(String id,  Map<String, Object> initialization) {
            synchronized (pending_entities){
                synchronized (created_entities) {
                    if (m_switch) {
                        createInstance(id, initialization);
                        created_entities.put(id, initialization);
                    } else {
                        pending_entities.put(id, initialization);
                    }
                }
            }
        }


        @Override
        public synchronized void deleteEntity(String id){

            synchronized (pending_entities){
                synchronized (created_entities) {
                    if (m_switch) {
                        deleteInstance(id);
                        created_entities.remove(id);
                    } else {
                        pending_entities.remove(id);
                    }
                }
            }
        }

        @Override
        public synchronized void deleteAllEntities() {
            synchronized (pending_entities){
                synchronized (created_entities) {

                    for(String id : created_entities.keySet()){
                        deleteInstance(id);
                    }
                    created_entities.clear();

                    for(String id : pending_entities.keySet()){
                        deleteInstance(id);
                    }
                    pending_entities.clear();
                }
            }
        }

        private synchronized void createInstance (String id, Map<String, Object> initialization){
            ComponentInstance instance;

            Hashtable properties = new Hashtable();
            /**     properties.put("factory.filter", m_entityImplementation);
             m_handlerManager.reconfigure(properties);**/

            properties = new Hashtable();
            properties.put(ContextEntity.CONTEXT_ENTITY_ID, id);
            properties.put("instance.name", m_entityPackage + id);
            LOG.info(" Try to create entity " + id);
            if (initialization != null){
                properties.put("context.entity.init", initialization);
                for (String key : initialization.keySet()){
                    LOG.info(" param : " + key + " value : " + initialization.get(key));
                }
            }
            try {
                    /*factory ready?*/
                instance = m_entityFactory.createComponentInstance(properties);
                ServiceRegistration sr = new IpojoServiceRegistration(instance);

                synchronized (entities_reg){
                    entities_reg.put(id, sr);
                }
                LOG.info(" Entity " + id + " creation succeed ");
            } catch (UnacceptableConfiguration unacceptableConfiguration) {
                LOG.error("Entity " + id + " instantiation failed", unacceptableConfiguration);
            } catch (MissingHandlerException e) {
                LOG.error("Entity " + id + " instantiation failed", e);
            } catch (ConfigurationException e) {
                LOG.error("Entity " + id + " instantiation failed", e);
            }
        }

        private void deleteInstance (String id){

            try {
                synchronized (entities_reg){
                    ServiceRegistration sr = entities_reg.remove(id);
                    if (sr != null){
                        sr.unregister();
                    }
                }
            } catch(IllegalStateException e) {
                error("Failed unregistering Entity " + id, e);
            }
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
                    error(" Invalid syntax Exception ", e);
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

}