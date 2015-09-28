package fr.liglab.adele.icasa.context.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.List;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface Aggregation extends ContextEntity {

    String getFilter();

    Object getResult();
}
