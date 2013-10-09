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
package fr.liglab.adele.icasa.simulation.test.util;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.util.List;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;

public abstract class IPojoApiBaseTest extends AbstractDistributionBaseTest {

	@Override
	public List<Option> config() {
		List<Option> options = super.config();
		options.add(ipojoApiBundles());
		return options;
	}

	protected CompositeOption ipojoApiBundles() {
		CompositeOption iPojoAPICoreConfig = new DefaultCompositeOption(mavenBundle().groupId("org.apache.felix")
		      .artifactId("org.apache.felix.ipojo.api").versionAsInProject());
		return iPojoAPICoreConfig;
	}
	
}
