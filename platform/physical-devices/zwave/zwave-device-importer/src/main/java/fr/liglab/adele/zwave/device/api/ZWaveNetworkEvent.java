package fr.liglab.adele.zwave.device.api;

/**
 * The basic information concerning network configuration events
 *
 */
public class ZWaveNetworkEvent {
	
	/**
	 * The type of event
	 *
	 */
	public enum Type {
		INCLUSION,
		EXCLUSION
	}

	public Type type;
	
	public long timeStamp;
	
	/**
	 * The concerned node 
	 */
	public int homeId;
	
	public int nodeId;
	
	/**
	 * Device identification
	 */
	public int manufacturerId;
	
	public int deviceType;
	
	public int deviceId;
}
