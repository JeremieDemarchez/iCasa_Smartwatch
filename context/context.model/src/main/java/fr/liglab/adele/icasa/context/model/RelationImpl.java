package fr.liglab.adele.icasa.context.model;

import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
@Provides
public class RelationImpl implements Relation {

    private static final Logger LOG = LoggerFactory.getLogger(RelationImpl.class);

    @Requires(id = "relation.source",optional = false, filter="(context.entity.id=${relation.source.id})")
    ContextEntity source;

    @Requires(id = "relation.end",optional = false, filter="(context.entity.id=${relation.end.id})")
    ContextEntity end;

    @ServiceProperty(name = "relation.source.id",mandatory = true)
    public String sourceId;

    @ServiceProperty(name = "relation.end.id",mandatory = true)
    public String endId;

    @ServiceProperty( name = "relation.name",mandatory = true)
    String name;

    @ServiceProperty( name = "relation.value",mandatory = true)
    Object value;

    private final ExtendedState m_extendedState ;

    public RelationImpl(@Property(name = "relation.extendedStateName",mandatory = true,immutable = true) String stateName,
                        @Property(name = "relation.extendedStateCallBack",mandatory = true,immutable = true) RelationCallBack stateCallBack,
                        @Property(name = "relation.extendedStateIsAggregate",mandatory = true,immutable = true) boolean stateIsAggregate){
        m_extendedState = new ExtendStateImmutableImpl(stateName,stateCallBack,stateIsAggregate);
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Modified(id = "relation.source")
    public void modifiedSource(){
      LOG.info("RELATION" + getId() + " Source is Modified");
        LOG.info("Actual Value " + value + " new value " + m_extendedState.getValue()+ " equals ? "+value.equals(m_extendedState.getValue()));
        if (value.equals(m_extendedState.getValue())){
            return;
        }
        value = m_extendedState.getValue();
    }

    @Override
    public String getId() {
        return getName()+getSource()+getEnd();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSource() {
        return sourceId;
        //NE FONCTIONNE PAS AVEC LES FILTRES DANS LES CONTEXT ENTITY
        //LE SYSTEME PLANTE QUAND L AFFICHAGE WEB DU CONTEXT EST ACTUALISE
        //return source.getId();
    }

    @Override
    public String getEnd() {
        //return endId;
        //NE FONCTIONNE PAS AVEC LES FILTRES DANS LES CONTEXT ENTITY
        //LE SYSTEME PLANTE QUAND L AFFICHAGE WEB DU CONTEXT EST ACTUALISE
        return end.getId();
    }

    private Map getSourceState(){
        return source.getStateAsMap();
    }

    @Override
    public ExtendedState getExtendedState() {
        return m_extendedState;
    }

    public class ExtendStateImmutableImpl implements ExtendedState {

        private final boolean m_isAggregate;

        private final String m_name;

        private final RelationCallBack m_callBack;

        private ExtendStateImmutableImpl(String name,RelationCallBack callBack,boolean isAggregate ){
            this.m_isAggregate = isAggregate;
            this.m_name = name;
            m_callBack = callBack;
        }

        @Override
        public boolean isAggregate() {
            return m_isAggregate;
        }

        @Override
        public String getName() {
            return m_name;
        }

        @Override
        public Object getValue() {
            return  m_callBack.callBack(getSourceState());
        }
    }



}
