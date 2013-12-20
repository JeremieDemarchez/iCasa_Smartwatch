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

import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.ow2.chameleon.runner.test.ChameleonRunner;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

@RunWith(ChameleonRunner.class)
public class ContextualFilterTest {

	@Inject
	public BundleContext context;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Inject
	private SimulationManager simulationMgr;

	private ContextSource contextSource;
    OSGiHelper helper;

	//private ConsumerSimple consumerApp;

	// private PrimitiveComponentType appCT;

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
		ContextSource contextSource = helper.getServiceObject(ContextSource.class,
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
