package fr.liglab.adele.icasa.context.transformation;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


@Component
@Instantiate
@Provides(specifications = AggregationFactory.class)
public class AggregationFactoryImpl implements AggregationFactory{

    private static final Logger LOG = LoggerFactory.getLogger(AggregationFactoryImpl.class);

    private final Map<String,IpojoServiceRegistrationAggregation> aggregations = new HashMap<String,IpojoServiceRegistrationAggregation>();

    private final Object m_lockAggregation;

    @Requires(filter = "(factory.name=fr.liglab.adele.icasa.context.transformation.AggregationImpl)")
    Factory aggregationIpojoFactory;

    public AggregationFactoryImpl(){
        this.m_lockAggregation = new Object();
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){
        synchronized (m_lockAggregation){
            aggregations.clear();
        }
    }

    @Override
    public void createAggregation(String name, String filter, AggregationFunction aggregationFunction) {
        LOG.info("Create Aggregation  : " + name +" filter : " + filter);
        String aggregationId = name;
        ComponentInstance instance;

        Hashtable properties = new Hashtable();
        properties.put("aggregation.source.filter", filter);
        List<String> sourcesId = new ArrayList<String>();
        properties.put("aggregation.function", aggregationFunction);
        Hashtable requiresFilters = new Hashtable();
        requiresFilters.put("aggregation.sources", filter);
        properties.put("requires.filters", requiresFilters);
        List<List<Object>> state = new ArrayList<>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);
        List<List<Object>> stateExtensions = new ArrayList<>();
        properties.put("context.entity.state", state);
        properties.put("context.entity.state.extension", stateExtensions);
        properties.put("context.entity.id", name);
        try {
            instance = aggregationIpojoFactory.createComponentInstance(properties);
            IpojoServiceRegistrationAggregation sr = new IpojoServiceRegistrationAggregation(
                    instance,
                    name,
                    filter,
                    sourcesId);
            synchronized (m_lockAggregation) {
                aggregations.put(aggregationId, sr);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            LOG.error("Aggregation instantiation failed", unacceptableConfiguration);
        } catch (MissingHandlerException e) {
            LOG.error("Aggregation instantiation failed", e);
        } catch (ConfigurationException e) {
            LOG.error("Aggregation instantiation failed", e);
        }
    }

    @Override
    public void deleteAggregation(String name, String filter) {
        LOG.info("Delete Aggregation  : " + name + " filter : " + filter);
        try {
            synchronized (m_lockAggregation){
                aggregations.remove(name).unregister();
            }
        }catch(IllegalStateException e){
            LOG.error("failed unregistering relation", e);
        }
    }

    class IpojoServiceRegistrationAggregation implements ServiceRegistration {

        private final ComponentInstance instance;
        private final String name;
        private final String filter;
        private final List<String> sourcesId;

        public IpojoServiceRegistrationAggregation(ComponentInstance instance,String name,String filter,List<String> sourcesId) {
            super();
            this.instance = instance;
            this.name = name;
            this.filter = filter;
            this.sourcesId = sourcesId;
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

            LOG.info("Reconfigure ! ");
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

        public void updateFilterAggregation(String newFilter){
            Properties properties = new Properties();
            properties.put("aggregation.name", name);
            properties.put("aggregation.filter", newFilter);
            properties.put("aggregation.sources", sourcesId);
            setProperties(properties);
        }
    }
}
