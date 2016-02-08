package fr.liglab.adele.icasa.context.runtime.handler.creator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.internal.HandlerReference;
import fr.liglab.adele.icasa.context.model.annotations.provider.Entity;
import fr.liglab.adele.icasa.context.model.introspection.EntityCreatorHandlerIntrospection;

@Handler(name = HandlerReference.CREATOR_HANDLER, namespace = HandlerReference.NAMESPACE)
@Provides(specifications = EntityCreatorHandlerIntrospection.class)
public class CreatorHandler extends PrimitiveHandler implements EntityCreatorHandlerIntrospection {

    private static final Logger LOG = LoggerFactory.getLogger(CreatorHandler.class);

    private InstanceManager myInstanceManager;

    private final Map<String, String> myImplByField = new HashMap<>();

    private final Map<String, EntityCreatorImpl> myCreatorsByImpl = new HashMap<>();

    @Requires(id ="entity.factory", filter = "(factory.name=${factory.filter})", optional = false, proxy = false)
    private Factory myEntityFactory;

    @Property(name = "factory.filter")
    private String myFactoryFilter;

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

        myInstanceManager = getInstanceManager();

        Element[] creatorElements = metadata.getElements(HandlerReference.CREATOR_HANDLER, HandlerReference.NAMESPACE);

