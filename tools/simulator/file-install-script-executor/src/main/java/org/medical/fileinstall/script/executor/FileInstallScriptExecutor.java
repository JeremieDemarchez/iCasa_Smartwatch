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
package org.medical.fileinstall.script.executor;


import java.io.File;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.medical.script.executor.ScriptExecutor;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
public class FileInstallScriptExecutor implements ArtifactInstaller {

	private ScriptExecutor executor;
	
	public boolean canHandle(File file) {
		if (file.getName().endsWith(".bhv")) {
			return true;
		}
		return false;
   }

	public void install(File file) throws Exception {
		executor.executeScript(file);
	   
   }

	public void uninstall(File file) throws Exception {
		executor.stopExecution();
   }

	public void update(File file) throws Exception {
		executor.executeScript(file);	   
   }

}
