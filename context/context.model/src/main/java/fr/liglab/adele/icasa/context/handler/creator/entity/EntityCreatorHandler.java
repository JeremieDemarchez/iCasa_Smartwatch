package fr.liglab.adele.icasa.context.handler.creator.entity;

import fr.liglab.adele.icasa.context.model.RelationFactory;
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
public class EntityCreatorHandler extends PrimitiveHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCreatorHandler.class);

    public static final String ENTITY_CREATOR_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.creator.entity";

    private InstanceManager m_instanceManager;

    private HandlerManager m_handlerManager;

    private ProvidedServiceHandler m_providedServiceHandler;

    private final Map<String, EntityCreatorInterface> creators = new HashMap<>();

    @Requires(optional = false)
    private RelationFactory m_relationFactory;

    @Requires(id ="entity.factory", filter = "(factory.name=${factory.filter})", optional = true, proxy = false)
    //@Requires(id ="entity.factory",optional = true)
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
            creators.put(field, new EntityCreatorImpl(entity));

            PojoMetadata pojoMetadata = getPojoMetadata();
            FieldMetadata creator = pojoMetadata.getField(field);
            m_instanceManager.register(creator, this);
        }

    }

    public Object onGet(Object pojo, String fieldName, Object value){
        return creators.get(fieldName);
    }

    @Override
    public HandlerDescription getDescription() {
        return new EntityCreatorHandlerDescription(this);
    }

    private class EntityCreatorHandlerDescription extends HandlerDescription {
        public EntityCreatorHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        public synchronized Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();

            Element creatorElements = new Element("Entity Creators","");

            for (Map.Entry<String, EntityCreatorInterface> creator : creators.entrySet()){
                Element creatorElement = new Element("Entity Creator","");
                creatorElement.addAttribute(new Attribute("Name",creator.getKey()));
                creatorElements.addElement(creatorElement);
            }

            elem.addElement(creatorElements);

            return elem;
        }
    }

    private class EntityCreatorImpl implements EntityCreatorInterface{

        private final Map<String,ServiceRegistration> entities = new HashMap<>();

        private String m_entityClass;

        private String m_entityPackage;

        protected EntityCreatorImpl(String entity){
            m_entityClass = entity;
            int lastPoint = entity.lastIndexOf(".");
            m_entityPackage = entity.substring(0, lastPoint) + ".";
        }

        @Override
        public void createEntity(String id){
            ComponentInstance instance;

            Hashtable properties = new Hashtable();
            properties.put("factory.filter", m_entityClass);
            m_handlerManager.reconfigure(properties);

            properties = new Hashtable();
            properties.put("context.entity.id", id);
            properties.put("instance.name", m_entityPackage + id);

            if (m_entityFactory!=null) {
                try {
                    /*factory prête?*/
                    instance = m_entityFactory.createComponentInstance(properties);
                    ServiceRegistration sr = new IpojoServiceRegistration(instance);

                    entities.put(id, sr);
                } catch (UnacceptableConfiguration unacceptableConfiguration) {
                    LOG.error("Relation instantiation failed", unacceptableConfiguration);
                } catch (MissingHandlerException e) {
                    LOG.error("Relation instantiation failed", e);
                } catch (ConfigurationException e) {
                    LOG.error("Relation instantiation failed", e);
                }
            }
        }

        @Override
        public void deleteEntity(String id){

            try {
                if (m_relationFactory.findIdsByEndpoint(id) != null) {
                    for (UUID uuid : m_relationFactory.findIdsByEndpoint(id)) {
                        m_relationFactory.deleteRelation(uuid);
                    }
                    entities.remove(id).unregister();
                }
            }catch(IllegalStateException e){
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
