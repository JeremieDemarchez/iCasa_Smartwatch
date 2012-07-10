/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
package org.medical.application.device.web.common.impl.component;

import org.medical.application.Application;

/**
 * Allow objects implementing this interface to be notified about digital service selection changes.
 * 
 * @author Thomas Leveque
 *
 */
public interface SelectedApplicationTracker {

	/**
	 * Called every time, a new service is selected.
	 * When a tracker is added, a notification is sent with oldSelectServ set to null.
	 * 
	 * @param oldSelectServ old selected service (not relevant for first notification)
	 * @param newSelectedServ new selected service
	 */
	public void notifySelectedAppChanged(Application oldSelectApp,
			Application newSelectedApp);

}
