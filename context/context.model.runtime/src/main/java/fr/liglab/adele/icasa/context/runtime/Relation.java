package fr.liglab.adele.icasa.context.runtime;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface Relation {

    String getId();

    String getName();

    String getSource();

    String getEnd();

    ExtendedState getExtendedState();
}
