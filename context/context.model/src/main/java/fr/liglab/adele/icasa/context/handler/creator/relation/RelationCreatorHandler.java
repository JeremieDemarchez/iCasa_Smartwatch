package fr.liglab.adele.icasa.context.handler.creator.relation;

import fr.liglab.adele.icasa.context.model.RelationFactory;
import fr.liglab.adele.icasa.context.model.RelationType;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Requires;
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

@Handler(name = "RelationCreator", namespace = RelationCreatorHandler.RELATION_CREATOR_HANDLER_NAMESPACE)
public class RelationCreatorHandler extends PrimitiveHandler implements _RelationCreatorManagement {

    private static final Logger LOG = LoggerFactory.getLogger(RelationCreatorHandler.class);

    public static final String RELATION_CREATOR_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.creator.relation";

    private InstanceManager m_instanceManager;

    private HandlerManager m_handlerManager;

    private ProvidedServiceHandler m_providedServiceHandler;

    private final Map<String, String> type_by_field = new HashMap<>();

    private final Map<String, RelationCreatorImpl> creators_by_type = new HashMap<>();

//    @Requires(id ="relation.factory", filter = "(factory.name=${factory.filter})", optional = true, proxy = false)
//    private Factory m_relationFactory;

//    @Property(name = "factory.filter")
//    private String m_factoryFilter;


    @Requires(optional = false)
    private RelationFactory m_relationFactory;

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

        Element[] creatorElements = metadata.getElements("RelationCreator", RELATION_CREATOR_HANDLER_NAMESPACE);

