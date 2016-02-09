package fr.liglab.adele.icasa.context.runtime;

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
@Provides(specifications = RelationFactory.class)
public class RelationFactoryImpl implements RelationFactory{
    /**
     * TODO : KEEP PENDING RELATIONS (due to pending entities)
     * @Require all Entity Creator Handler Interfaces
     * Creation -> check source and end entities
     * If doesn't exist and are in pending entities
     * Create pending relations
     */

    private static final Logger LOG = LoggerFactory.getLogger(RelationFactoryImpl.class);

    private final Map<UUID,IpojoServiceRegistrationRelation> relations = new HashMap<UUID,IpojoServiceRegistrationRelation>();

    private final Object m_lockRelation;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.runtime.RelationImpl)")
    Factory relationIpojoFactory;

    public RelationFactoryImpl(){
        this.m_lockRelation = new Object();
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){
        synchronized (m_lockRelation){
            relations.clear();
        }
    }

    @Override
    public UUID createRelation(UUID uuid, RelationType relationType, String source, String end) {
        return createRelation(uuid, relationType.getName(), source, end, relationType.getExtendStateName(), relationType.isAggregate(), relationType.getRelationCallBack());
    }

    @Override
    public UUID createRelation(RelationType relationType, String source, String end) {
        UUID relationId = UUID.randomUUID();
        return createRelation(relationId, relationType.getName(), source, end, relationType.getExtendStateName(), relationType.isAggregate(), relationType.getRelationCallBack());
    }

    private UUID createRelation(UUID uuid, String name, String source, String end, String extendStateName, boolean isAggregate, RelationCallBack relationCallBack){
      //  LOG.info("Create Relation  : " + name +" source : " + source + " end " + end);
        ComponentInstance instance;

        Hashtable properties = new Hashtable();
        properties.put("relation.name", name);
        properties.put("relation.source.id", source);
        properties.put("relation.end.id", end);
        properties.put("relation.value", 0);
        properties.put("relation.extendedStateName", extendStateName);
        properties.put("relation.extendedStateCallBack", relationCallBack);
        properties.put("relation.extendedStateIsAggregate", isAggregate);

        try {
            instance = relationIpojoFactory.createComponentInstance(properties);
            IpojoServiceRegistrationRelation sr = new IpojoServiceRegistrationRelation(
                    instance,
                    name,
                    source,
                    end);
            synchronized (m_lockRelation) {
                relations.put(uuid, sr);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Relation instantiation failed", unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Relation instantiation failed", e);
        } catch (ConfigurationException e) {
            LOG.error("Relation instantiation failed", e);
        }
        return uuid;
    }

    @Override
    public void deleteRelation(UUID relationId) {
        if (relationId != null) {
            try {
                synchronized (m_lockRelation){
                    relations.remove(relationId).unregister();
                }
            }catch(IllegalStateException e){
                LOG.error("failed unregistering relation", e);
            }
        }
    }

    @Override
    public void updateRelation(UUID relationId, String newSource, String newEnd) {
        if (relationId != null) {
            synchronized (m_lockRelation) {
                IpojoServiceRegistrationRelation relationToUpdate = relations.get(relationId);
                relationToUpdate.updateRelation(newSource, newEnd);
            }
        }
    }

    @Override
    public UUID findId(String name, String source, String end) {
        IpojoServiceRegistrationRelation relation;
        for(Map.Entry<UUID, IpojoServiceRegistrationRelation> entry : relations.entrySet()){
            relation = entry.getValue();
            if(relation.getName().equals(name)){
                if((relation.getSource().equals(source))&&(relation.getEnd().equals(end))){
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public Set<UUID> findIdsByEndpoint(String endpoint) {
        Set<UUID> endpointIds = new HashSet<>();
        endpointIds.addAll(findIdsBySource(endpoint));
        endpointIds.addAll(findIdsByEnd(endpoint));
        return endpointIds;
    }

    @Override
    public Set<UUID> findIdsBySource(String source) {
        Set<UUID> sourceIds = new HashSet<>();
        IpojoServiceRegistrationRelation relation;
        for(Map.Entry<UUID, IpojoServiceRegistrationRelation> entry : relations.entrySet()){
            relation = entry.getValue();
            if(relation.getSource().equals(source)){
                sourceIds.add(entry.getKey());
            }
        }
        return sourceIds;
    }

    @Override
    public Set<UUID> findIdsByEnd(String end) {
        Set<UUID> endIds = new HashSet<>();
        IpojoServiceRegistrationRelation relation;
        for(Map.Entry<UUID, IpojoServiceRegistrationRelation> entry : relations.entrySet()){
            relation = entry.getValue();
            if(relation.getEnd().equals(end)){
                endIds.add(entry.getKey());
            }
        }
        return endIds;
    }

    class IpojoServiceRegistrationRelation implements ServiceRegistration {

        private final ComponentInstance instance;
        private final String name;
        private  String source;
        private  String end;

        public IpojoServiceRegistrationRelation(ComponentInstance instance,String name,String source,String end) {
            super();
            this.instance = instance;
            this.name = name;
            this.source = source;
            this.end = end;
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
            try {
                instance.reconfigure(properties);
            }catch (Exception e ){
                LOG.error("Reconfiguration error",e);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see org.osgi.framework.ServiceRegistration#unregister()
         */
        public void unregister() {
            instance.dispose();
        }


        public void updateRelation(String newSource, String newEnd){
            Properties properties = new Properties();
            source = newSource;
            end = newEnd;
            properties.put("relation.name", name);
            properties.put("relation.source.id", source);
            properties.put("relation.end.id", end);
            setProperties(properties);
        }

        public String getName(){
            return name;
        }

        public String getSource(){
            return source;
        }

        public String getEnd(){
            return end;
        }
    }
}