package fr.liglab.adele.icasa.context.handler.synchronization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.FIELD)
public @interface Pull {

    String state();

    /**
     * Sets the period of time.
     */
    long period() default -1L;

    /**
     * Sets the time unit to use for the period.
     */
    TimeUnit unit() default TimeUnit.SECONDS;

}
