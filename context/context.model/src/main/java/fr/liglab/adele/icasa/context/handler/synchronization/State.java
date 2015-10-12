package fr.liglab.adele.icasa.context.handler.synchronization;

/**
 * Created by aygalinc on 12/10/15.
 */
public @interface State {

    String[] states() default {};

}
