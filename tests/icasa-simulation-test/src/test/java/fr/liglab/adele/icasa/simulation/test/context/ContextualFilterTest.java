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
package fr.liglab.adele.icasa.simulation.test.context;

import javax.inject.Inject;

import org.apache.felix.ipojo.ContextSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.Person;
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

	private ContextSource contextSource;

	//private ConsumerSimple consumerApp;

	// private PrimitiveComponentType appCT;

	@Before
	public void setUp() {
		waitForStability(context);
		/*
		try {
			PrimitiveComponentType deviceCT = new PrimitiveComponentType()
			      .setBundleContext(context)
			      .setClassName(SimpleDeviceImpl.class.getName())
			      .addService(
			            new Service()
			                  .setSpecification(SimpleDevice.class.getName())
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

			appCT = new PrimitiveComponentType().setBundleContext(context)
			      .setClassName(ConsumerSimpleImpl.class.getName())
			      .addDependency(new Dependency().setField("m_device").setFilter("(location=${person.paul.location})"))
			      .addService(new Service());

		} catch (UnacceptableConfiguration e) {
			e.printStackTrace();
		} catch (MissingHandlerException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		*/
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
	public void filterInPlatformTest() {
		contextSource = getContextSource();
		Assert.assertNotNull(contextSource);
	}

	@Test
	public void personContextModifiedTest() {
		prepareSimulationEnvironment();

		Person person = simulationMgr.addPerson("paul", "Boy");
		simulationMgr.setPersonZone("paul", "kitchen");
		contextSource = getContextSource();
		Assert.assertNotNull(contextSource);
		Assert.assertEquals(person.getLocation(), contextSource.getProperty("person.paul.location"));
	}

	@Test
	public void personContextModifiedTwiceTest() {
		prepareSimulationEnvironment();

		Person person = simulationMgr.addPerson("paul", "Boy");
		simulationMgr.setPersonZone("paul", "kitchen");
		contextSource = getContextSource();
		Assert.assertNotNull(contextSource);
		Assert.assertEquals(person.getLocation(), contextSource.getProperty("person.paul.location"));
		simulationMgr.setPersonZone("paul", "livingroom");
		Assert.assertEquals(person.getLocation(), contextSource.getProperty("person.paul.location"));
	}

	@Test
	public void otherPersonContextModifiedTest() {
		prepareSimulationEnvironment();

		simulationMgr.addPerson("pierre", "Boy");
		simulationMgr.setPersonZone("pierre", "kitchen");
		contextSource = getContextSource();
		Assert.assertNotNull(contextSource);
		Assert.assertNull(contextSource.getProperty("person.paul.location"));
	}

	private ContextSource getContextSource() {
		ContextSource contextSource = (ContextSource) getService(context, ContextSource.class,
		      "(factory.name=ICasaContextSourceBuilder)");
		return contextSource;
	}

	private void prepareSimulationEnvironment() {
		simulationMgr.createZone("kitchen", new Position(0, 0), 50);
		simulationMgr.createZone("bathroom", new Position(0, 100), 50);
		simulationMgr.createZone("livingroom", new Position(0, 200), 50);
		simulationMgr.createZone("bedroom", new Position(0, 300), 50);
	}

}
