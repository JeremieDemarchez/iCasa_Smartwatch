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
package org.medical.application;

import org.medical.common.Identifiable;
import org.medical.common.ProgressMonitor;

/**
 * This interface represents an application.
 * An installed application should provide one service which implements this interface.
 * 
 * @author Thomas Leveque
 *
 */
public interface ApplicationProvider {

	/**
	 * Starts this application.
	 * Resources for this application must not be allocated before it has started. 
	 * 
	 * @param monitor a operation progress monitor (may be null)
	 */
	public void start(ProgressMonitor monitor);
	
	/**
	 * Stops this application.
	 * Allocated resources must be clean up before returning. 
	 * 
	 * @param monitor a operation progress monitor (may be null)
	 */
	public void stop(ProgressMonitor monitor);
	
	/**
	 * Resume this application if it has been paused.
	 * 
	 * @param monitor a operation progress monitor (may be null)
	 */
	public void resume(ProgressMonitor monitor);
	
	/**
	 * Stop application computation temporary.
	 * All computation must be stopped or paused.
	 * In particular, there should not be any messages sent from this digital application.
	 * However, incoming messages may still arrive.
	 * Allocated resource could remain in memory.
	 * 
	 * @param monitor a operation progress monitor (may be null)
	 */
	public void pause(ProgressMonitor monitor);
	
}
