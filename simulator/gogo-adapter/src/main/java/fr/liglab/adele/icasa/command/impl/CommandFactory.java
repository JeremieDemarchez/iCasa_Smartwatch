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
package fr.liglab.adele.icasa.command.impl;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.script.executor.SimulatorCommand;

@Component
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = SimulatorCommand.PROP_NAMESPACE, value= SimulatorCommand.DEFAULT_NAMESPACE),
        @StaticServiceProperty(type = "java.lang.String", name = SimulatorCommand.PROP_NAME, value= "getFactory"),
		@StaticServiceProperty(type = "java.lang.String", name = SimulatorCommand.PROP_DESCRIPTION, value = "Expose an ipojo factory")
})
@Instantiate
public class CommandFactory implements SimulatorCommand {

	@Requires
	Factory[] m_factories;

	public Object execute(InputStream in, PrintStream out, JSONObject param)
			throws Exception {
		String factoryName = param.getString("name");
		for (Factory f : m_factories) {
			if(f.getName().equals(factoryName)){
				return f;
			}
		}
		System.out.println("Factory not found "+ factoryName);
		return null;
	}
}
