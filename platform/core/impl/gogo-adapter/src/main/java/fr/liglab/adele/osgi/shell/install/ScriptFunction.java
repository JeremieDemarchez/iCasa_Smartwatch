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
package fr.liglab.adele.osgi.shell.install;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.List;

import fr.liglab.adele.icasa.Constants;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptFunction implements Function {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);

    /**
	 * The path of the script to be executed by gogo
	 */
	private final URI _scriptURI;

	/**
	 * The command processor used to create new session
	 */
	private final CommandProcessor _commandProcessor;

	ScriptFunction(URI scriptPath, CommandProcessor processor) {
		_scriptURI = scriptPath;
		_commandProcessor = processor;
	}

	public Object execute(CommandSession session, List<Object> arguments)
			throws Exception {

		/*
		 * There is two options to implement the execution of the script. One is
		 * to create a new session, the other is to execute the script is the
		 * same session.
		 * 
		 * Creating a new session is safer since it creates a new namespace for
		 * variables. Using the same session could have strange sideeffects that
		 * we do no want.
		 * 
		 * The main problem is that there is no EXPORT command that can be use
		 * to export variables. This is clearly an issue. To deal with this, a
		 * session object is given to the script via a SESSION
		 */

		CommandSession newSession = _commandProcessor.createSession(
				session.getKeyboard(), session.getConsole(), System.err);

		// set script arguments
		newSession.put("0", _scriptURI);
		newSession.put("args", arguments);
		newSession.put("SESSION", session);

		for (int i = 0; i < arguments.size(); ++i) {
			newSession.put(String.valueOf(i + 1), arguments.get(i));
		}

		Object result = newSession.execute(readScript(_scriptURI));
		newSession.close();
		return result;
	}

	/**
	 * This code is a copy org.apache.felix.gogo.shell.Shell.java under ASF
	 * license
	 */
	private CharSequence readScript(URI script) throws Exception {
		URLConnection conn = script.toURL().openConnection();
		int length = conn.getContentLength();

		if (length == -1) {
			logger.error("eek! unknown Contentlength for: " + script);
			length = 10240;
		}

		InputStream in = conn.getInputStream();
		CharBuffer cbuf = CharBuffer.allocate(length);
		Reader reader = new InputStreamReader(in);
		reader.read(cbuf);
		in.close();
		cbuf.rewind();

		return cbuf;
	}

}
