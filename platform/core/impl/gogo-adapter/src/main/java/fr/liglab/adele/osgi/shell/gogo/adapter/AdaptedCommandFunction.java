/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.List;

import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.commands.ICasaCommand;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.json.JSONObject;

/**
 * AdaptedCommandFunction are used to expose ICommandService as gogo shell commands. It helps to store a reference to
 * the ICommand to be executed.
 */
public class AdaptedCommandFunction implements Function {

	/** The ICommandService command to be executed. */
	final ICasaCommand m_command;

	/**
	 * Instantiates a new adapted command function.
	 * 
	 * @param command the command
	 */
	AdaptedCommandFunction(ICasaCommand command) {
		m_command = command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.felix.service.command.Function#execute(org.apache.felix.service .command.CommandSession,
	 * java.util.List)
	 */
	public Object execute(CommandSession session, List<Object> arguments) throws Exception {
		JSONObject params = new JSONObject();
        int argumentsSize = (arguments == null)?0:arguments.size();
		Signature signature =  m_command.getSignature(argumentsSize);
        if (signature != null){
            String paramsNames[] = signature.getParameters();
            if (arguments != null) {
                for (int i = 0; i < paramsNames.length && argumentsSize > i; i++) {
                    params.put(paramsNames[i], arguments.get(i));
                }
            }
        }
		return m_command.execute(session.getKeyboard(), session.getConsole(), params);
	}
}
