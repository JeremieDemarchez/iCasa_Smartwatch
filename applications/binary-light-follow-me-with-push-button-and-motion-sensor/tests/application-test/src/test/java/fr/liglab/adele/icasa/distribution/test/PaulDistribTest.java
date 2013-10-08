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

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PaulDistribTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;
	
	@Inject
	public SimulationManager simulationManager;
	
	@Inject
	public ContextManager icasa;
	
	@Inject
	public ScriptExecutor scriptExecutor;
	
	@Before
	public void setUp() {
		waitForStability(context);
	}
	
	@After
	public void tearDown() {

	}

    @Test
     public void testApplicationValidity(){

     }

}