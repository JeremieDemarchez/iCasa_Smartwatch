package fr.liglab.adele.icasa.context.model;

/**
 * Created by aygalinc on 18/09/15.
 */
public interface ExtendedState {

    boolean isAggregate();

    String getName();

    Object getValue();
}