        for (Element e: creatorElements){
            String relation = e.getAttribute("relation");
            String field = e.getAttribute("field");
            creators_by_type.put(relation, new RelationCreatorImpl(relation));
            type_by_field.put(field, relation);

            PojoMetadata pojoMetadata = getPojoMetadata();
            FieldMetadata creator = pojoMetadata.getField(field);
            m_instanceManager.register(creator, this);

            /*TODO A ENLEVER*/
            switchCreation(relation, true);
        }

    }

    public Object onGet(Object pojo, String fieldName, Object value){
        String implementation = type_by_field.get(fieldName);
        if (implementation != null){
            return creators_by_type.get(implementation);
        } else {
            return null;
        }
    }

    @Override
    public HandlerDescription getDescription() {
        return new RelationCreatorHandlerDescription(this);
    }

    @Override
    public Set<String> getTypes() {

        return new HashSet<>(type_by_field.values());
    }

    @Override
    public Set<String> getPendingInstances(String type) {
//        RelationCreatorImpl relationCreator = creators_by_impl.get(implementation);
//        if (relationCreator != null){
//            return relationCreator.getPendingRelations();
//        } else {
//            return null;
//        }

        return null;
    }

    @Override
    public boolean getTypeState(String type) {
        return false;
    }

    @Override
    public void switchCreation(String implementation, boolean enable) {

        RelationCreatorImpl relationCreator = creators_by_type.get(implementation);
        if (relationCreator != null){
            if (enable){
                relationCreator.enableCreation();
            } else {
                relationCreator.disableCreation();
            }
        } else {}
    }

    private class RelationCreatorHandlerDescription extends HandlerDescription {
        public RelationCreatorHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        public synchronized Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();

            Element creatorElements = new Element("Relation Creators","");

            for (Map.Entry<String, String> creator : type_by_field.entrySet()){
                Element creatorElement = new Element("Relation Creator","");
                creatorElement.addAttribute(new Attribute("Name",creator.getKey()));
                creatorElements.addElement(creatorElement);
            }

            elem.addElement(creatorElements);

            return elem;
        }
    }

    private class RelationCreatorImpl implements _RelationCreator {
        /*enabling relations could have been done with RelationType (here done with impl)*/

        private String m_relationImplementation;

        private String m_relationPackage;     /*useless for now (only one implementation)*/

        private boolean m_switch;

        private final Map<UUID, RelationTuple> relations = new HashMap<>();

        private final Map<UUID, RelationTuple> pending_relations = new HashMap<>();


        protected RelationCreatorImpl(String relation){
            m_relationImplementation = relation;
            int lastPoint = relation.lastIndexOf(".");
            m_relationPackage = relation.substring(0, lastPoint) + ".";
        }

        protected String getImplementation(){
//            return m_relationImplementation;
            return null;
        }

        protected Set<String> getPendingRelations(){
//            Set<String> pending_relations_string = new HashSet<>();
//            for(RelationTuple relationTuple : pending_relations.values()){
//                pending_relations_string.add(relationTuple.toString());
//            }
//            return pending_relations_string;
            return null;
        }

        protected synchronized void enableCreation() {
//            /*TODO CHECK ENTITY -> possible only with the id?*/
//
//            /*A VERIFIER*/
//            Iterator<RelationTuple> it = pending_relations.values().iterator();
//            while(it.hasNext()){
//                RelationTuple rt = it.next();
//                UUID uuid = rt.getUUID();
//                m_relationFactory.createRelation(uuid, rt.getRelationType(), rt.getSource(), rt.getEnd());
//                relations.put(uuid, rt);
//                pending_relations.remove(uuid);
//            }
//            m_switch = true;
        }

        protected synchronized void disableCreation() {
//
//            m_switch = false;
//            /*A VERIFIER*/
//            Iterator<RelationTuple> it = relations.values().iterator();
//            while(it.hasNext()){
//                RelationTuple rt = it.next();
//                UUID uuid = rt.getUUID();
//                m_relationFactory.deleteRelation(uuid);
//                pending_relations.put(uuid, rt);
//                relations.remove(uuid);
//            }
        }

        @Override
        public synchronized UUID createRelation(RelationType relationType, String source, String end) {
//            UUID relationId = UUID.randomUUID();
//            RelationTuple rt = new RelationTuple(relationId, relationType, source, end);
//
//            if (m_switch){
//                m_relationFactory.createRelation(relationId, relationType, source, end);
//                relations.put(relationId, rt);
//            } else {
//                pending_relations.put(relationId, rt);
//            }
//            return relationId;
            return null;
        }

        @Override
        public synchronized void deleteRelation(UUID relationId) {
//
//            if (m_switch){
//                m_relationFactory.deleteRelation(relationId);
//                relations.remove(relationId);
//            } else {
//                pending_relations.remove(relationId);
//            }
        }

        @Override
        public synchronized void updateRelation(UUID relationId, String newSource, String newEnd) {
            /*TODO*/
        }

        @Override
        public void deleteRelationsOfEntity(String id) {

        }

        @Override
        public void removeRelationsOfEntity(String id) {

        }

        @Override
        public void retrieveRelationsOfEntity(String id) {

        }

        @Override
        public UUID findId(String name, String source, String end) {
            /*TODO*/
            return null;
        }

        @Override
        public Set<UUID> findIdsByEndpoint(String endpoint) {

//            return m_relationFactory.findIdsByEndpoint(endpoint);
            return null;
        }

        @Override
        public Set<UUID> findIdsBySource(String source) {

//            return m_relationFactory.findIdsBySource(source);
            return null;
        }

        @Override
        public Set<UUID> findIdsByEnd(String end) {

//            return m_relationFactory.findIdsByEnd(end);
            return null;
        }

//        @Override
//        public synchronized void entityCreated(String id) {
//            /*TODO Sync?*/
//            /*TODO MODIFY --> RELATION EN COMMUN OU PAS*/
//
//            /*TODO Iterator*/
////            for(RelationTuple rt : pending_relations.values()){
////                /*TRY Creating Relation (other one might be invalid too)*/
////                UUID uuid = rt.getUUID();
////                m_relationFactory.createRelation(uuid, rt.getRelationType(), rt.getSource(), rt.getEnd());
////                relations.put(uuid, rt);
////                pending_relations.remove(uuid);
////            }
//        }

//        @Override
//        public synchronized void entityRemoved(String id) {
////            /*TODO Sync?*/
////            /*TODO MODIFY --> RELATION EN COMMUN OU PAS*/
////            if (m_relationFactory.findIdsByEndpoint(id) != null) {
////                for (UUID uuid : m_relationFactory.findIdsByEndpoint(id)) {
////                    m_relationFactory.deleteRelation(uuid);
////                    if (relations.get(uuid) != null){
////                        pending_relations.put(uuid, relations.get(uuid));
////                        relations.remove(uuid);
////                    }
////                }
////            }
//        }

        private class RelationTuple {

            private UUID m_uuid;

            private RelationType m_relationType;

            private String m_source;

            private String m_end;

            protected RelationTuple(UUID uuid, RelationType relationType, String source, String end){
                m_uuid = uuid;
                m_relationType = relationType;
                m_source = source;
                m_end = end;
            }


            public UUID getUUID(){
                return m_uuid;
            }

            public RelationType getRelationType(){
                return m_relationType;
            }

            public String getSource(){
                return m_source;
            }

            public String getEnd(){
                return m_end;
            }

            @Override
            public String toString(){
                return m_relationType.toString() + "." + m_source + "." + m_end;
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof RelationTuple) {
                    RelationTuple relationTuple = (RelationTuple) o;
                    if (m_uuid.equals(relationTuple.getUUID())) {
                        return true;
                    } else {}
                } else {}
                return false;
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
