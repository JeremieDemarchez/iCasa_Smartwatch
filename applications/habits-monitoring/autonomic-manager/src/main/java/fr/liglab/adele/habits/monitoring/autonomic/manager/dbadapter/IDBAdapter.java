/**
 * 
 */
package fr.liglab.adele.habits.monitoring.autonomic.manager.dbadapter;

import java.util.Set;

/**
 * Interface for the autonomic manager database adapter.
 * @author Kettani Mehdi.
 *
 */
public interface IDBAdapter {

	/**
	 * Get deployment package id associated with the interface list given as parameter. 
	 * @param interfaceSet
	 * @return
	 */
	public String getDeviceAdapterId(Set<String> interfaceSet);
	
	/**
	 * Get the deployment package url related to the id given as parameter.
	 * @param dpId the dpid whose url need to be fetched.
	 * @return url of dp as string.
	 */
	public Object getDeviceAdapterUrl(String dpId);
}
