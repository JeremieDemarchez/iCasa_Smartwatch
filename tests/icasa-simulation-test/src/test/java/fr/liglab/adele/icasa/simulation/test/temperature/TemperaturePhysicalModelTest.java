/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.simulation.test.temperature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.felix.ipojo.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * Tests for temperature physical model.
 * 
 * @author Thomas Leveque
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class TemperaturePhysicalModelTest extends AbstractDistributionBaseTest {

    public static final String TEMPERATURE_VAR_NAME = "Temperature";

    @Inject
    public BundleContext context;

    @Inject
    public ContextManager contextMgr;

    @Inject
    private SimulationManager simulationMgr;


    @Before
    public void setUp() {
        waitForStability(context);
        createXMLSharedPreferencesInstance();
    }

    @After
    public void tearDown() {
        try {
            simulationMgr.resetContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createXMLSharedPreferencesInstance() {
        try {
            Collection<ServiceReference<Factory>> refs = context.getServiceReferences(Factory.class,
                    "(factory.name=org.ow2.chameleon.sharedprefs.XmlSharedPreferences)");
            if (refs.size() > 0) {
                ArrayList<ServiceReference<Factory>> arrayReferences = new ArrayList<ServiceReference<Factory>>(refs);
                ServiceReference<Factory> reference = arrayReferences.get(0);

                Factory preferencesFactory = context.getService(reference);

                Properties props = new Properties();
                props.put("instance.name", "XmlSharedPreferences-102");
                props.put("location", "preferences");
                try {
                    preferencesFactory.createComponentInstance(props);
                } catch (Exception e) {
                    fail("Cannot create the instance : " + e.getMessage());
                }
            }

        } catch (InvalidSyntaxException e) {
            fail("Cannot create the instance : " + e.getMessage());
        }
    }
    
    @Test
    public void tempVarExistsTest() {
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        waitForTemperatureExists(zone);

        // cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void defaultTempTest() {
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        Double temp = (Double) zone.getVariableValue(TEMPERATURE_VAR_NAME);
        
        System.out.println("===========> " + temp);
        
        waitForTemperatureEqualsTo(zone, 293.15);

        // cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void setTempTest() {
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        double newTemp = 300.20;
        zone.setVariableValue(TEMPERATURE_VAR_NAME, newTemp);
        
        waitForTemperatureEqualsTo(zone, newTemp);

        // cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void tempWithOneHeaterTest() {
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 150;
        Position position = new Position(100, 100);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        LocatedDevice locatedDevice = simulationMgr.createDevice("iCasa.Heater", "device1", new Hashtable());
        
        Heater heater = (Heater) locatedDevice.getDeviceObject();
        
        heater.setPowerLevel(0.0d);

        double newTemp = 299.20;
        zone.setVariableValue(TEMPERATURE_VAR_NAME, newTemp);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertEquals(newTemp, zone.getVariableValue(TEMPERATURE_VAR_NAME));

        heater.setPowerLevel(1.0d);
        simulationMgr.moveDeviceIntoZone(locatedDevice.getSerialNumber(), zone.getId());
        
        waitForTemperatureGreaterThan(zone, newTemp);

        // cleanup
        contextMgr.removeZone(zoneId);
        simulationMgr.removeDevice(locatedDevice.getSerialNumber());
    }

    public void waitForTemperatureExists(Zone zone) {
        TestUtils.testConditionWithTimeout(new TemperatureVarExistsCondition(zone), 5000, 30);
    }

    public void waitForTemperatureEqualsTo(Zone zone, double temp) {
        TestUtils.testConditionWithTimeout(new TemperatureEqualsToCondition(zone, temp), 5000, 30);
    }

    public void waitForTemperatureGreaterThan(Zone zone, double temp) {
        TestUtils.testConditionWithTimeout(new TemperatureGreaterThanCondition(zone, temp), 5000, 30);
    }

}
