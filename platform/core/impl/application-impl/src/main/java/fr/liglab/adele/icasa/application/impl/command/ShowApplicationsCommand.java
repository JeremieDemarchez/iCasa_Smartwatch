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
