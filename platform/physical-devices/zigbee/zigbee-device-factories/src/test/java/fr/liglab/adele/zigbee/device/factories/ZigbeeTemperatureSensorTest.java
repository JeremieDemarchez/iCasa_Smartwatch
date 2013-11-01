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
package fr.liglab.adele.zigbee.device.factories;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for zigbee temperature sensor.
 * @author Kettani Mehdi
 *
 */
public class ZigbeeTemperatureSensorTest {
	
	private ZigbeeThermometer sensor = new ZigbeeThermometer();

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testPositiveTemperatureConversion(){
		String result = sensor.computeTemperature("13E");
		Assert.assertEquals("19.875", result);
		
		String result2 = sensor.computeTemperature("13F");
		Assert.assertEquals("19.9375", result2);
	}
	
	
	@Test
	public void testNegativeTemperatureConversion(){
		String result = sensor.computeTemperature("E6F");
		Assert.assertEquals("-25.0625", result);
		
		String result2 = sensor.computeTemperature("E71");
		Assert.assertEquals("-24.9375", result2);
	}
}
