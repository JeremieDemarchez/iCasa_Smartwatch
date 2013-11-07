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
package fr.liglab.adele.zigbee.device.factories.tests;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.liglab.adele.zigbee.device.factories.ZigbeePhotometer;

public class ZigbeePhotometerTest {
	
	private ZigbeePhotometer photometer = new ZigbeePhotometer();

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testComputedIlluminance(){
		
		Double result = photometer.computeIlluminance("?>=5");
		Assert.assertEquals("1042.0", result.toString());
		
		Double result2 = photometer.computeIlluminance("<297");
		Assert.assertEquals("92.0", result2.toString());
	}
	
	@Test
	public void testComputedIlluminanceWithInvalidData(){
		Double result = photometer.computeIlluminance("?>=");
		Assert.assertEquals("0.0", result.toString());
	}
}
