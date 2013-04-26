package fr.liglab.adele.habits.monitoring.autonomic.manager;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Kettani Mehdi
 * Date: 26/04/13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class GenericDeviceListener implements DeviceListener {

    private static final Logger logger = LoggerFactory.getLogger(GenericDeviceListener.class);

    @Override
    public void deviceAdded(GenericDevice genericDevice) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deviceRemoved(GenericDevice genericDevice) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void devicePropertyModified(GenericDevice genericDevice, String s, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void devicePropertyAdded(GenericDevice genericDevice, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void devicePropertyRemoved(GenericDevice genericDevice, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
