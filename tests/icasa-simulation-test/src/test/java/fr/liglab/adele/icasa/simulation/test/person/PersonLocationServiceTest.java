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
package fr.liglab.adele.icasa.simulation.test.person;

import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.services.PersonLocationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.runner.test.ChameleonRunner;

import javax.inject.Inject;
import java.util.Set;


@RunWith(ChameleonRunner.class)
public class PersonLocationServiceTest {

    @Inject
    public BundleContext context;

	@Inject
	private PersonLocationService personLocationService;

	@Inject
	private SimulationManager simulationMgr;


	@Before
	public void setUp() {

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
