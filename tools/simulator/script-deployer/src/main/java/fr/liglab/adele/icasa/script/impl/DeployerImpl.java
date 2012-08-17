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
package fr.liglab.adele.icasa.script.impl;

import java.io.File;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.script.ScenarioInstaller;

/**
 * Implementation of the iCASA script deployer (FileInstall extension).
 * 
 * @author bourretp
 */
@Component
@Provides
@Instantiate
public class DeployerImpl implements ArtifactInstaller {

   /*
    private final Map<String, Script> m_scripts = new HashMap<String, Script>();

    @Requires
    private Interpreter m_interpreter;
    
    */
    
    @Requires
    private ScenarioInstaller m_scenarioInstaller;

    @Override
    public boolean canHandle(File artifact) {
        return artifact.getName().endsWith(".icasa");
    }

    @Override
    public void install(File artifact) throws Exception {
   	 System.out.println("Scenario Deployer  " + artifact.getCanonicalPath());   	 
   	 m_scenarioInstaller.installScenario(artifact.getName());
    }

    @Override
    public void update(File artifact) throws Exception {
        // uninstall and install
        uninstall(artifact);
        install(artifact);
    }

    @Override
    public void uninstall(File artifact) throws Exception {
   	 m_scenarioInstaller.uninstallCurrentScenario();
    }
}
