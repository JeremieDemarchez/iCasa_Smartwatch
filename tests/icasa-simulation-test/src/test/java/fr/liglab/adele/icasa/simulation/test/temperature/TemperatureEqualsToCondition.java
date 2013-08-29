package fr.liglab.adele.icasa.simulation.test.temperature;

import fr.liglab.adele.icasa.location.Zone;

/**
 * Condition is true only if specified zone has specified temperature value.
 *
 * @author Thomas Leveque
 */
public class TemperatureEqualsToCondition extends TemperatureVarExistsCondition {

    private double _temp;

    public TemperatureEqualsToCondition(Zone zone, double temp) {
        super(zone);
        _temp = temp;
    }

    public boolean isChecked() {
        return super.isChecked() && new Double(_temp).equals(_zone.getVariableValue("Temperature"));
    }

    public String getDescription() {
        return "Temperature variable must Equals to " + _temp + " on zone " + _zone.getId() + ".";
    }
}
