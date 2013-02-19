package fr.liglab.adele.icasa.script.executor;

public interface ScriptExecutorListener {

	public void scriptPaused(String scriptName);
	
	void scriptResumed(String scriptName);
	
	void scriptStopped(String scriptName);
	
	void scriptStarted(String scriptName);
	
}