        for (Element creator: creatorElements) {
            
        	
        	String fieldName		= creator.getAttribute("field");
        	String componentName	= myInstanceManager.getClassName();
        	
        	FieldMetadata field		 = getPojoMetadata().getField(fieldName);
			
        	if (field == null) {
				throw new ConfigurationException("Malformed Manifest : the specified creator field '"+fieldName+"' is not defined in class "+componentName);
        	}

            String entity 				= creator.getAttribute("entity");
            String relation				= creator.getAttribute("relation");
        
            if (entity != null) {

            	/*
            	 * instantiate the creator for the given field
            	 */
            	myCreatorsByImpl.put(entity, new EntityCreatorImpl(entity));
                myImplByField.put(fieldName, entity);
                
                /*
                 * Reconfigure the handler with the right spec to track the factory
                 * 
                 * TODO This doesn't work if there are several creators in the same component
                 */
                Properties properties = new Properties();
                properties.put("factory.filter",entity);
                getHandlerManager().reconfigure(properties);

                /*TODO default : ENABLED?*/
                switchCreation(entity, true);
            }
            else if (relation != null) {
            	
            }
            else {
				throw new ConfigurationException("Malformed Manifest : the creator entity or relation is not specified for field '"+fieldName+"' is not defined in class "+componentName);
            }
            
            myInstanceManager.register(getPojoMetadata().getField(fieldName),this);
        }


    }


    public Object onGet(Object pojo, String fieldName, Object value){

        String implementation = myImplByField.get(fieldName);
        if (implementation != null){
            return myCreatorsByImpl.get(implementation);
        } else {
            return null;
        }
    }

    @Override
    public HandlerDescription getDescription() {
        return new EntityCreatorHandlerDescription(this);
    }

    @Override
    public String getAttachedComponentInstanceName() {
        return myInstanceManager.getInstanceName();
    }

    @Override
    public Set<String> getImplementations() {

        return new HashSet<>(myImplByField.values());
    }

    @Override
    public Set<String> getPendingInstances(String implementation) {
        EntityCreatorImpl entityCreator = myCreatorsByImpl.get(implementation);
        if (entityCreator != null){
            return entityCreator.getPendingEntities();
        } else {
            return null;
        }
    }

    @Override
    public boolean getImplentationState(String implementation) {
        EntityCreatorImpl entityCreator = myCreatorsByImpl.get(implementation);
        if (entityCreator != null){
            return entityCreator.getState();
        } else {return false;}
    }

    @Override
    public boolean switchCreation(String implementation, boolean enable) {

        EntityCreatorImpl entityCreator = myCreatorsByImpl.get(implementation);
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
        EntityCreatorImpl entityCreator = myCreatorsByImpl.get(implementation);
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

            for (Map.Entry<String,String> entry : myImplByField.entrySet()){
                Element creatorElement = new Element("Entity Creator","");
                creatorElement.addAttribute(new Attribute("field : ", entry.getKey()));
                creatorElement.addAttribute(new Attribute("Spec ", entry.getValue()));
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

    private class EntityCreatorImpl implements Entity.Creator<Object> {

        private String myEntityImplementation;

        private String myEntityPackage;

        private boolean mySwitch;

        private final Map<String,ServiceRegistration> myEntitiesReg = new HashMap<>();

        private final Map<String, Map<String, Object>> myCreatedEntities = new HashMap<>();

        private final Map<String, Map<String, Object>> myPendingEntities = new HashMap<>();


        protected EntityCreatorImpl(String entity){
            myEntityImplementation = entity;
            int lastPoint = entity.lastIndexOf(".");
            myEntityPackage = lastPoint != -1 ? entity.substring(0, lastPoint) + "." : "";
            mySwitch = false;
        }

        protected String getImplementation(){
            return myEntityImplementation;
        }

        protected Set<String> getPendingEntities(){
            return myPendingEntities.keySet();
        }

        protected boolean getState(){return mySwitch;}

        protected synchronized void enableCreation() {

            synchronized (myPendingEntities){
                synchronized (myCreatedEntities){
                    Set<String> entities_to_remove = new HashSet<>();
                    Map<String, Object> initialization;
                    String id;
                    for(Map.Entry<String, Map<String, Object>> entity : myPendingEntities.entrySet()){
                        id = entity.getKey();
                        initialization = entity.getValue();
                        createInstance(id, initialization);
                        myCreatedEntities.put(id, initialization);
                        entities_to_remove.add(id);
                    }

                    for (String id_r : entities_to_remove){
                        myPendingEntities.remove(id_r);
                    }

                    mySwitch = true;
                }
            }
        }

        protected synchronized void disableCreation() {

            synchronized (myPendingEntities){
                synchronized (myCreatedEntities) {
                    mySwitch = false;

                    Set<String> entities_to_remove = new HashSet<>();
                    Map<String, Object> initialization;
                    String id;

                    for (Map.Entry<String, Map<String, Object>> entity : myCreatedEntities.entrySet()) {
                        id = entity.getKey();
                        initialization = entity.getValue();
                        deleteInstance(id);
                        entities_to_remove.add(id);
                        myPendingEntities.put(id, initialization);
                    }

                    for (String id_r : entities_to_remove){
                        myCreatedEntities.remove(id_r);
                    }
                }
            }
        }

        @Override
        public Set<String> getEntityIdsCreated() {
            synchronized (myCreatedEntities){
                return new HashSet<>(myCreatedEntities.keySet());
            }
        }

        @Override
        public synchronized void createEntity(String id) {
            createEntity(id, null);
        }

        @Override
        public void createEntity(String id,  Map<String, Object> initialization) {
            synchronized (myPendingEntities){
                synchronized (myCreatedEntities) {
                    if (mySwitch) {
                        createInstance(id, initialization);
                        myCreatedEntities.put(id, initialization);
                    } else {
                        myPendingEntities.put(id, initialization);
                    }
                }
            }
            
        }


        @Override
        public synchronized void deleteEntity(String id){

            synchronized (myPendingEntities){
                synchronized (myCreatedEntities) {
                    if (mySwitch) {
                        deleteInstance(id);
                        myCreatedEntities.remove(id);
                    } else {
                        myPendingEntities.remove(id);
                    }
                }
            }
        }

        @Override
        public synchronized void deleteAllEntities() {
            synchronized (myPendingEntities){
                synchronized (myCreatedEntities) {

                    for(String id : myCreatedEntities.keySet()){
                        deleteInstance(id);
                    }
                    myCreatedEntities.clear();

                    for(String id : myPendingEntities.keySet()){
                        deleteInstance(id);
                    }
                    myPendingEntities.clear();
                }
            }
        }

        private synchronized void createInstance (String id, Map<String, Object> initialization){
            ComponentInstance instance;



            Hashtable properties = new Hashtable();
            properties.put(ContextEntity.CONTEXT_ENTITY_ID, id);
            properties.put("instance.name", myEntityPackage + id);
            LOG.info(" Try to create entity " + id);
            if (initialization != null){
                properties.put("context.entity.init", initialization);
                for (String key : initialization.keySet()){
                    LOG.info(" param : " + key + " value : " + initialization.get(key));
                }
            }
            try {
                    /*factory ready?*/
                instance = myEntityFactory.createComponentInstance(properties);
                ServiceRegistration sr = new IpojoServiceRegistration(instance);

                synchronized (myEntitiesReg){
                    myEntitiesReg.put(id, sr);
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
                synchronized (myEntitiesReg){
                    ServiceRegistration sr = myEntitiesReg.remove(id);
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