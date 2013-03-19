package fr.liglab.adele.icasa.simulator.script.executor;

public interface ScriptExecutorListener {

	/**
	 * Notification when a new script has been deployed in the platform
	 * @param scriptName the Script Name
	 */
	void scriptAdded(String scriptName);
	
	/**
	 * Notification when a existing script has been removed from the platform
	 * @param scriptName the Script Name
	 */
	void scriptRemoved(String scriptName);
	
	/**
	 * Notification when a existing script has been modified
	 * @param scriptName the Script Name
	 */
	void scriptUpdated(String scriptName);
	
	/**
	 * Notification when a script being executed is paused 
	 * @param scriptName the Script Name
	 */
	void scriptPaused(String scriptName);
	
	/**
	 * Notification when a paused script is resumed
	 * @param scriptName the Script Name
	 */
	void scriptResumed(String scriptName);
	
	/**
	 * Notification when a script being executed is stopped 
	 * @param scriptName the Script Name
	 */
	void scriptStopped(String scriptName);
	
	/**
	 * Notification when a script is started (execution launched)
	 * @param scriptName
	 */
	void scriptStarted(String scriptName);
	
}
