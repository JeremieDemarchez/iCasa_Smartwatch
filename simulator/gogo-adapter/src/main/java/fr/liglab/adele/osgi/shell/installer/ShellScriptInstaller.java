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
package fr.liglab.adele.osgi.shell.installer;

import java.net.URI;

/**
 * The Interface ShellScriptInstaller.
 */
public interface ShellScriptInstaller {

	/**
	 * Install a new script as a command
	 * 
	 * @param scope
	 *            : the command scope
	 * @param name
	 *            : the name of the script
	 * @param scriptPath
	 *            : the script URI
	 */
	public void install(String scope, String name, URI scriptPath);

	/**
	 * Remove a script
	 * 
	 * @param scope
	 *            : the scope of the command
	 * @param name
	 *            : the name of the command
	 */
	public void remove(String scope, String name)
			throws IllegalArgumentException;

}
