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
package fr.liglab.adele.osgi.shell.gogo.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.json.JSONObject;

import fr.liglab.adele.icasa.command.SimulatorCommand;

class AdaptedFunction implements Function {

	final SimulatorCommand m_command;

	AdaptedFunction(SimulatorCommand command) {
		m_command = command;
	}

	public Object execute(CommandSession session, List<Object> arguments)
			throws Exception {
		if (arguments.size() > 1) {
			throw new IllegalArgumentException(
					"function does not accept more than one parameter");
		}
		JSONObject params = new JSONObject();

		if (arguments.size() > 0) {
			Object firstArg = arguments.get(0);
			if (firstArg instanceof Map) {
				params = new JSONObject((Map) firstArg);
			} else if (firstArg instanceof String) {
				params = new JSONObject((String) firstArg);
			} else {
				throw new IllegalArgumentException(
						"function only accept Map or JSON as parameter");
			}
		}
		
		return m_command.execute(session.getKeyboard(), session.getConsole(),
				params);
	}
}
