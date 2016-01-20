package fr.liglab.adele.icasa.context.handler.creator.entity;

import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreator;
import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreatorInterface;
import fr.liglab.adele.icasa.context.model.RelationImpl;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
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
@Provides
public class EntityCreatorHandler extends PrimitiveHandler implements EntityCreatorManagementInterface {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCreatorHandler.class);

    public static final String ENTITY_CREATOR_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.creator.entity";

    private InstanceManager m_instanceManager;

    private HandlerManager m_handlerManager;

    private ProvidedServiceHandler m_providedServiceHandler;

    private final Map<String, String> impl_by_field = new HashMap<>();

    private final Map<String, EntityCreatorImpl> creators_by_impl = new HashMap<>();

    @RelationCreator(relation = RelationImpl.class)
    private RelationCreatorInterface m_relationCreator;

    @Requires(id ="entity.factory", filter = "(factory.name=${factory.filter})", optional = true, proxy = false)
    private Factory m_entityFactory;

    @Property(name = "factory.filter")
    private String m_factoryFilter;


    @Override
    public synchronized void start() {

        m_providedServiceHandler = (ProvidedServiceHandler) getHandler(HandlerFactory.IPOJO_NAMESPACE + ":provides");
    }

    @Override
    public synchronized void stop() {
        m_providedServiceHandler = null;
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

            /*deafault : ENABLED*/
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
        return false;
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

    private class EntityCreatorHandlerDescription extends HandlerDescription {

        public EntityCreatorHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        public synchronized Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();

            Element creatorElements = new Element("Entity Creators","");

            for (Map.Entry<String, String> creator : impl_by_field.entrySet()){
                Element creatorElement = new Element("Entity Creator","");
                creatorElement.addAttribute(new Attribute("Name",creator.getKey()));
                creatorElements.addElement(creatorElement);
            }

            elem.addElement(creatorElements);

            return elem;
        }
    }

    private class EntityCreatorImpl implements EntityCreatorInterface{

        private String m_entityImplementation;

        private String m_entityPackage;

        private boolean m_switch;

        private final Map<String,ServiceRegistration> entities_reg = new HashMap<>();

        private final Set<String> created_entities = new HashSet<>();

        private final Set<String> pending_entities = new HashSet<>();


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
            return pending_entities;
        }

        protected synchronized void enableCreation() {

            synchronized (pending_entities){
                synchronized (created_entities){
                    Set<String> pes = new HashSet<>(pending_entities);
                    Set<String> entities_to_remove = new HashSet<>();
                    for(String id : pes){
                        createInstance(id, null);
                        created_entities.add(id);
                        entities_to_remove.add(id);
                    }
                    pending_entities.removeAll(entities_to_remove);

                    /*TODO NOTIFY RELATIONS*/
                    m_switch = true;
                }
            }
        }

        protected synchronized void disableCreation() {

            synchronized (pending_entities){
                synchronized (created_entities) {
                    m_switch = false;

                    Set<String> entities_to_remove = new HashSet<>();
                    for (String id : created_entities) {
                        deleteInstance(id);
                        entities_to_remove.add(id);
                        pending_entities.add(id);
                    }
                    created_entities.removeAll(entities_to_remove);

                    /***/
                    /*TODO NOTIFY RELATIONS*/
                }
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
                        created_entities.add(id);
                    } else {
                        pending_entities.add(id);
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

        private void createInstance (String id, Map<String, Object> initialization){
            ComponentInstance instance;

            Hashtable properties = new Hashtable();
            properties.put("factory.filter", m_entityImplementation);
            m_handlerManager.reconfigure(properties);

            properties = new Hashtable();
            properties.put("context.entity.id", id);
            properties.put("instance.name", m_entityPackage + id);

            if (m_entityFactory!=null) {
                try {
                    /*factory prête?*/
                    instance = m_entityFactory.createComponentInstance(properties);
                    ServiceRegistration sr = new IpojoServiceRegistration(instance);

                    synchronized (entities_reg){
                        entities_reg.put(id, sr);
                    }
//                    m_relationCreator.entityCreated(id);
                } catch (UnacceptableConfiguration unacceptableConfiguration) {
                    LOG.error("Relation instantiation failed", unacceptableConfiguration);
                } catch (MissingHandlerException e) {
                    LOG.error("Relation instantiation failed", e);
                } catch (ConfigurationException e) {
                    LOG.error("Relation instantiation failed", e);
                }
            }
        }

        private void deleteInstance (String id){

            try {
//                m_relationCreator.entityRemoved(id);
                synchronized (entities_reg){
                    entities_reg.remove(id).unregister();
                }
            } catch(IllegalStateException e) {
                LOG.error("failed unregistering device", e);
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

}