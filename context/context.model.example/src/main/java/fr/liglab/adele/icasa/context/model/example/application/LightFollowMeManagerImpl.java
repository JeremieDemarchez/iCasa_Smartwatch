package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Instantiate
public class LightFollowMeManagerImpl {

    private static final Logger LOG = LoggerFactory.getLogger(LightFollowMeManagerImpl.class);

    @Requires(specification = ContextEntity.class,id = "zone.to.regulate" , optional = true, filter = "(context.entity.state=zone.name*)")
    List<ContextEntity> zoneEntities;

    @Requires(specification = ContextEntity.class, id = "moment.of.the.day", optional = true, filter = "(context.entity.id=momentOfTheDay*)")
    ContextEntity momentOfTheDayEntity;

    @Requires(specification = LightFollowRegulator.class, id = "light.follow.me.regulators", optional = true)
    List<LightFollowRegulator> lightFollowRegulators;

    @Requires(id = "physical.factory",optional = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.transformation.PhysicalParameterImpl)")
    Factory physicalParameterFactory;

    private final Map<String,ComponentInstance> m_physicalAggregation = new HashMap<>();

    private final Object m_aggregationlock = new Object();

    @Requires(id = "light.factory",optional = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.application.LightFollowRegulatorImpl)")
    Factory lightFollowMeFactory;

    private final Map<String,ComponentInstance> m_lightFollowMe = new HashMap<>();

    private final Object m_lightLock = new Object();

