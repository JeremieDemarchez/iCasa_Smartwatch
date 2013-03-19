/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;

import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.script.executor.SimulatorCommand;

public class RunnableCommandAdapter implements Runnable {

	private SimulatorCommand commandService;
	private JSONObject param;
	
	public RunnableCommandAdapter(SimulatorCommand commandService, JSONObject param) {
		this.commandService = commandService;
		this.param = param;
	}
	
	@Override
	public void run() {
		try {
	      commandService.execute(null, null, param);
      } catch (Exception e) {
	      e.printStackTrace();
      }

	}

}
