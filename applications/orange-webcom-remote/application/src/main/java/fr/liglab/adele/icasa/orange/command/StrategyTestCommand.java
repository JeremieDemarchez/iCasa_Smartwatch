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

import fr.liglab.adele.cream.facilities.ipojo.annotation.ContextRequirement;
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.device.testable.TestReport;
import fr.liglab.adele.icasa.device.testable.Testable;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.List;
import java.util.function.Consumer;

@CommandProvider(namespace = "zwave-test")
@Component
@Instantiate
public class StrategyTestCommand {

	@Requires(id="testableDevice", specification = ZwaveDevice.class,optional=true, proxy=false)
	@ContextRequirement(spec = Testable.class)
	List<ZwaveDevice> testableDevice;

	private boolean tracking = false;

	@Command
	public void testStrategyPresent() {
		for (ZwaveDevice test : testableDevice){
			System.out.println("Zwave ID :" + test.getNodeId());
		}
	}

	@Command
	public void testBegin(String nodeId) {
		for (ZwaveDevice test : testableDevice){
			if (String.valueOf(test.getNodeId()).equals(nodeId)){
				System.out.println("Test launch on " + nodeId);
				((Testable)test).beginTest(new TestConsumer(nodeId));
			}
		}
	}


	public class TestConsumer implements Consumer<TestReport> {

		private final String nodeId;

		public TestConsumer(String nodeId){
			this.nodeId = nodeId;
		}
		@Override
		public void accept( TestReport testReport) {
			System.out.println("Test result on " + nodeId + " , RESULT : " + testReport.testResult + " , result message : " + testReport.testMessage);
		}
	}
}
