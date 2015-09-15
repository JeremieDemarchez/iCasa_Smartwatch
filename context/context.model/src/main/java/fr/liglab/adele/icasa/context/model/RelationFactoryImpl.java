package fr.liglab.adele.icasa.context.model;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


@Component
@Instantiate
@Provides(specifications = RelationFactory.class)
public class RelationFactoryImpl implements RelationFactory{

    private static final Logger LOG = LoggerFactory.getLogger(RelationFactoryImpl.class);

    private final Map<String,ServiceRegistration> relations = new HashMap<String,ServiceRegistration>();

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.model.RelationImpl)")
    Factory relationIpojoFactory;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    public void createRelation(String name,String source,String end){
        ComponentInstance instance;

        Hashtable properties = new Hashtable();
        properties.put("relation.name", name);
        properties.put("relation.name", name);
        properties.put("relation.name", name);
        Hashtable filters = new Hashtable();
        filters.put("relation.source","(context.entity.id="+source+")");
        filters.put("relation.end","(context.entity.id="+end+")");
        properties.put("requires.filters",filters);

        try {
            instance = relationIpojoFactory.createComponentInstance(properties);
            ServiceRegistration sr = new IpojoServiceRegistration(
                    instance);

            relations.put(name+source+end,sr);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Relation instantiation failed",unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Relation instantiation failed",e);
        } catch (ConfigurationException e) {
            LOG.error("Relation instantiation failed",e);
        }

    }

    @Override
    public void deleteRelation(String name, String source, String end) {
        try {
            relations.remove(name+source+end).unregister();
        }catch(IllegalStateException e){
            LOG.error("failed unregistering relation", e);
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
