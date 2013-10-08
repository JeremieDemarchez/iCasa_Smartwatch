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
package fr.liglab.adele.icasa.simulation.test.person;

import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.services.PersonLocationService;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PersonLocationServiceTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;
	
	@Inject
	private PersonLocationService personLocationService;
	
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
	public void testPersonLocation() {

		simulationMgr.createZone("livingroom", 410, 28, Zone.DEFAULT_Z_BOTTOM, 245, 350, Zone.DEFAULT_Z_LENGTH);
		simulationMgr.createZone("bathroom", 10, 430, Zone.DEFAULT_Z_BOTTOM, 245, 350, Zone.DEFAULT_Z_LENGTH);
		simulationMgr.addPerson("Patrick", "Grandfather");
		simulationMgr.setPersonZone("Patrick", "livingroom");
		
		Set<String> personsName = personLocationService.getPersonInZone("livingroom");

		Assert.assertEquals(personsName.size(), 1);
		Assert.assertTrue(personsName.contains("Patrick"));
		
		simulationMgr.addPerson("Paul", "Boy");
		simulationMgr.setPersonZone("Paul", "livingroom");
		
		personsName = personLocationService.getPersonInZone("livingroom");

		Assert.assertEquals(personsName.size(), 2);
		Assert.assertTrue(personsName.contains("Patrick"));
		Assert.assertTrue(personsName.contains("Paul"));
		
		simulationMgr.setPersonZone("Patrick", "bathroom");
		
		personsName = personLocationService.getPersonInZone("livingroom");
		
		Assert.assertEquals(personsName.size(), 1);
		Assert.assertTrue(personsName.contains("Paul"));
		
	}
	
	
	
}
