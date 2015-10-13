package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.example.day.MomentContextEntityImpl;
import fr.liglab.adele.icasa.context.model.example.day.MomentOfTheDay;
import fr.liglab.adele.icasa.context.model.example.device.PresenceContextEntityImpl;
import fr.liglab.adele.icasa.context.model.example.transformation.PhysicalParameterImpl;
import fr.liglab.adele.icasa.context.model.example.zone.ZoneContextEntityImpl;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
@Instantiate
public class LightFollowMeManagerImpl {

    private static final Logger LOG = LoggerFactory.getLogger(LightFollowMeManagerImpl.class);

    @Requires(specification = ContextEntity.class,id = "zone.to.regulate" , optional = true, filter = "("+ ZoneContextEntityImpl.ZONE_NAME+"=*)")
    List<ContextEntity> zoneEntities;

    @Requires(specification = ContextEntity.class, id = "moment.of.the.day", optional = true, filter = "("+MomentContextEntityImpl.MOMENT_OF_THE_DAY+"=*)")
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

    @Bind(id = "zone.to.regulate" )
    public void bindZone(ContextEntity entity){
       createPresenceAggregation((String) entity.getStateValue(ZoneContextEntityImpl.ZONE_NAME));
        createLightFollowMeRegulator((String) entity.getStateValue(ZoneContextEntityImpl.ZONE_NAME));
    }


    @Unbind(id = "zone.to.regulate" )
    public void unbindZone(ContextEntity entity){
        deletePresenceAggregation((String) entity.getStateValue(ZoneContextEntityImpl.ZONE_NAME));
       deleteLightFollowMeRegulator((String) entity.getStateValue(ZoneContextEntityImpl.ZONE_NAME));
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

        String m_filter = "(&(location=" + zone + ")" +
                "("+ PresenceContextEntityImpl.DEVICE_TYPE+"=PresenceSensor))";

        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        properties.put("requires.filters", m_requiresFilters);
        properties.put("instance.name", "fr.liglab.adele.icasa.context.model.example.transformation."+zone+"Presence");
        properties.put("aggregation.function", new PresenceAggregation());
        properties.put("aggregation.source.filter", m_filter);
        properties.put("physical.parameter.zone", zone);
        properties.put(PhysicalParameterImpl.PHYSICAL_PARAMETER_NAME+".init","Presence");
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

        String m_filterPresence = "(&("+PhysicalParameterImpl.PHYSICAL_PARAMETER_NAME+"=Presence)(zone.impacted="+zone+"))";
        String m_filterLight = "(&(Relation.Location="+zone+")" +
                "(|(("+ BinaryLight.BINARY_LIGHT_POWER_STATUS+"={true,false})" +
                "("+DimmerLight.DIMMER_LIGHT_POWER_LEVEL + "=*)))";
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
                if (contextEntity.getStateValue(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE).equals(true)) {
                    countOn +=1;
                }else {
                    countOn -= 1;
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
        MomentOfTheDay momentOfTheDay = (MomentOfTheDay) momentOfTheDayEntity.getStateValue(MomentContextEntityImpl.MOMENT_OF_THE_DAY);
        lightFollowRegulator.setIlluminanceFactor(convertMomentOfTheDay(momentOfTheDay));
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
    }
}
