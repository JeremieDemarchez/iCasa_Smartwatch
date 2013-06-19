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
package fr.liglab.adele.icasa.simulation.test.context;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.api.Dependency;
import org.apache.felix.ipojo.api.PrimitiveComponentType;
import org.apache.felix.ipojo.api.Service;
import org.apache.felix.ipojo.api.ServiceProperty;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ContextualFilterTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Inject
	private SimulationManager simulationMgr;
	
	private ConsumerTestDevice consumerApp;
	
	

	@Override
	public List<Option> config() {
		List<Option> options = super.config();
		options.add(ipojoApiBundles());
		return options;
	}

	protected CompositeOption ipojoApiBundles() {
		CompositeOption apamCoreConfig = new DefaultCompositeOption(mavenBundle().groupId("org.apache.felix")
		      .artifactId("org.apache.felix.ipojo.api").versionAsInProject());
		return apamCoreConfig;
	}

	@Before
	public void setUp() {
		waitForStability(context);
		try {
			PrimitiveComponentType deviceCT = new PrimitiveComponentType()
			      .setBundleContext(context)
			      .setClassName(TestDeviceImpl.class.getName())
			      .addService(
			            new Service()
			                  .setSpecification(TestDevice.class.getName())
			                  .addProperty(new ServiceProperty().setName("location").setField("m_location"))
			                  .addProperty(new ServiceProperty().setName("device.serialNumber").setField("m_serialNumber")));

			Dictionary<String, String> conf = new Hashtable<String, String>();
			
			// Creation of device in kitchen
			conf.put("location", "kitchen");
			conf.put("device.serialNumber", "kitchen-1234");
			conf.put("instance.name", "TestDevice-kitchen-1234");
			deviceCT.createInstance(conf);

			// Creation of device in bathroom
			conf.put("location", "bathroom");
			conf.put("device.serialNumber", "bathroom-1234");
			conf.put("instance.name", "TestDevice-bathroom-1234");
			deviceCT.createInstance(conf);
			
			// Creation of device in bedroom
			conf.put("location", "bedroom");
			conf.put("device.serialNumber", "bedroom-1234");
			conf.put("instance.name", "TestDevice-bedroom-1234");
			deviceCT.createInstance(conf);
			

			PrimitiveComponentType appCT = new PrimitiveComponentType().setBundleContext(context)
			      .setClassName(ConsumerTestDeviceImpl.class.getName())
			      .addDependency(new Dependency().setField("m_device").setFilter("(location=${person.paul.location})"))
			      .addService(new Service());


			
			appCT.createInstance("Test-Consumer");
			

			

		} catch (UnacceptableConfiguration e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void personInExistingDeviceRoomTest() {

		prepareSimulationEnvironment();

		simulationMgr.addPerson("paul", "Boy");
		simulationMgr.setPersonZone("paul", "kitchen");

		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);
		
		Assert.assertNotNull(consumerApp);
		Assert.assertEquals(simulationMgr.getPerson("paul").getLocation(), consumerApp.getDeviceLocation());
		
	}
	
	@Test
	public void personInNotExistingDeviceRoomTest() {

		prepareSimulationEnvironment();

		simulationMgr.addPerson("paul", "Boy");

		simulationMgr.setPersonZone("paul", "livingroom");

		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		

		Assert.assertNull(consumerApp);		
	}
	
	@Test
	public void personMovingInTwoExistingDeviceRoomTest() {

		prepareSimulationEnvironment();

		simulationMgr.addPerson("paul", "Boy");

		simulationMgr.setPersonZone("paul", "kitchen");
		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		
		Assert.assertNotNull(consumerApp);		
		Assert.assertEquals(simulationMgr.getPerson("paul").getLocation(), consumerApp.getDeviceLocation());
		
		simulationMgr.setPersonZone("paul", "bathroom");
		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		
		Assert.assertNotNull(consumerApp);
		Assert.assertEquals(simulationMgr.getPerson("paul").getLocation(), consumerApp.getDeviceLocation());
				
	}	
	
	@Test
	public void personMovingInTwoNotExistingDeviceRoomTest() {

		prepareSimulationEnvironment();

		simulationMgr.addPerson("paul", "Boy");

		simulationMgr.setPersonZone("paul", "kitchen");
		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		
		Assert.assertNotNull(consumerApp);		
		Assert.assertEquals(simulationMgr.getPerson("paul").getLocation(), consumerApp.getDeviceLocation());
		
		simulationMgr.setPersonZone("paul", "livingroom");
		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		
		Assert.assertNull(consumerApp);
				
	}	

	
	@Test
	public void twoPersonInDeviceRoomTest() {

		prepareSimulationEnvironment();

		simulationMgr.addPerson("paul", "Boy");
		simulationMgr.addPerson("amelie", "Girl");

		simulationMgr.setPersonZone("paul", "kitchen");
		simulationMgr.setPersonZone("amelie", "kitchen");

		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		

		Assert.assertNotNull(consumerApp);			
		Assert.assertEquals(simulationMgr.getPerson("paul").getLocation(), consumerApp.getDeviceLocation());
	}	
	
	
	@Test
	public void wrongPersonInDeviceRoomTest() {
		prepareSimulationEnvironment();
		
		simulationMgr.addPerson("amelie", "Girl");
		simulationMgr.setPersonZone("amelie", "livingroom");

		consumerApp = (ConsumerTestDevice) getService(context, ConsumerTestDevice.class);		
		Assert.assertNull(consumerApp);						
	}	
	
	private void prepareSimulationEnvironment() {
		simulationMgr.createZone("kitchen", new Position(0, 0), 50);
		simulationMgr.createZone("bathroom", new Position(0, 100), 50);
		simulationMgr.createZone("livingroom", new Position(0, 200), 50);
		simulationMgr.createZone("bedroom", new Position(0, 300), 50);
	}
	
	
}
