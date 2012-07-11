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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.script.Interpreter;
import fr.liglab.adele.icasa.script.ScenarioInstaller;
import fr.liglab.adele.icasa.script.Script;

/**
 * Implementation of the iCASA script deployer (FileInstall extension).
 * 
 * @author Gabriel
 */
@Component
@Provides
@Instantiate
public class ScenarioInstallerImpl implements ScenarioInstaller, ArtifactInstaller {

    //private final Map<String, Script> m_scripts = new HashMap<String, Script>();

    private final Map<String, File> m_scenarios = new HashMap<String, File>();
    
    private Script currentScenario = null;
    
    @Requires
    private Interpreter m_interpreter;

    @Override
    public boolean canHandle(File artifact) {
        return artifact.getName().endsWith(".icasa");
    }

    @Override
    public void install(File artifact) throws Exception {
   	 System.out.println("Scenario Installer ---- > " + artifact.getCanonicalPath());
   	 m_scenarios.put(artifact.getName(), artifact);   	
    }

    @Override
    public void update(File artifact) throws Exception {
        // uninstall and install
        uninstall(artifact);
        install(artifact);
    }

    @Override
    public void uninstall(File artifact) throws Exception {
        // Stop and remove the script
   	 m_scenarios.remove(artifact.getName());
    }

	@Override
   public void installScenario(String name) throws Exception {
		File scenarioFile = m_scenarios.get(name);
		//System.out.println("Sceneario File ---- > " + scenarioFile);
		if (scenarioFile!=null) {
			
			if (currentScenario!=null)
				currentScenario.stop();
			
	        // Parse the script
	        FileInputStream in = new FileInputStream(scenarioFile);
	        Script script;
	        try {
	            script = m_interpreter.parse(in);
	        } finally {
	            in.close();
	        }
	        // Start the script
	        script.start();
		}			   
   }

	@Override
   public List<String> getScenarioList() {
	   List<String> list = new ArrayList<String>(m_scenarios.keySet());
	   return list;
   }

	@Override
   public void uninstallCurrentScenario() throws Exception {
		if (currentScenario!=null)
			currentScenario.stop();	   
   }
}
