package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.AggregationFactory;
import fr.liglab.adele.icasa.context.model.ContextEntity;

import org.apache.felix.ipojo.annotations.*;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;

import java.util.List;

/**
 * Created by Gerbert on 21/09/2015.
 */
@Component
@Instantiate
public class TestMeanAggregation {
    @Requires (id = "test.aggregation.factory", optional = false)
    AggregationFactory aggregationFactory;

    String filter_room = "kitchen";

    String filter_dl = "dimmerLight*";

    String filter_bl = "binaryLight*";

    String dl_power_level = DimmerLight.DIMMER_LIGHT_POWER_LEVEL;

    String bl_power_status = BinaryLight.BINARY_LIGHT_POWER_STATUS;

    String bl_max_power = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL;

    public TestMeanAggregation(){

    }

    @Validate
    public void start(){
        aggregationFactory.createAggregation("MeanBinaryLightKitchen",
                "(&(context.entity.state.extension=" + filter_room + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                          "(context.entity.state=" + filter_dl + ")))",
                sources -> {
                    return this.meanAggregationFunction(sources);
                });

    }

    @Invalidate
    public void stop(){

    }

    public Object meanAggregationFunction (List sources) {
        double result = 0;
        int n = 0;

        for (Object s : sources){
            if (s instanceof ContextEntity) {
                ContextEntity contextEntity = (ContextEntity) s;
                if (!contextEntity.getStateValue(bl_power_status).isEmpty()) {
                    n += 1;
                    if (contextEntity.getStateValue(bl_power_status).get(1).equals(true)) {
                        result += Math.pow((double) contextEntity.getStateValue(bl_max_power).get(1), 2);
                    }

                } else if (!contextEntity.getStateValue(dl_power_level).isEmpty()) {
                    n += 1;
                    result += Math.pow((double) contextEntity.getStateValue(dl_power_level).get(1), 2);
                }
            }
        }

        if (n>0) {
            result = Math.sqrt(result / n);
        } else {
            result = -1;
        }
        return result;
    }
}
