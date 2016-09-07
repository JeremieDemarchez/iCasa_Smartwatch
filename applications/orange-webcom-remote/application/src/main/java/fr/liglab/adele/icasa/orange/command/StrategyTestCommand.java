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
package fr.liglab.adele.icasa.orange.command;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.orange.service.TestReport;
import fr.liglab.adele.icasa.orange.service.TestRunningException;
import fr.liglab.adele.icasa.orange.service.ZwaveTestStrategy;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.List;
import java.util.function.BiConsumer;

@CommandProvider(namespace = "zwave-test")
@Component
@Instantiate
public class StrategyTestCommand {

	@Requires(id="testStrategies", specification = ZwaveTestStrategy.class,optional=true, proxy=false)
	List<ZwaveTestStrategy> testStrategies;

	private boolean tracking = false;

	@Command
	public void testStrategyPresent() {
		for (ZwaveTestStrategy test : testStrategies){
			System.out.println("Zwave Test strategy name " + test.getStrategyName());
			System.out.println("Can launch test on : ");

			for (String targetId : test.getTestTargets()){
				System.out.println("Zwave ID :" + targetId);
			}
		}
	}

	@Command
	public void testBegin(String nodeId) {
		for (ZwaveTestStrategy test : testStrategies){
			for (String targetId : test.getTestTargets()){
				if (targetId.equals(nodeId)){
					System.out.println("Test launch on " + nodeId + " with strategy " + test.getTestTargets());
					try {
						test.beginTest(nodeId,new TestBiConsumer(),true);
					} catch (TestRunningException e) {
						System.out.println("Start test fail, because " + e.toString());
					}
				}
			}
		}
	}

	public class TestBiConsumer implements BiConsumer<String,TestReport>{

		@Override
		public void accept(String s, TestReport testReport) {
			System.out.println("Test result on " + s + " , RESULT : " + testReport.testResult + " , result message : " + testReport.testMessage);
		}
	}
}
