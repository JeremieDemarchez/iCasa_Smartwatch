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
package fr.liglab.adele.icasa.service.scheduler;

/**
 * This Interface is the parent interface allowing to schedule tasks using the
 * iCasa Clock {@link fr.liglab.adele.icasa.clock.Clock}
 */
public interface ICasaRunnable extends Runnable{
    /**
     * Gets the job's group.
     * Jobs sharing a group use the same thread pool.
     * @return the job's group name.
     */
    public String getGroup();
}
