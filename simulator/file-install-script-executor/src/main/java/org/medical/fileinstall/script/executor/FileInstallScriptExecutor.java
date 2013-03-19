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
package fr.liglab.adele.icasa.fileinstall.script.executor;


import java.io.File;

import org.apache.felix.fileinstall.ArtifactInstaller;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;

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
