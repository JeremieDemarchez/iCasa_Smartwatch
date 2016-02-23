package fr.liglab.adele.icasa.context.extensions.remote.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;

@Component
@Instantiate
@Provides(specifications=ContextApplicationRegistry.class)
public class ContextApplicationRegistryImpl implements ContextApplicationRegistry {

    @Requires(specification=Factory.class, filter="(icasa.application=*)", optional=true, proxy=false)
    List<Factory> applicationFactories;

	@Override
	public List<String> getFactories() {
		return applicationFactories.stream().map(Factory::getName).collect(Collectors.toList());
 	}

	@Override
	public List<String> getInstances(String factoryId) {
		
		Optional<Factory> factory	= applicationFactories.stream()
													.filter(candidate -> candidate.getName().equals(factoryId))
													.findAny();
		if (!factory.isPresent()) {
			return null;
		}
		
		List<String> instanceIds 	= factory.get().getInstances().stream()
												.map(ComponentInstance::getInstanceName)
												.collect(Collectors.toList());
												
		return instanceIds;
	}

	@Override
	public Application getInstance(String factoryId, String instanceId) {
		
		Optional<Factory> factory				= applicationFactories.stream()
														.filter(candidate -> candidate.getName().equals(factoryId))
														.findAny();
		
		if (!factory.isPresent()) {
			return null;
		}
		
		Optional<ComponentInstance> instance	= factory.get().getInstances().stream()
														.filter(candidate-> candidate.getInstanceName().equals(instanceId))
														.findAny();
		
		if (!instance.isPresent()) {
			return null;
		}
		
		return new ApplicationDescription(instance.get());
	}

	/**
	 * A snapshot of the current state of the underlying iPOJO component
	 *
	 */
	private static class ApplicationDescription implements Application {

		private final String name;
		private final State status;
		private final List<Requirement> requirements;
		
		public ApplicationDescription(ComponentInstance instance) {
			
			this.name 			= instance.getInstanceName();
			this.status			= State.valueOf(instance.getState());
	        this.requirements	= new ArrayList<>();

	        /*
	         * search for context dependencies
	         */
	        for (HandlerDescription handlerDescription  : instance.getInstanceDescription().getHandlers()) {
	        	if (handlerDescription instanceof DependencyHandlerDescription) {
	        		for (DependencyDescription  dependencyDescription : ((DependencyHandlerDescription)handlerDescription).getDependencies()) {
	        			Dependency dependency 	= dependencyDescription.getDependency();
	        			Class<?> service 		= dependency.getSpecification();
						if (service.isAnnotationPresent(ContextService.class)) {
							requirements.add(new RequirementDescription(dependency));
						}
					}
	        	}
			}
			
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public List<Requirement> getRequirements() {
			return requirements;
		}

		@Override
		public State getState() {
			return status;
		}
		
	}
	
	/**
	 * A snapshot of the state of the underlying iPOJO dependency
	 * 
	 * @author vega
	 *
	 */
	private static class RequirementDescription implements Requirement {

		private final String id;
		private final String specification;
		private final boolean optional;
		private final boolean aggregate;
		private final State status;
		
		public RequirementDescription(Dependency dependency) {
			this.id				= dependency.getId();
			this.specification	= dependency.getSpecification().getName();
			this.optional		= dependency.isOptional();
			this.aggregate		= dependency.isAggregate();
			this.status			= State.valueOf(dependency.getState());
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getSpecification() {
			return specification;
		}

		@Override
		public boolean isOptional() {
			return optional;
		}

		@Override
		public boolean isAggregate() {
			return aggregate;
		}

		@Override
		public State getState() {
			return status;
		}

	}
	
}
