package fr.liglab.adele.icasa.electicity.viewer;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Created by aygalinc on 27/03/14.
 */
public interface ElectricityViewer {

    public double getTotalConsumption();

    public double getZoneConsumption(String zoneId);

    public <T extends GenericDevice> double getGroupOfDeviceConsumption(Class<T> clazz);

    public void addListener(ElectricityViewerListener listener);

    public void removeListener(ElectricityViewerListener listener);
}
