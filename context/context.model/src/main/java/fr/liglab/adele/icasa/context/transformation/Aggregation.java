package fr.liglab.adele.icasa.context.transformation;

import java.util.List;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface Aggregation {

    String getId();

    String getName();

    String getFilter();

    List<String> getSources();

    Object getResult();
}
