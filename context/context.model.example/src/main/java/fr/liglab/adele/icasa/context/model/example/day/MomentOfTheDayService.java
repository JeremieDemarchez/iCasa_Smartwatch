package fr.liglab.adele.icasa.context.model.example.day;

import fr.liglab.adele.icasa.context.annotation.EntityType;

/**
 * The MomentOfTheDay service is used to retrieve the moment of the day.
 * It also supports listeners that are notified when the moment of the day
 * change.
 */
@EntityType(states = {"momentOfTheDay"})
public interface MomentOfTheDayService {

    public static final String MOMENT_OF_THE_DAY = "momentOfTheDay";
    /**
     * Gets the moment of the day.
     *
     * @return the moment of the day
     */
    MomentOfTheDay getMomentOfTheDay();

}
