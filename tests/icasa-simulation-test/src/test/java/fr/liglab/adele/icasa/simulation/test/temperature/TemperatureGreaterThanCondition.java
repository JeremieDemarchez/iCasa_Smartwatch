package fr.liglab.adele.icasa.simulation.test.temperature;

import fr.liglab.adele.icasa.location.Zone;

/**
 * Condition is true only if specified zone has specified temperature value.
 *
 * @author Thomas Leveque
 */
public class TemperatureGreaterThanCondition extends TemperatureVarExistsCondition {

    private double _temp;

    public TemperatureGreaterThanCondition(Zone zone, double temp) {
        super(zone);
        _temp = temp;
    }

    public boolean isChecked() {
        if (!super.isChecked())
            return false;

        Object tempObj = _zone.getVariableValue("Temperature");
        if ((tempObj == null) || (tempObj instanceof Double))
            return false;
        return ((Double) tempObj) > _temp;
    }

    public String getDescription() {
        return "Temperature variable must be greater than " + _temp + " on zone " + _zone.getId() + ".";
    }
}
