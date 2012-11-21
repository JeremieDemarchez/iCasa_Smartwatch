package fr.liglab.adele.icasa.environment;

public interface ZoneListener {

	public void variableModified(Zone zone, String variableName, Double oldValue, Double newValue);
	
	public void moved(Zone zone);

	public void resized(Zone zone);
	
	public void parentModified(Zone zone);
	
}
