package fr.liglab.adele.icasa.electricity.manager.sample;

import org.joda.time.DateTime;


public interface ConsumptionSample {

    public double getConsumption();

    public DateTime getDate();

    public long getId();

}
