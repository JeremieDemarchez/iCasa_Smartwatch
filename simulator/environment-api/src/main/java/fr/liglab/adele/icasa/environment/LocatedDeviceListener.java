package fr.liglab.adele.icasa.environment;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 30/11/12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public interface LocatedDeviceListener {

    // ----  LocatedDevice events ---- //

    public void deviceAdded(LocatedDevice device);

    public void deviceRemoved(LocatedDevice device);

    public void deviceMoved(LocatedDevice device);

    public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue);

    public void devicePropertyAdded(LocatedDevice device, String propertyName);

    public void devicePropertyRemoved(LocatedDevice device, String propertyName);

    public void personDeviceAttached(Person person, LocatedDevice device);

    public void personDeviceDetached(Person person, LocatedDevice device);
}
