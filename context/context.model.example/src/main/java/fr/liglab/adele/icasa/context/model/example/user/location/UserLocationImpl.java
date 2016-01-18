package fr.liglab.adele.icasa.context.model.example.user.location;

import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreator;
import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreatorInterface;
import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.RelationImpl;
import fr.liglab.adele.icasa.context.model.RelationType;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.util.*;
import java.util.function.Function;


@Component(immediate = true,propagation = false)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {UserLocationImpl.PARAMETER_NAME, UserLocationImpl.AGGREGATION_VALUE})
public class UserLocationImpl implements Aggregation {

    public final static String PARAMETER_NAME = "user.location";

    public final static String AGGREGATION_VALUE = "aggregation.value";

    @RelationCreator(relation = RelationImpl.class)
    private RelationCreatorInterface m_relationCreator;

    @Requires(id = "aggregation.sources", optional = true)
    List<Aggregation> sources;

    @Requires(id = "user.source", optional = false, filter = "(context.entity.id=${user.id})")
    ContextEntity user;

    /** PROPERTIES **/
    @ServiceProperty(name = "aggregation.source.filter", mandatory = true)
    String filter;

    @ServiceProperty(name = "user.id",mandatory = true)
    String userId;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    /** ATTRIBUTES **/
    private Set<String> zoneId;
    private final AggregationFunction m_aggregationFunction;

    private final RelationType relation_computeWith = new Relation_ComputeWithPP();
    private final RelationType relation_location = new Relation_UserLocation();

    private static final Logger LOG = LoggerFactory.getLogger(UserLocationImpl.class);

    public UserLocationImpl() {
        m_aggregationFunction = new UserLocation();
        zoneId = new HashSet<String>();
    }

    @Pull(state = UserLocationImpl.PARAMETER_NAME )
    Function getName = (Object obj) ->{
        return getParameterName();
    };

    @Pull(state = UserLocationImpl.AGGREGATION_VALUE )
    Function getAggregationValue = (Object obj) ->{
        return getResult();
    };

    private String getParameterName(){
        return PARAMETER_NAME;
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){
//        Set<UUID> uuid_list = m_relationCreator.findIdsByEndpoint(name);
//        for(UUID uuid: uuid_list){
//            m_relationCreator.deleteRelation(uuid);
//        }
    }

    @Bind(id = "aggregation.sources", aggregate = true)
    public void bindPA (Aggregation aggregation) {
        m_relationCreator.createRelation(relation_computeWith, aggregation.getId(), this.getId());
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Modified(id = "aggregation.sources")
    public void modifiedPA (Aggregation aggregation) {
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Unbind(id = "aggregation.sources")
    public void unbindPA (Aggregation aggregation) {
//        UUID uuid = m_relationCreator.findId(relation_computeWith.getName(), aggregation.getId(), this.getId());
//        if (uuid != null) {
//            m_relationCreator.deleteRelation(uuid);
//        }
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Bind(id = "user.source", aggregate = false)
    public void bindUserContextEntity (ContextEntity contextEntity) {
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Unbind(id = "user.source")
    public void unbindUserContextEntity (ContextEntity contextEntity) {
        /*A modifier*/
//        Set<UUID> uuid_list = m_relationCreator.findIdsByEndpoint(contextEntity.getId());
//        for(UUID uuid: uuid_list){
//            m_relationCreator.deleteRelation(uuid);
//        }
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public synchronized Object getResult() {
        return m_aggregationFunction.getResult(sources);
    }




    public class UserLocation implements AggregationFunction {

        @Override
        public Object getResult(List sources) {
            Set<String> locations = new HashSet<>();
            String zone = "";
            String uID = user.getId();
            Boolean test;
            if ((uID != null)&&(!sources.isEmpty())){
                UUID uuid;
                for (Object s: sources){
                    Aggregation aggregation = (Aggregation) s;
                    test = (Boolean)aggregation.getResult();
                    zone = (String)aggregation.getStateExtensionValue("zone.impacted");
                    if ((zone!= null)&&(test!=null)){
                        if (test.equals(true)) {
                            locations.add(zone);
                            uuid = m_relationCreator.findId(relation_location.getName(), zone, userId);
                            if (uuid == null) {
                                m_relationCreator.createRelation(relation_location, zone, userId);
                            }
                        } else {
                            locations.remove(zone);
                            uuid = m_relationCreator.findId(relation_location.getName(), zone, userId);
                            if (uuid != null) {
                                m_relationCreator.deleteRelation(uuid);
                            }
                        }

                    }
                }
            }
            zoneId = locations;
            return zoneId;
        }
    }




    private final Map<String,Object> injectedState = new HashMap<>();

    private final Map<String,Object> injectedExtensionState =new HashMap<>();

    @Override
    public String getId() {
        return name;
    }

    @Override
    public Object getStateValue(String property) {
        return injectedState.get(property);
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getState() {
        return Collections.unmodifiableMap(injectedState);
    }

    @Override
    public Object getStateExtensionValue(String property) {
        return injectedExtensionState.get(property);
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return Collections.unmodifiableMap(injectedExtensionState);
    }

    @Override
    public void pushState(String state, Object value) {

    }
}
