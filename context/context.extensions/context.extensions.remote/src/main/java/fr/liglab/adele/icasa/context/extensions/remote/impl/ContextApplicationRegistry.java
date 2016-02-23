package fr.liglab.adele.icasa.context.extensions.remote.impl;

import java.util.List;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.handlers.dependency.Dependency;

/**
 * This class allows to query the platform about the deployed applications currently using context
 * services
 * 
 * @author vega
 *
 */
public interface ContextApplicationRegistry {

	/**
	 * The list of deployed application factories
	 */
	public List<String> getFactories();

	/**
	 * The running instances of a given application
	 */
	public List<String> getInstances(String factoryId);

	/**
	 * The current state of the specified application
	 * 
	 * NOTE notice that this is a snapshot of the state taken at invocation time that is not automatically
	 * updated
	 */
	public Application getInstance(String factoryId, String instanceId);

	/**
	 * The state of a ruuning application
	 * 
	 * @author vega
	 *
	 */
	public interface Application {

		/**
		 * The name of the application instance
		 */
		public String getName();

		/**
		 * The list of context service requirements
		 */
		public List<Requirement> getRequirements();

		/**
		 * The current  of the application
		 * 
		 * @see ComponentInstance#getState()
		 * 
		 */
		public State getState();

		/**
		 * The component possible statuses
		 *
		 */
		public enum State {

			UNKNOWN(-2),

			DISPOSED(ComponentInstance.DISPOSED),

			STOPPED(ComponentInstance.STOPPED),

			INVALID(ComponentInstance.INVALID),

			VALID(ComponentInstance.VALID);

			private final int value;

			private State(int value) {
				this.value = value;
			}

			public static State valueOf(int value) {
				for (State state : values()) {
					if (state.value == value) {
						return state;
					}
				}

				return UNKNOWN;
			}
		}

	}

	/**
	 * The context requirements of an application
	 * 	 *
	 */
	public interface Requirement {

		/**
		 * The identification of the dependency
		 */
		public String getId();

		/**
		 * The required specification
		 */
		public String getSpecification();
		
		/**
		 * Whether the dependency is optional or mandatory
		 */
		public boolean isOptional();

		/**
		 * Whether the dependecny requires multiple service providers
		 */
		public boolean isAggregate();

		/**
		 * The current resolution status of the dependency
		 */
		public State getState();

		
		/**
		 * The resolution statuses
		 *
		 */
		public enum State {

			UNKNOWN(-2),

			BROKEN(Dependency.BROKEN),

			UNRESOLVED(Dependency.UNRESOLVED),

			RESOLVED(Dependency.RESOLVED);

			private final int value;

			private State(int value) {
				this.value = value;
			}

			public static State valueOf(int value) {
				for (State state : values()) {
					if (state.value == value) {
						return state;
					}
				}

				return UNKNOWN;
			}
		}

	}

}
