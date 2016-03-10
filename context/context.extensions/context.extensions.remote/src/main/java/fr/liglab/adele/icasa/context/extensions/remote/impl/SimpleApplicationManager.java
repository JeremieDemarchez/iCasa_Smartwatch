package fr.liglab.adele.icasa.context.extensions.remote.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.context.extensions.remote.api.ContextApplicationRegistry;
import fr.liglab.adele.icasa.context.extensions.remote.api.ContextApplicationRegistry.Requirement;
import fr.liglab.adele.icasa.context.model.introspection.EntityProvider;


@Component
@Instantiate
@Provides(specifications=SimpleApplicationManager.class)

public class SimpleApplicationManager {

	@Requires(optional=false,proxy=false)
	ContextApplicationRegistry applicationRegistry;

	@Requires(optional=false,proxy=false)
	ContextFactoryManager entityRegistry;
	
	@Requires(specification=EntityProvider.class,optional=true,proxy=false)
	List<EntityProvider> providers;
	
	/**
	 * Try to activate an appropriate context provider for the specified application
	 */
	public void repair(String applicationId) {
		
		if (!isEnabled()) {
			return;
		}
		
		/*
		 * Get the set of unresolved context dependencies of the different components of the application
		 */
		Set<String> requiredContextServices = new HashSet<>();
		
		for (String factoryId : nullable(applicationRegistry.getFactoriesByApplicationId(applicationId))) {
			for (String instanceId : nullable(applicationRegistry.getInstances(factoryId))) {
				for (Requirement requirement : applicationRegistry.getInstance(factoryId, instanceId).getRequirements()) {
					if (includeResolved || !requirement.getState().equals(Requirement.State.RESOLVED)) {
						requiredContextServices.add(requirement.getSpecification());
					}
				}
			}
		}
		
		if (requiredContextServices.isEmpty()) {
			return;
		}
		
		/*
		 * Try to find a disabled provider that may satisfy the requirements
		 */
		for (EntityProvider entityProvider : providers) {
			for (String entityId : entityProvider.getProvidedEntities()) {
				
				/*
				 * If the provider is already enabled and is not currently satisfying the requirement
				 * we ignore it, and hope another provider may work
				 */
				if (entityProvider.isEnabled(entityId))
					continue;

				/*
				 * Otherwise we verify if the provider may satisfy some of the unsatisfied requirements
				 */
				if (entityRegistry.getContextFactoriesIds().contains(entityId)) {
					
					Set<String> satisfiedRequirements = new HashSet<>();
					for (String requiredService : requiredContextServices) {
						if (entityRegistry.getSetOfContextServices(entityId).contains(requiredService)) {
							satisfiedRequirements.add(requiredService);
						}
					}
					
					/*
					 * Enable the provider 
					 */
					if (!satisfiedRequirements.isEmpty()) {
						entityProvider.enable(entityId);
						requiredContextServices.removeAll(satisfiedRequirements);
					}
				}
			}
		}
	}
	
	private boolean enabled = true;
	
	public final void enable() {
		setEnabled(true);
	}
	
	public final void disable() {
		setEnabled(false);
	}
	
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	private boolean includeResolved = true;

	public final void setIncludeResolved(boolean include) {
		this.includeResolved = include;
	}
	
	public  boolean includesResolved() {
		return includeResolved;
	}

	public static <T> List<T> nullable(List<T> list) {
		return list != null ? list : Collections.emptyList();
	}
}
