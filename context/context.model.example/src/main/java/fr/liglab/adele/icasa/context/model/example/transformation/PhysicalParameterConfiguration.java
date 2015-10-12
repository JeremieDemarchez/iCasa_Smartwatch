package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.configuration.Configuration;
import org.apache.felix.ipojo.configuration.Instance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static org.apache.felix.ipojo.configuration.Instance.instance;

//@Configuration
public class PhysicalParameterConfiguration {

    /**    String filter_kitchen = "kitchen";

    String filter_living_room = "livingroom";

    String filter_bathroom = "bathroom";

    String filter_bedroom = "bedroom";

    String filter_dl = DimmerLight.DIMMER_LIGHT_POWER_LEVEL+"*";

    String filter_bl = BinaryLight.BINARY_LIGHT_POWER_STATUS+"*";

    String filter_presence = "presenceSensor.sensedPresence*";

    String dl_power_level = DimmerLight.DIMMER_LIGHT_POWER_LEVEL;

    String bl_power_status = BinaryLight.BINARY_LIGHT_POWER_STATUS;

    String bl_max_power = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL;

    String presence = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE;

    Instance instance1() {
        String m_filter = "(&(context.entity.state.extension=" + filter_bedroom + ")" +
                "(|(context.entity.state=" + filter_bl + ")" +
                "(context.entity.state=" + filter_dl + ")))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_bedroom + "Illuminance")
                .with("aggregation.function").setto(new IlluminanceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_bedroom)
                .with("physical.parameter.name").setto("Illuminance")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_bedroom+"Illuminance");
    }

    Instance instance2() {
        String m_filter = "(&(context.entity.state.extension=" + filter_bathroom + ")" +
                "(|(context.entity.state=" + filter_bl + ")" +
                "(context.entity.state=" + filter_dl + ")))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_bathroom + "Illuminance")
                .with("aggregation.function").setto(new IlluminanceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_bathroom)
                .with("physical.parameter.name").setto("Illuminance")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_bathroom+"Illuminance");
    }

    Instance instance3() {
        String m_filter = "(&(context.entity.state.extension=" + filter_living_room + ")" +
                "(|(context.entity.state=" + filter_bl + ")" +
                "(context.entity.state=" + filter_dl + ")))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_living_room+"Illuminance")
                .with("aggregation.function").setto(new IlluminanceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_living_room)
                .with("physical.parameter.name").setto("Illuminance")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_living_room+"Illuminance");
    }

    Instance instance4() {
        String m_filter = "(&(context.entity.state.extension=" + filter_kitchen + ")" +
                "(|(context.entity.state=" + filter_bl + ")" +
                "(context.entity.state=" + filter_dl + ")))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_kitchen + "Illuminance")
                .with("aggregation.function").setto(new IlluminanceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_kitchen)
                .with("physical.parameter.name").setto("Illuminance")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_kitchen+"Illuminance");
    }

   public class IlluminanceAggregation implements AggregationFunction{

        @Override
        public Object getResult(List sources) {
            double result = 0;

            for (Object s : sources){
                if (s instanceof ContextEntity) {
                    ContextEntity contextEntity = (ContextEntity) s;
                    if (!contextEntity.getStateValue(bl_power_status).isEmpty()) {
                        if (contextEntity.getStateValue(bl_power_status).get(1).equals(true)) {
                            result += (double) contextEntity.getStateValue(bl_max_power).get(1);
                        }

                    } else if (!contextEntity.getStateValue(dl_power_level).isEmpty()) {
                        result += (double) contextEntity.getStateValue(dl_power_level).get(1);
                    }
                }
            }
            return result;
        }
    }

    public class PresenceAggregation implements AggregationFunction{

        @Override
        public Object getResult(List sources) {

            int countOn = 0;
            for (Object s : sources){
                ContextEntity contextEntity = (ContextEntity) s;
                if (!contextEntity.getStateValue(presence).isEmpty()) {
                    if (contextEntity.getStateValue(presence).get(1).equals(true)) {
                        countOn +=1;
                        // result += (double) contextEntity.getStateValue(presence).get(1);
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

    Instance instance5() {
        String m_filter = "(&(context.entity.state.extension=" + filter_bedroom + ")" +
                "(context.entity.state=" + filter_presence + "))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_bedroom + "Presence")
                .with("aggregation.function").setto(new PresenceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_bedroom)
                .with("physical.parameter.name").setto("Presence")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_bedroom+"Presence");
    }

    Instance instance6() {
        String m_filter = "(&(context.entity.state.extension=" + filter_bathroom + ")" +
                "(context.entity.state=" + filter_presence + "))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_bathroom + "Presence")
                .with("aggregation.function").setto(new PresenceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_bathroom)
                .with("physical.parameter.name").setto("Presence")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_bathroom+"Presence");
    }

    Instance instance7() {
        String m_filter = "(&(context.entity.state.extension=" + filter_living_room + ")" +
                "(context.entity.state=" + filter_presence + "))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_living_room+"Presence")
                .with("aggregation.function").setto(new PresenceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_living_room)
                .with("physical.parameter.name").setto("Presence")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_living_room+"Presence");
    }

    Instance instance8() {
        String m_filter = "(&(context.entity.state.extension=" + filter_kitchen + ")" +
                "(context.entity.state=" + filter_presence + "))";
        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        List<List<Object>> state =  new ArrayList<List<Object>>();
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(0);
        state.add(property_array);

        return instance().of(PhysicalParameterImpl.class)
                .named(filter_kitchen + "Presence")
                .with("aggregation.function").setto(new PresenceAggregation())
                .with("aggregation.source.filter").setto(m_filter)
                .with("requires.filters").setto(m_requiresFilters)
                .with("physical.parameter.zone").setto(filter_kitchen)
                .with("physical.parameter.name").setto("Presence")
                .with("context.entity.state").setto(state)
                .with("context.entity.state.extension").setto(new ArrayList<List<Object>>())
                .with("context.entity.id").setto(filter_kitchen+"Presence");
    }**/
}
