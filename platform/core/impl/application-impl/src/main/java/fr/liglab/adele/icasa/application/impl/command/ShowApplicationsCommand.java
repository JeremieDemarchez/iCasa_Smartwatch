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
package fr.liglab.adele.icasa.application.impl.command;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationManager;

@Component(name = "ShowApplicationsCommand")
@Provides(properties = {
      @StaticServiceProperty(name = "osgi.command.scope", type = "String", value = "apps"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{showApps}") })
@Instantiate(name="ShowApplicationsCommand-0")
public class ShowApplicationsCommand {
	
	@Requires
	ApplicationManager manager;
	
	public void showApps() {
		List<Application> apps = manager.getApplications();
		
		for (Application application : apps) {
			System.out.println("App ID :: " + application.getId());
	      System.out.println("App Name :: " + application.getName());
	      System.out.println("App Version :: " + application.getVersion());
	      System.out.println("-----------------------------");
      }
				
   }

}
