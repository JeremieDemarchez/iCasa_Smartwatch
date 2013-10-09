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
package fr.liglab.adele.icasa.simulation.test;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DistributioniCasaTest extends AbstractDistributionBaseTest {

	@Inject
	BundleContext context;

	/**
	 * It will the wait for the stability of the gateway.
	 * It it does not reach the stability. 
	 */
	@Test
	public void testDistributionStability() {
		try{
			waitForStability(context);
		}catch (IllegalStateException ex){
			fail("Unable to reach stability of the gateway");
		}
	}/**
	 * It will the wait for the stability of the gateway.
	 * It it does not reach the stability. 
	 */
	@Test
	public void testFactoriesStability() {
		waitForStability(context);//Wait for the stability of the wateway.
		try{
			waitForiPojoFactoriesStability(context);
		}catch (IllegalStateException ex){
			fail("Unable to reach stability of the gateway");
		}
	}
}