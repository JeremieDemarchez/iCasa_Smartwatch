package fr.liglab.adele.icasa.context.transformation;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface AggregationFactory {

    public void createAggregation(String name, String filter, AggregationFunction aggregationFunction);

    public void deleteAggregation(String name, String filter);

    public void updateFilterAggregation(String name, String oldFilter, String newFilter);
}
