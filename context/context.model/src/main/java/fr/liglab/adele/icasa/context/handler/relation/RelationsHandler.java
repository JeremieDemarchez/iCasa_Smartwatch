package fr.liglab.adele.icasa.context.handler.relation;

import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.MethodMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Member;
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

    private final Map<Long,Object> m_stateExtensionByServiceId = new HashMap<>();

    @Bind(id = "context.entity.relation")
    public synchronized void bindRelations (Relation relation,ServiceReference serviceReference) {
        LOG.info(" BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        if(relation.getExtendedState().getValue() != null) {
            m_stateExtensionByServiceId.put((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());

            String relationName = relation.getExtendedState().getName();

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
        }else {
            LOG.error(" Error Bind relation " + relation.getName() + " on context entity " + relation.getEnd() + " because extended State function return a null value");
        }

    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation,ServiceReference serviceReference) {
        LOG.info(" MODIFIED relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() + " old value " + m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID)));
        String relationName = relation.getExtendedState().getName();
        if(relation.getExtendedState().getValue() != null) {
            Object oldStateExtension = m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID));
            if (oldStateExtension != null) {
                if (oldStateExtension.equals(relation.getExtendedState().getValue())) {
                    LOG.info(" Modified call but same value ");
                    return;
                }
                LOG.info(" MODIFIED PROPERTY !!!!!! ");

                if (relation.getExtendedState().isAggregate()){
                    List<Object> stateExtension = (List)m_stateExtensions.get(relationName);
                    int oldValueIndex = stateExtension.indexOf(oldStateExtension);
                    stateExtension.add(oldValueIndex,relation.getExtendedState().getName());
                    m_stateExtensions.put(relationName, stateExtension);
                    updateExtensionState(relationName, stateExtension);
                    m_stateExtensionByServiceId.replace((Long) serviceReference.getProperty(Constants.SERVICE_ID),relation.getExtendedState().getValue());
                }else {
                    m_stateExtensions.put(relationName,relation.getExtendedState().getValue());
                    updateExtensionState(relationName, relation.getExtendedState().getValue());
                    m_stateExtensionByServiceId.replace((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
                }
            }else {
                if (relation.getExtendedState().getValue() != null) {
                    m_stateExtensionByServiceId.put((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());
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
                } else {
                    LOG.error(" Error Modified relation " + relation.getName() + " on context entity " + relation.getEnd() + " because extended State function return a null value");
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
        Object oldStateExtension = m_stateExtensionByServiceId.get((Long) serviceReference.getProperty(Constants.SERVICE_ID));
        if (oldStateExtension != null) {
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


    private void addExtensionState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        m_providedServiceHandler.addProperties(hashtable);
    }

    private void removeExtensionState(String propertyId){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, new Object());
        m_providedServiceHandler.removeProperties(hashtable);
    }

    private void updateExtensionState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        m_providedServiceHandler.reconfigure(hashtable);
    }

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
        PojoMetadata pojoMetadata = getPojoMetadata();
        MethodMetadata[] methodMetadatas = pojoMetadata.getMethods();
        for (MethodMetadata method : methodMetadatas){
            if (method.getMethodName().equals("getStateExtensionAsMap")){
                getInstanceManager().register(method,this);
            }
        }

        String id =  (String)configuration.get("context.entity.id");
        Properties properties = new Properties();
        properties.put("end.id",id);
        getHandlerManager().reconfigure(properties);
    }

    public synchronized void stop() {

    }

    public synchronized void start() {
        m_providedServiceHandler = (ProvidedServiceHandler) getHandler(HandlerFactory.IPOJO_NAMESPACE + ":provides");

    }

    public synchronized void onExit(Object pojo, Member method, Object returnedObj){

        if(returnedObj instanceof Map){
            Map<String,Object> returnMap = (Map)returnedObj ;
            returnMap.clear();
            returnMap.putAll(m_stateExtensions);
        }
    }
}
