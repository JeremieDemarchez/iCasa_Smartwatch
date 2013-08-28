/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.application;

/**
 * Tracker of available applications.
 * After addition of a tracker, an event is generated for all existing applications.
 * 
 * @author Thomas Leveque
 *
 */
public interface ApplicationTracker {

	/**
	 * Called when an application arrives.
	 * 
	 * @param app an application
	 */
	public void addApplication(Application app);
	
	/**
	 * Called when an application is removed.
	 * 
	 * @param app an application
	 */
	public void removeApplication(Application app);
	
	public void deploymentPackageAdded(Application app, String symbolicName);
	
	public void deploymentPackageRemoved(Application app, String symbolicName);
	
	public void bundleAdded(Application app, String symbolicName);
	
	public void bundleRemoved(Application app, String symbolicName);
	
}
