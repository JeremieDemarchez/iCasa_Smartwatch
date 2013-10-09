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
package fr.liglab.adele.osgi.shell.install;

import java.io.File;

import fr.liglab.adele.icasa.Constants;
import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.osgi.shell.installer.ShellScriptInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Provides
@Instantiate
public class FileInstallShellScriptInstaller implements ArtifactInstaller {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);

    private final static String EXTENSIONS = ".gogo";

	@Requires
	protected ShellScriptInstaller _scriptInstaller;

	public boolean canHandle(File artifact) {
		return artifact.getName().endsWith(EXTENSIONS);
	}

	public void install(File artifact) throws Exception {
		logger.debug("Install gogo script " + artifact.getAbsolutePath());

		// register the command
		_scriptInstaller.install(getScopeName(artifact),
				getCommandName(artifact), artifact.toURI());
	}

	public void update(File artifact) throws Exception {
		uninstall(artifact);
		install(artifact);
	}

	public void uninstall(File artifact) throws Exception {
		logger.debug("Uninstall gogo script "
				+ artifact.getAbsolutePath());

		// unregister the previously registered command :
		_scriptInstaller.remove(getScopeName(artifact),
				getCommandName(artifact));
	}

	/**
	 * Compute the command name from the given file
	 * 
	 * @param artifact
	 *            : the given file
	 * @return the command name
	 */
	private static final String getCommandName(File artifact) {
		return artifact.getName().replace(EXTENSIONS, "");
	}

	/**
	 * Gets the scope name from the given file
	 * 
	 * @param artifact
	 *            the artifact is the given file
	 * @return the scope name
	 */
	private static final String getScopeName(File artifact) {
		String[] dirs = artifact.getAbsolutePath().split(File.separator);
		return dirs[dirs.length - 2];
	}

}
