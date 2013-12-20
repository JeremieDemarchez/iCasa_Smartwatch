/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulation.test.interceptor;

import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.api.Dependency;
import org.apache.felix.ipojo.api.PrimitiveComponentType;
import org.apache.felix.ipojo.api.Service;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.runner.test.ChameleonRunner;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

import javax.inject.Inject;
import java.util.Hashtable;

@RunWith(ChameleonRunner.class)
public class InterceptorTest {

	@Inject
	public BundleContext context;

	@Inject
	private SimulationManager simulationMgr;
	
	private BinaryLightConsumer consumerApp;
    private OSGiHelper helper;

    @Before
	public void setUp() {
        helper = new OSGiHelper(context);
	}

	@After
	public void tearDown() {
		try {
			simulationMgr.resetContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
        helper.dispose();
	}

	@Test
	public void contextAddedToServiceReference() {
        String instanceName = "testConsumer1";
        PrimitiveComponentType consumer = createInstanceConsumer(instanceName,"(location=kitchen)");

        prepareSimulationEnvironment();
		LocatedDevice lDevice = simulationMgr.createDevice("iCasa.BinaryLight", "12345", new Hashtable<String, Object>());
		simulationMgr.moveDeviceIntoZone(lDevice.getSerialNumber(), "kitchen");
        helper.waitForService(BinaryLightConsumer.class, null, 10000);

		consumerApp = helper.getServiceObject(BinaryLightConsumer.class);
				
		Assert.assertNotNull(consumerApp);
		Assert.assertEquals(consumerApp.getDeviceId(), lDevice.getSerialNumber());
        consumer.disposeInstance(instanceName);
        consumer.stop();

	}
	
	@Test
	public void serviceReferenceWithOtherContext() {
        String instanceName = "testConsumer2";
        PrimitiveComponentType component = createInstanceConsumer(instanceName, "(location=kitchen)");
        prepareSimulationEnvironment();
		LocatedDevice lDevice = simulationMgr.createDevice("iCasa.BinaryLight", "12345", new Hashtable<String, Object>());
		simulationMgr.moveDeviceIntoZone(lDevice.getSerialNumber(), "livingroom");

		consumerApp = helper.getServiceObject(BinaryLightConsumer.class);
		Assert.assertNull(consumerApp);
        component.disposeInstance(instanceName);
        component.stop();
	}	
	
	
	@Test
	public void contextChangedToServiceReference() {
        String instanceName ="testconsumer3";
        PrimitiveComponentType consumer = createInstanceConsumer(instanceName, "(location=kitchen)");
		prepareSimulationEnvironment();
		LocatedDevice lDevice = simulationMgr.createDevice("iCasa.BinaryLight", "12345", new Hashtable<String, Object>());
		simulationMgr.moveDeviceIntoZone(lDevice.getSerialNumber(), "kitchen");

        helper.waitForService(BinaryLightConsumer.class, null, 10000);
        consumerApp = helper.getServiceObject(BinaryLightConsumer.class);
				
		Assert.assertNotNull(consumerApp);
		Assert.assertEquals(consumerApp.getDeviceId(), lDevice.getSerialNumber());		
		
		simulationMgr.moveDeviceIntoZone(lDevice.getSerialNumber(), "livingroom");
		consumerApp = helper.getServiceObject(BinaryLightConsumer.class);
		Assert.assertNull(consumerApp);
        consumer.disposeInstance(instanceName);
        consumer.stop();
	}
	

	/**
	 * Creates a component type and instance of class BinaryLightConsumerImpl
	 * @param filter
	 */
	private PrimitiveComponentType createInstanceConsumer(String instanceName, String filter) {
        PrimitiveComponentType appCT = new PrimitiveComponentType();
        appCT.setBundleContext(context);
        appCT.setClassName(BinaryLightConsumerImpl.class.getName());
        appCT.setComponentTypeName(instanceName);
        appCT.addDependency(new Dependency().setField("m_device").setFilter(filter));
        appCT.addService(new Service().setSpecification(BinaryLightConsumer.class.getName()));
        appCT.start();
        try {
			appCT.createInstance(instanceName);
		} catch (UnacceptableConfiguration e) {
			e.printStackTrace();
		} catch (MissingHandlerException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

        return appCT;
	}

	/**
	 * Creates a basic simulation environment
	 */
	private void prepareSimulationEnvironment() {
		simulationMgr.createZone("kitchen", new Position(0, 0), 50);
		simulationMgr.createZone("bathroom", new Position(0, 100), 50);
		simulationMgr.createZone("livingroom", new Position(0, 200), 50);
		simulationMgr.createZone("bedroom", new Position(0, 300), 50);
	}
}