    private final String m_presence = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

/**    @Bind(id = "zone.to.regulate" )
    public void bindZone(ContextEntity entity){
        createPresenceAggregation((String) entity.getStateAsMap().get("zone.name"));
        createLightFollowMeRegulator((String)entity.getStateAsMap().get("zone.name"));
    }


    @Unbind(id = "zone.to.regulate" )
    public void unbindZone(ContextEntity entity){
        deletePresenceAggregation((String) entity.getStateAsMap().get("zone.name"));
        deleteLightFollowMeRegulator((String) entity.getStateAsMap().get("zone.name"));
    }

    @Unbind(id = "physical.factory")
    public void unbindPhysicalFactory(Factory factory){
        synchronized (m_aggregationlock){
            for (String key : m_physicalAggregation.keySet()){
                try {
                    m_physicalAggregation.get(key).dispose();
                }catch(IllegalStateException e){
                    LOG.error("failed unregistering Physical Parameter" + key, e);
                }
            }
        }
    }

    @Unbind(id = "light.factory")
    public void unbindLightFactory(Factory factory){
        synchronized (m_lightLock){
            for (String key : m_lightFollowMe.keySet()){
                try {
                    m_lightFollowMe.get(key).dispose();
                }catch(IllegalStateException e){
                    LOG.error("failed unregistering Light Follow Me Regultor " + key, e);
                }
            }
        }
    }

    private void deletePresenceAggregation(String zone){
        try {
            synchronized (m_aggregationlock) {
                m_physicalAggregation.get(zone + "Presence").dispose();
            }
        }catch(IllegalStateException e){
            LOG.error("failed unregistering Presence Physical Parameter in " + zone, e);
        }
    }

    private void deleteLightFollowMeRegulator(String zone){
        try {
            synchronized (m_lightLock) {
                m_lightFollowMe.get(zone + "Regulator").dispose();
            }
        }catch(IllegalStateException e){
            LOG.error("failed unregistering Light Follow Me Regultor in"+zone, e);
        }
    }
    private void createPresenceAggregation(String zone){
        Hashtable properties = new Hashtable();
        ComponentInstance instance;

        String m_filter = "(&(Relation.Location=" + zone + ")" +
                "(context.entity.state=presenceSensor.sensedPresence*))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(false);
        state.add(property_array);

        properties.put("requires.filters", m_requiresFilters);
        properties.put("instance.name", "fr.liglab.adele.icasa.context.model.example.transformation."+zone+"Presence");
        properties.put("aggregation.function", new PresenceAggregation());
        properties.put("aggregation.source.filter", m_filter);
        properties.put("physical.parameter.zone", zone);
        properties.put("physical.parameter.name","Presence");
        properties.put("context.entity.state",state);
        properties.put("context.entity.state.extension",new ArrayList<List<Object>>());
        properties.put("context.entity.id",zone + "Presence");

        try {
            instance = physicalParameterFactory.createComponentInstance(properties);
            synchronized (m_aggregationlock){
                m_physicalAggregation.put(zone + "Presence",instance);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

    private void createLightFollowMeRegulator(String zone){
        Hashtable properties = new Hashtable();
        ComponentInstance instance;

        String m_filterPresence = "(&(physical.parameter.name=Presence)(zone.impacted="+zone+"))";
        String m_filterLight = "(&(Relation.Location="+zone+")" +
                "(|(context.entity.state="+ BinaryLight.BINARY_LIGHT_POWER_STATUS + "*)" +
                "(context.entity.state=" + DimmerLight.DIMMER_LIGHT_POWER_LEVEL + "*)))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("presence",m_filterPresence);
        m_requiresFilters.put("lights",m_filterLight);

        properties.put("requires.filters", m_requiresFilters);
        try {
            instance = lightFollowMeFactory.createComponentInstance(properties);
            synchronized (m_lightLock){
                m_lightFollowMe.put(zone + "Regulator",instance);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

    public class PresenceAggregation implements AggregationFunction {

        @Override
        public Object getResult(List sources) {

            int countOn = 0;
            for (Object s : sources){
                ContextEntity contextEntity = (ContextEntity) s;
                if (!contextEntity.getStateValue(m_presence).isEmpty()) {
                    if (contextEntity.getStateValue(m_presence).get(1).equals(true)) {
                        countOn +=1;
                    }else {
                        countOn -=1;
                    }
                }
            }
            if (countOn > 0){
                return true;
            }
            else {
                return false;
            }

        }
    }

    @Bind(id = "moment.of.the.day")
    public void bindMomentOfTheDay(ContextEntity momentOfTheDay){
        for(LightFollowRegulator lightFollowRegulator : lightFollowRegulators){
            updateLightFollowRegulator(lightFollowRegulator, momentOfTheDay);
        }
    }

    @Modified(id = "moment.of.the.day")
    public void modifiedMomentOfTheDay(ContextEntity momentOfTheDay){
        for(LightFollowRegulator lightFollowRegulator : lightFollowRegulators){
            updateLightFollowRegulator(lightFollowRegulator, momentOfTheDay);
        }
    }

    @Unbind(id = "moment.of.the.day")
    public void unbindMomentOfTheDay(ContextEntity momentOfTheDay){

    }

    @Bind(id = "light.follow.me.regulators")
    public void bindLightFollowMeRegulators(LightFollowRegulator lightFollowMeRegulator){
        if (momentOfTheDayEntity != null) {
            updateLightFollowRegulator(lightFollowMeRegulator, momentOfTheDayEntity);
        }
    }

    @Unbind(id = "light.follow.me.regulators")
    public void unbindLightFollowMeRegulators(LightFollowRegulator lightFollowMeRegulator){

    }

    private void updateLightFollowRegulator (LightFollowRegulator lightFollowRegulator, ContextEntity momentOfTheDayEntity){
        if (!momentOfTheDayEntity.getStateValue("currentMomentOfTheDay").isEmpty()) {
            MomentOfTheDay momentOfTheDay = (MomentOfTheDay) momentOfTheDayEntity.getStateValue("currentMomentOfTheDay").get(1);
            lightFollowRegulator.setIlluminanceFactor(convertMomentOfTheDay(momentOfTheDay));
        }

    }

    private double convertMomentOfTheDay(MomentOfTheDay momentOfTheDay){
        double illuminanceFactor = 1;
        switch (momentOfTheDay){
            case MORNING:
                illuminanceFactor = 0.5;
                break;
            case AFTERNOON:
                illuminanceFactor = 0.2;
                break;
            case EVENING:
                illuminanceFactor = 1;
                break;
            case NIGHT:
                illuminanceFactor = 0.8;
                break;
        }

        return illuminanceFactor;
    }**/
}
