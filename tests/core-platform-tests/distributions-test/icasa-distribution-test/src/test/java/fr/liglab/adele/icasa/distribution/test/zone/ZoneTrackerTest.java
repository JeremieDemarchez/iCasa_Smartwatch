package fr.liglab.adele.icasa.distribution.test.zone;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTracker;
import fr.liglab.adele.icasa.distribution.test.device.DeviceTrackedNumberCondition;
import fr.liglab.adele.icasa.distribution.test.device.Type1Device;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.util.ZoneTracker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: garciai@imag.fr
 * Date: 8/2/13
 * Time: 3:14 PM
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ZoneTrackerTest extends AbstractDistributionBaseTest {

    @Inject
    public BundleContext context;

    @Before
    public void setUp() {
        waitForStability(context);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testZoneTracker() {
        ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
        Assert.assertNotNull(contextMgr);

        ZoneTracker tracker = new ZoneTracker(context, null);
        tracker.open();
        Assert.assertEquals(0, tracker.size());

        contextMgr.createZone("toto",10,10,10,10,10,10);
        Assert.assertEquals(tracker.size(),1);//One tracked zone.
        Assert.assertEquals(tracker.size(),contextMgr.getZones().size());//
    }

    @Test
    public void testZoneTrackerRemoveZone() {
        ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
        Assert.assertNotNull(contextMgr);

        ZoneTracker tracker = new ZoneTracker(context, null);
        tracker.open();
        Assert.assertEquals(0, tracker.size());

        contextMgr.createZone("toto",10,10,10,10,10,10);
        Assert.assertEquals(tracker.size(),1);//One tracked zone.
        Assert.assertEquals(tracker.size(),contextMgr.getZones().size());//
        contextMgr.removeAllZones();
        Assert.assertEquals(tracker.size(),0);//One tracked zone.
        Assert.assertEquals(tracker.size(),contextMgr.getZones().size());//
    }

    @Test
    public void testZoneTrackerWithVariable() {
        ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
        Assert.assertNotNull(contextMgr);
        String zoneId = "toto";
        String variableName = "needed-variable";

        ZoneTracker tracker = new ZoneTracker(context, null, variableName);
        tracker.open();
        Assert.assertEquals(0, tracker.size());

        //create zone.
        contextMgr.createZone(zoneId,10,10,10,10,10,10);
        //check size in context and in tracker.
        Assert.assertEquals(contextMgr.getZones().size(), 1);//One zone in context.
        Assert.assertEquals(tracker.size(),0);//zone does not have needed variable.
        //add a variable
        contextMgr.addZoneVariable(zoneId, variableName);
        //now the tracker must have the zone
        Assert.assertEquals(tracker.size(),1);//One tracked zone with the variable
    }

    @Test
    public void testZoneTrackerRemovingVariable() {
        ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
        Assert.assertNotNull(contextMgr);
        String zoneId = "toto";
        String variableName = "needed-variable";

        ZoneTracker tracker = new ZoneTracker(context, null, variableName);
        tracker.open();
        Assert.assertEquals(0, tracker.size());

        //create zone.
        Zone zone = contextMgr.createZone(zoneId, 10, 10, 10, 10, 10, 10);
        //check size in context and in tracker.
        Assert.assertEquals(contextMgr.getZones().size(), 1);//One zone in context.
        Assert.assertEquals(tracker.size(),0);//zone does not have needed variable.
        //add a variable
        contextMgr.addZoneVariable(zoneId, variableName);
        //now the tracker must have the zone
        Assert.assertEquals(tracker.size(),1);//One tracked zone with the variable

        zone.removeVariable(variableName);
        //now the tracker must have 0 zones
        Assert.assertEquals(tracker.size(),0);//0 tracked zones 'cause the needed variable has been removed.
    }

}