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
package fr.liglab.adele.icasa.simulation.test.script;

import javax.inject.Inject;

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
import fr.liglab.adele.commons.test.utils.Condition;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ScriptTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Inject
	private SimulationManager simulationMgr;
	
	@Inject
	private ScriptExecutor executor;

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
	public void saveAndExecuteScriptTest() {
	    simulationMgr.createZone("test-zone", 10, 10, 0, 100, 100, 100);
	    simulationMgr.addPerson("Patrick", "Grandfather");
	    simulationMgr.setPersonZone("Patrick", "test-zone");
	    
	    executor.saveSimulationScript("test-script.bhv");
	    
	    simulationMgr.resetContext();
	    
	    executor.execute("test-script.bhv");
	    	    	   	  	   
	    	    

	}

	
    class ZoneNameAndNumberCondition implements Condition {
        private int m_number;

        public ZoneNameAndNumberCondition(int number) {
            m_number = number;
        }

        public boolean isChecked() {
            return (m_number == simulationMgr.getZones().size());
        }

        public String getDescription() {
            return "Expected " + m_number + " zones in Simulator Manager";
        }
    }
    

}
