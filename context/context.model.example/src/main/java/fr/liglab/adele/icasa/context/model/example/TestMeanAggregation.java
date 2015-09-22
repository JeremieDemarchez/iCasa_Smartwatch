package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.AggregationFactory;
import fr.liglab.adele.icasa.context.model.ContextEntity;

import org.apache.felix.ipojo.annotations.*;

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

    String filter_dl = "dimmerLight.powerLevel";

    String filter_bl = "binaryLight.powerStatus";

    String bl_max_power = "binaryLight.maxPowerLevel";

    public TestMeanAggregation(){

    }

    @Validate
    public void start(){
//        aggregationFactory.createAggregation("MeanBinaryLightKitchen",
//                "(&(context.entity.state=" + filter_room + ")" +
//                        "(|(context.entity.state=" + filter_bl + ")" +
//                        "(context.entity.state=" + filter_dl + ")))",
//                sources -> {
//                    return this.meanAggregationFunction(sources);
//                });
        aggregationFactory.createAggregation("MeanBinaryLightKitchen",
                filter_room + ")" +
                        "(|(context.entity.state=" + filter_bl + ")" +
                        "(context.entity.state=" + filter_dl + ")",
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
                if (contextEntity.getStateValue(filter_bl) != null) {
                    n += 1;
                    if (contextEntity.getStateValue(filter_bl).get(0).equals(true)) {
                        result += Math.pow((double) contextEntity.getStateValue(bl_max_power).get(1), 2);
                    }

                } else if (contextEntity.getStateValue(filter_dl) != null) {
                    n += 1;
                    result += Math.pow((double) contextEntity.getStateValue(filter_dl).get(1), 2);
                }
            }
        }

        result = Math.sqrt(result / n);
        return result;
    }
}
