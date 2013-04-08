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

import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PaulDistribTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	@Inject
	public ContextManager icasa;

	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}

	/**
	 * Test the creation of a new zone.
	 */
	@org.junit.Test
	public void creationZoneTest(){
		Set<String> devices = icasa.getDeviceIds();
		for (String device : devices){
			System.out.println("device n : " + device);
		}
//		String zone_id_0 = "myZone-0";
//		int zone_0_scope = 5;
//		Position positionZone_0 = new Position(0,0);
//		Zone zone_0 = icasa.createZone(zone_id_0, positionZone_0, zone_0_scope);
//		//Test the zone and its Id. 
//		Assert.assertNotNull(zone_0);
//		Assert.assertEquals(zone_id_0, zone_0.getId());
	}

}