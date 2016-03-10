package fr.liglab.adele.icasa.context.extensions.remote.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

@Component
@Instantiate
@CommandProvider(namespace = "app-manager")
public class SimpleApplicationManagerCommands {

	   private static final Logger LOG = LoggerFactory.getLogger(SimpleApplicationManagerCommands.class);

	@Requires(optional=true,proxy=false)
	public SimpleApplicationManager manager;
	
	@Command
	public void status() {
		if (manager == null) {
			LOG.info("SimpleApplicationManager not started");
			return;
		}
		
		LOG.info("SimpleApplicationManager started and "+(manager.isEnabled() ? "enabled":"not enabled"));
		LOG.info(" resolved requirements are handled : "+manager.includesResolved());
		
	}
	
	
	@Command
	public void repair(String applicationId) {
		if (manager != null && applicationId != null) {
			manager.repair(applicationId);
		}
	}
	
	@Command
	public void enable() {
		enable(null);
	}
	
	@Command
	public void enable(String value) {
		boolean enabled = value == null || Boolean.valueOf(value); 
		if (manager != null) {
			manager.setEnabled(enabled);
		}
	}

	@Command
	public void resolved() {
		resolved(null);
	}
	
	@Command
	public void resolved(String value) {
		boolean resolved = value == null || Boolean.valueOf(value); 
		if (manager != null) {
			manager.setIncludeResolved(resolved);
		}
	}

}
