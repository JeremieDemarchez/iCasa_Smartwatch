/*
 * Copyright Adele Team LIG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.cilia.helper.CiliaHelper;
import fr.liglab.adele.cilia.helper.MediatorTestHelper;
import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.commons.test.utils.Condition;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.habits.monitoring.measure.generator.Measure;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.button.simulated.SimulatedPushButton;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.ops4j.pax.exam.CoreOptions.*;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class AdapterTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	@Inject
	public SimulationManager simulationManager;

    @Inject
    public ScriptExecutor scriptExecutor;

	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}

	public static Option junitAndMockitoBundles() {
        return new DefaultCompositeOption(
                // Repository required to load harmcrest (OSGi-fied version).
                repository("http://repository.springsource.com/maven/bundles/external").id(
                        "com.springsource.repository.bundles.external"),

                // Repository required to load harmcrest (OSGi-fied version).
                repository("http://repo1.maven.org/maven2/").id(
                        "central"),

                // Mockito without Hamcrest and Objenesis
                mavenBundle("org.mockito", "mockito-core", "1.9.5"),

                // cilia helper
                mavenBundle().groupId("fr.liglab.adele.cilia").artifactId("cilia-helper").versionAsInProject(),//("1.6.4-SNAPSHOT"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.test.helpers").versionAsInProject(),

                // Hamcrest with a version matching the range expected by Mockito
                mavenBundle("org.hamcrest", "com.springsource.org.hamcrest.core", "1.1.0"),

                // Objenesis with a version matching the range expected by Mockito
                wrappedBundle(mavenBundle("org.objenesis", "objenesis", "1.2"))
                        .exports("*;version=1.2"),

                // The default JUnit bundle also exports Hamcrest, but with an (incorrect) version of
                // 4.9 which does not match the Mockito import. When deployed after the hamcrest bundles, it gets
                // resolved correctly.
                CoreOptions.junitBundles(),

                /*
                 * Felix has implicit boot delegation enabled by default. It conflicts with Mockito:
                 * java.lang.LinkageError: loader constraint violation in interface itable initialization:
                 * when resolving method "org.osgi.service.useradmin.User$$EnhancerByMockitoWithCGLIB$$dd2f81dc
                 * .newInstance(Lorg/mockito/cglib/proxy/Callback;)Ljava/lang/Object;" the class loader
                 * (instance of org/mockito/internal/creation/jmock/SearchingClassLoader) of the current class,
                 * org/osgi/service/useradmin/User$$EnhancerByMockitoWithCGLIB$$dd2f81dc, and the class loader
                 * (instance of org/apache/felix/framework/BundleWiringImpl$BundleClassLoaderJava5) for interface
                 * org/mockito/cglib/proxy/Factory have different Class objects for the type org/mockito/cglib/
                 * proxy/Callback used in the signature
                 *
                 * So we disable the bootdelegation. this property has no effect on the other OSGi implementation.
                 */
                frameworkProperty("felix.bootdelegation.implicit").value("false")
        );
    }

	 @Configuration
	    public Option[] configuration() {

		 	List<Option> lst = super.config();
		 	lst.add(junitAndMockitoBundles());
		 	Option conf[] = lst.toArray(new Option[0]);
		 	return conf;
	    }

	/**
	 * Test the reception of a valid measure.
	 */
	@Test
	public void validMeasureWithPushButtonTest(){
        String expectedCiliaComponent = "push-button-adapter";
        String existentCiliaComponent = "transformer";
        String deviceId = "simulatedDevice";
        String chainId = "generator-mesures";
        String deviceType = "iCasa.PushButton";

        testAutonomicAdapterCreation(deviceType,deviceId, chainId, existentCiliaComponent, expectedCiliaComponent, new DeviceActivitySimulator() {
            public void executeActivity(LocatedDevice device) {
                SimulatedPushButton actuator = (SimulatedPushButton)device.getDeviceObject();
                actuator.pushAndRelease();
            }
        });

	}

    /**
     * Test the reception of a valid measure.
     */
    @Test
    public void validMeasureWithPhotometer(){
        String expectedCiliaComponent = "photometer-collector";
        String existentCiliaComponent = "transformer";
        String deviceId = "simulatedDevice";
        String chainId = "generator-mesures";
        String deviceType = "iCasa.Photometer";

        testAutonomicAdapterCreation(deviceType,deviceId, chainId, existentCiliaComponent, expectedCiliaComponent, new DeviceActivitySimulator() {
            public void executeActivity(LocatedDevice device) {
                Photometer actuator = (Photometer)device.getDeviceObject();
                actuator.setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, 10f);
            }
        });

    }

    @Test
    public void validMeasureWithPresenceSensorTest(){
        String expectedCiliaComponent = "presence-collector";
        String existentCiliaComponent = "transformer";
        String deviceId = "simulatedDevice";
        String chainId = "generator-mesures";
        String deviceType = "iCasa.PresenceSensor";

        testAutonomicAdapterCreation(deviceType,deviceId, chainId, existentCiliaComponent, expectedCiliaComponent, new DeviceActivitySimulator() {
            public void executeActivity(LocatedDevice device) {
                PresenceSensor sensor = (PresenceSensor)device.getDeviceObject();
                sensor.setPropertyValue(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE, Boolean.TRUE);
            }
        });

    }

    @Test
    public void validMeasureWithMotionSensorTest(){
        String expectedCiliaComponent = "motion-sensor-collector";
        String existentCiliaComponent = "transformer";
        String deviceId = "simulatedDevice";
        String chainId = "generator-mesures";
        String deviceType = "iCasa.MotionSensor";

        testAutonomicAdapterCreation(deviceType,deviceId, chainId, existentCiliaComponent, expectedCiliaComponent, new DeviceActivitySimulator() {
            public void executeActivity(LocatedDevice device) {
                MotionSensor sensor = (MotionSensor)device.getDeviceObject();
                String zone = "zoneId";
                String person = "Jhon";
                //simulate a person has moved into zone.
                simulationManager.createZone(zone, 100,100,100,100,100,100);
                simulationManager.addPerson(person, "Father");
                //move device into created zone.
                simulationManager.moveDeviceIntoZone(device.getSerialNumber(),zone);
                simulationManager.setPersonZone(person, zone);
            }
        });

    }

    @Test
    public void validMeasureTestUsingScript(){

        String firstScript = "demo_config.bhv";
        String secondScript = "init_demo_oseo.bhv";

        // instrument a cilia helper class
        CiliaHelper helper = new CiliaHelper(context);
        Assert.assertEquals(true, helper.waitToChain("generator-mesures", 5000));
        MediatorTestHelper transformer = helper.instrumentMediatorInstance("generator-mesures", "transformer", new String[]{"in"}, new String[]{"out"});
        Assert.assertNotNull(transformer);

        // init data : run scripts
        ScriptExecutorListener listener = mock(ScriptExecutorListener.class);
        scriptExecutor.addListener(listener);

        // execute script for zones and devices
        scriptExecutor.execute(firstScript);
        if(!helper.waitToComponent("generator-mesures", "presence-collector", 10000)){//It will download the dp and install it.
            Assert.fail("Unable to retrieve presence-collector component after 10sec");
        }
        if(!helper.checkValidState("generator-mesures", "presence-collector", 10000)){//It will download the dp and install it.
            Assert.fail("Unable to retrieve presence-collector  as a valid component after 10sec");
        }

        TestUtils.testConditionWithTimeout(new DeviceNumberCondition(4), 10000, 50);
        TestUtils.testConditionWithTimeout(new ZoneNumberCondition(4), 10000, 50);
        Set<String> devices = simulationManager.getDeviceIds();
        Set<String> zones = simulationManager.getZoneIds();

        //verify(listener).scriptStopped(firstScript);
        // execute second script to trigger events
        scriptExecutor.execute(secondScript);

        CiliaHelper.waitSomeTime(5000);
        scriptExecutor.stop();
        CiliaHelper.checkReceived(transformer,1,5000);
        Assert.assertTrue(transformer.getAmountData()>0);
        Data lastData = transformer.getLastData();
        assertThat(lastData.getContent(), instanceOf(Measure.class));

        Measure measure = (Measure) lastData.getContent();
        Assert.assertEquals(measure.getLocalisation(), simulationManager.getPerson("Paul").getLocation());
        assertThat(devices, hasItem(measure.getDeviceId()));
        assertThat(true, equalTo(measure.getReliability() >  (float)50));
        System.out.println("StartDate:" + new Date(scriptExecutor.getStartDate(secondScript)));
        System.out.println("Measure Time:" + new Date(measure.getTimestamp()));
        assertThat(true, equalTo((measure.getTimestamp() - scriptExecutor.getStartDate(secondScript)) < (1000*scriptExecutor.getFactor(secondScript))));
    }

    private void testAutonomicAdapterCreation(String deviceType, String deviceId, String chainId, String existantCiliaComponent, String expectedCiliaComponent, DeviceActivitySimulator activity){
        CiliaHelper helper = new CiliaHelper(context);

        //wait to chain
        Assert.assertEquals(true, helper.waitToChain(chainId, 5000));

        //wait to transformer component.
        if(!helper.waitToComponent(chainId, existantCiliaComponent, 10000)){
            Assert.fail("Unable to retrieve transformer component after 10sec");
        }
        //instrument transformer.
        MediatorTestHelper transformer = helper.instrumentMediatorInstance(chainId, existantCiliaComponent, new String[]{"in"}, new String[]{"out"});
        Assert.assertNotNull(transformer);

        //it will trigger the dp installation.
        LocatedDevice device = createDevice(deviceType, deviceId);
        Set<String> devices = simulationManager.getDeviceIds();

        //wait for the adapter in a valid state.
        if(!helper.waitToComponent(chainId, expectedCiliaComponent, 10000)){//It will wait the.created component by the dp.
            Assert.fail("Unable to retrieve "+expectedCiliaComponent+" component after 10sec");
        }

        if(!helper.checkValidState(chainId, expectedCiliaComponent, 10000)){//It will wait the created component until is valid (max 10sec).
            Assert.fail("Unable to retrieve "+ expectedCiliaComponent +" as a valid component after 10sec");
        }

        //simulate device activity
        activity.executeActivity(device);

        //at least one message
        CiliaHelper.checkReceived(transformer,1,5000);
        Assert.assertTrue(transformer.getAmountData()>0);

        //Get the last message
        Data lastData = transformer.getLastData();
        assertThat(lastData.getContent(), instanceOf(Measure.class));

        //test value of last message.
        Measure measure = (Measure) lastData.getContent();
        assertThat(devices, hasItem(measure.getDeviceId()));
        assertThat(true, equalTo(measure.getReliability() >  (float)50));
        //dispose chain and simulated devices
        helper.dispose();
        simulationManager.removeAllDevices();
    }

    private LocatedDevice createDevice(String type, String id){
        LocatedDevice device = simulationManager.createDevice(type, id, new Hashtable());
        return device;
    }

    private interface DeviceActivitySimulator {
        void executeActivity(LocatedDevice device);
    }

    class DeviceNumberCondition implements Condition {
        private int m_number;

        public DeviceNumberCondition(int number) {
            m_number = number;
        }

        public boolean isChecked() {
            return (simulationManager.getDeviceIds().size() >= m_number);
        }

        public String getDescription() {
            return "Expected " + m_number + " devices in Simulation manager";
        }
    }

    class ZoneNumberCondition implements Condition {
        private int m_number;

        public ZoneNumberCondition(int number) {
            m_number = number;
        }

        public boolean isChecked() {
            return (simulationManager.getZoneIds().size() >= m_number);
        }

        public String getDescription() {
            return "Expected " + m_number + " zones in Simulation manager";
        }
    }

}