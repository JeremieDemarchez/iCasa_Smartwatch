/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;

/**
 * 
 * Executes an script
 * 
 * @author Gabriel Pedraza Ferreira
 * 
 */
@Component(name = "ExecuteScriptCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{executeScript}"),
      @StaticServiceProperty(name = "name", value = "execute-script", type = "String") })
@Instantiate(name = "execute-script-command")
public class ExecuteScriptCommand extends AbstractCommand {

	@Requires
	private ScriptExecutor executor;

	private String scriptName;

	@Override
	public Object execute() throws Exception {
		System.out.println("Executing script ... " + scriptName);
		executor.execute(scriptName);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.scriptName = param.getString("scriptName");
	}

	public void executeScript(String scriptName) throws Exception {
		this.scriptName = scriptName;
		execute();
	}

}
