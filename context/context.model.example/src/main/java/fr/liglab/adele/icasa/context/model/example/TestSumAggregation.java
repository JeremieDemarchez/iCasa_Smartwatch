package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.transformation.AggregationFactory;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import org.apache.felix.ipojo.annotations.*;

import java.util.List;

/**
 * Created by Gerbert on 21/09/2015.
 */
/**@Component
@Instantiate**/
public class TestSumAggregation {
    @Requires (id = "test.aggregation.factory", optional = false)
    AggregationFactory aggregationFactory;

    String filter_kitchen = "kitchen";

    String filter_living_room = "livingroom";

    String filter_bathroom = "bathroom";

    String filter_bedroom = "bedroom";

    String filter_dl = "dimmerLight*";

    String filter_bl = "binaryLight*";

    String dl_power_level = DimmerLight.DIMMER_LIGHT_POWER_LEVEL;

    String bl_power_status = BinaryLight.BINARY_LIGHT_POWER_STATUS;

    String bl_max_power = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL;

    public TestSumAggregation(){

    }

    @Validate
    public void start(){
        aggregationFactory.createAggregation("SumLightKitchen",
                "(&(context.entity.state.extension=" + filter_kitchen + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                          "(context.entity.state=" + filter_dl + ")))",
                sources -> {
                    return this.sumAggregationFunction(sources);
                });

        aggregationFactory.createAggregation("SumLightLivingroom",
                "(&(context.entity.state.extension=" + filter_living_room + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                        "(context.entity.state=" + filter_dl + ")))",
                sources -> {
                    return this.sumAggregationFunction(sources);
                });

        aggregationFactory.createAggregation("SumLightBathroom",
                "(&(context.entity.state.extension=" + filter_bathroom + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                        "(context.entity.state=" + filter_dl + ")))",
                sources -> {
                    return this.sumAggregationFunction(sources);
                });

        aggregationFactory.createAggregation("SumLightBedroom",
                "(&(context.entity.state.extension=" + filter_bedroom + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                        "(context.entity.state=" + filter_dl + ")))",
                sources -> {
                    return this.sumAggregationFunction(sources);
                });

    }

    @Invalidate
    public void stop(){

    }

    public Object sumAggregationFunction (List sources) {
        double result = 0;
        int n = 0;

        for (Object s : sources){
            if (s instanceof ContextEntity) {
                ContextEntity contextEntity = (ContextEntity) s;
                if (!contextEntity.getStateValue(bl_power_status).isEmpty()) {
                    n += 1;
                    if (contextEntity.getStateValue(bl_power_status).get(1).equals(true)) {
                        result += (double) contextEntity.getStateValue(bl_max_power).get(1);
                    }

                } else if (!contextEntity.getStateValue(dl_power_level).isEmpty()) {
                    n += 1;
                    result += (double) contextEntity.getStateValue(dl_power_level).get(1);
                }
            }
        }

        if (n<=0) {
            result = -1;
        }
        return result;
    }
}
