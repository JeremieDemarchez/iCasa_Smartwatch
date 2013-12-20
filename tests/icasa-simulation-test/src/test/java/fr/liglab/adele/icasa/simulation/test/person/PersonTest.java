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

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.ow2.chameleon.runner.test.ChameleonRunner;

@RunWith(ChameleonRunner.class)
public class PersonTest  {

	@Inject
	public BundleContext context;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

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
	public void creationPersonWithoutNameTest() {
				
		String personName = "";
		String personType = "Grandfather";

		expectedException.expect(IllegalArgumentException.class);
		simulationMgr.addPerson(personName, personType);
	}

	/**
	 * Test the creation of a new person.
	 */
	@Test
	public void creationPersonWithPredefinedTypeTest() {
		
		
		String personName = "Patrick";
		String personType = "Grandfather";

		Person person = simulationMgr.addPerson(personName, personType);
		Assert.assertNotNull(person);
		Assert.assertEquals(personName, person.getName());
		Assert.assertEquals(personType, person.getPersonType().getName());
	}
		

	@Test
	public void creationPersonWithNotExistingTypeTest() {
				
		String personName = "Patrick";
		String personType = "Aunt";

		expectedException.expect(IllegalArgumentException.class);
		simulationMgr.addPerson(personName, personType);
	}


	@Test
	public void creationPersonWithoutTypeTest() {
		
		String personName = "Patrick";
		String personType = "";

		expectedException.expect(IllegalArgumentException.class);
		simulationMgr.addPerson(personName, personType);
	}

	@Test
	public void movePersonToExistingZoneTest() {
				
		String personName = "Patrick";
		String personType = "Grandfather";

		simulationMgr.createZone("livingroom", 410, 28, Zone.DEFAULT_Z_BOTTOM, 245, 350, Zone.DEFAULT_Z_LENGTH);
		Person person = simulationMgr.addPerson(personName, personType);
		simulationMgr.setPersonZone("Patrick", "livingroom");

		Assert.assertEquals(person.getLocation(), "livingroom");
		Assert.assertEquals(personName, person.getName());
		Assert.assertEquals(personType, person.getPersonType().getName());
	}

	@Test
	public void movePersonToPositionTest() {
				
		String personName = "Patrick";
		String personType = "Grandfather";

		Person person = simulationMgr.addPerson(personName, personType);
		
		
		Position position = new Position(20, 20);
		person.setCenterAbsolutePosition(position);

		Assert.assertEquals(person.getCenterAbsolutePosition(), position);
		Assert.assertEquals(personName, person.getName());
		Assert.assertEquals(personType, person.getPersonType().getName());
		

		
	}
	
	@Test
	public void testPersonListener() {
		String personName = "Patrick";
		String personType = "Grandfather";

		Person person = simulationMgr.addPerson(personName, personType);
		
		Position position = new Position(20, 20);
		person.setCenterAbsolutePosition(position);
		
		PersonTestListener listener = new PersonTestListener();
		person.addListener(listener);
		person.setCenterAbsolutePosition(new Position(40, 40));
		Assert.assertEquals(position, listener.getOldPosition());
	}
	
	

}
