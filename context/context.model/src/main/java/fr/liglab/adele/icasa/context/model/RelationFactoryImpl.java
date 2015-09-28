package fr.liglab.adele.icasa.context.model;

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

    private static final Logger LOG = LoggerFactory.getLogger(RelationFactoryImpl.class);

    private final Map<String,IpojoServiceRegistrationRelation> relations = new HashMap<String,IpojoServiceRegistrationRelation>();

    private final Object m_lockRelation;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.RelationImpl)")
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

    public void createRelation(String name,String source,String end,String extendStateName,boolean isAggregate,RelationCallBack relationCallBack){
        LOG.info("Create Relation  : " + name +" source : " + source + " end " + end);
        String relationId = name + source + end;
        ComponentInstance instance;

        Hashtable properties = new Hashtable();
        properties.put("relation.name", name);
        properties.put("relation.source.id", source);
        properties.put("relation.end.id", end);
        properties.put("relation.value", 0);
        properties.put("relation.extendedStateName", extendStateName);
        properties.put("relation.extendedStateCallBack", relationCallBack);
        properties.put("relation.extendedStateIsAggregate", isAggregate);
     /*   Hashtable filters = new Hashtable();
        filters.put("relation.source", "(context.entity.id=" + source + ")");
        filters.put("relation.end", "(context.entity.id=" + end + ")");
        properties.put("requires.filters", filters);*/

        try {
            instance = relationIpojoFactory.createComponentInstance(properties);
            IpojoServiceRegistrationRelation sr = new IpojoServiceRegistrationRelation(
                    instance,
                    name,
                    source,
                    end);
            synchronized (m_lockRelation) {
                relations.put(relationId, sr);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Relation instantiation failed", unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Relation instantiation failed", e);
        } catch (ConfigurationException e) {
            LOG.error("Relation instantiation failed", e);
        }

    }

    @Override
    public void deleteRelation(String name, String source, String end) {
        LOG.info("Delete Relation  : " + name + " source : " + " end " + end);
        try {
            synchronized (m_lockRelation){
                relations.remove(name+source+end).unregister();
            }
        }catch(IllegalStateException e){
            LOG.error("failed unregistering relation", e);
        }
    }

    @Override
    public void updateRelation(String name, String oldSource, String oldEnd,String newSource,String newEnd){
        LOG.info("Update relation " + name + " oldSource : " + oldSource + " new Source : " + newSource + " oldEnd : " + oldEnd + " newEnd : " + newEnd);
        synchronized (m_lockRelation) {
            String oldRelationId = name + oldSource + oldEnd;
            IpojoServiceRegistrationRelation relationToUpdate = relations.remove(oldRelationId);
            relationToUpdate.updateRelation(newSource, newEnd);

            String newRelationId = name + newSource + newEnd;
            relations.put(newRelationId, relationToUpdate);
        }


    }

    class IpojoServiceRegistrationRelation implements ServiceRegistration {

        private final ComponentInstance instance;
        private final String name;
        private final String source;
        private final String end;

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


        public void updateRelation(String newSource,String newEnd){
            Properties properties = new Properties();
            properties.put("relation.name", name);
            properties.put("relation.source.id", newSource);
            properties.put("relation.end.id", newEnd);
            setProperties(properties);
        }
    }
}
