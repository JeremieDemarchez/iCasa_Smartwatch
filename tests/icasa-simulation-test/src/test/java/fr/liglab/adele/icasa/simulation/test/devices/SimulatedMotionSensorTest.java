/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulation.test.devices;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import static org.mockito.Mockito.*;

import javax.inject.Inject;
import java.util.Hashtable;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SimulatedMotionSensorTest extends AbstractDistributionBaseTest {

    @Inject
    BundleContext context;

    @Inject
    private SimulationManager simulationMgr;

    @Before
    public void setUp() {
        waitForStability(context);
    }

    @After
    public void tearDown() {
        try {
            simulationMgr.resetContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validMotionSensorTest(){
        LocatedDevice device = simulationMgr.createDevice("iCasa.MotionSensor", "device1", new Hashtable());
        Assert.assertNotNull(device);
    }
    @Test
    public void motionSensorTest(){
        //Create new device
        LocatedDevice device = simulationMgr.createDevice("iCasa.MotionSensor", "device1", new Hashtable());
        //Mock listener.
        DeviceListener listener = mock(DeviceListener.class);
        GenericDevice realDevice = (GenericDevice)device.getDeviceObject();
        //Add Mock listener
        realDevice.addListener(listener);
        //Create zone and person.
        simulationMgr.createZone("zone1",50, 50, 0, 100, 100, 100);
        simulationMgr.addPerson("Toto", "Boy");
        //put device and person in the zone (this will trigger a deviceEvent)
        simulationMgr.moveDeviceIntoZone("device1", "zone1");
        simulationMgr.setPersonZone("Toto", "zone1");
        //Move the person, but in the same zone. This trigger the second deviceEvent.
        simulationMgr.setPersonPosition("Toto", new Position(70,70));
        //Two events, when person enter in zone, and when person move in the same zone.
        verify(listener, atLeast(2)).deviceEvent(realDevice, Boolean.TRUE);

    }
}
