package fr.liglab.adele.icasa.simulation.test.temperature;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void tempVarExistsTest(){
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        waitForTemperatureExists(zone);

        //cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void defaultTempTest(){
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        waitForTemperatureEqualsTo(zone, 293.15);

        //cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void setTempTest(){
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 5;
        Position position = new Position(10, 10);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        double newTemp = 300.20;
        zone.setVariableValue(TEMPERATURE_VAR_NAME, newTemp);

        waitForTemperatureEqualsTo(zone, newTemp);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(newTemp, zone.getVariableValue(TEMPERATURE_VAR_NAME));

        //cleanup
        contextMgr.removeZone(zoneId);
    }

    @Test
    public void tempWithOneHeaterTest(){
        assertNotNull(contextMgr);

        String zoneId = "tempZone-0";
        int zoneScope = 50;
        Position position = new Position(100, 100);
        Zone zone = contextMgr.createZone(zoneId, position, zoneScope);

        LocatedDevice device = simulationMgr.createDevice("iCasa.Heater", "device1", new Hashtable());
        device.setPropertyValue(Heater.HEATER_POWER_LEVEL, 0.0d);
        device.setPropertyValue(Heater.HEATER_MAX_POWER_LEVEL, 1000);

        double newTemp = 300.20;
        zone.setVariableValue(TEMPERATURE_VAR_NAME, newTemp);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(newTemp, zone.getVariableValue(TEMPERATURE_VAR_NAME));

        device.setPropertyValue(Heater.HEATER_POWER_LEVEL, 1.0d);

        waitForTemperatureGreaterThan(zone, newTemp);

        //cleanup
        contextMgr.removeZone(zoneId);
        simulationMgr.removeDevice(device.getSerialNumber());
    }

    public void waitForTemperatureExists(Zone zone) {
        TestUtils.testConditionWithTimeout(new TemperatureVarExistsCondition(zone), 5000, 10);
    }

    public void waitForTemperatureEqualsTo(Zone zone, double temp) {
        TestUtils.testConditionWithTimeout(new TemperatureEqualsToCondition(zone, temp), 5000, 10);
    }

    public void waitForTemperatureGreaterThan(Zone zone, double temp) {
        TestUtils.testConditionWithTimeout(new TemperatureGreaterThanCondition(zone, temp), 5000, 10);
    }

}

