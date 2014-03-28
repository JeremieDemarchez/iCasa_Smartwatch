package fr.liglab.adele.icasa.electicity.viewer;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Created by aygalinc on 27/03/14.
 */
public interface ElectricityViewerListener {

    public void deviceConsumptionModified(GenericDevice device,double newConsumption, double oldConsumption);

    public void zoneConsumptionModified(String zoneId,double newConsumption, double oldConsumption);
}
