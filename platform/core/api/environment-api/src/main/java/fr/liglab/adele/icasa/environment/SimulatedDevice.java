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
package fr.liglab.adele.icasa.environment;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service interface that is must be provided by simulated device
 * implementations, so they can be bound to a simulated environment service.
 * 
 * @author bourretp
 */
public interface SimulatedDevice extends GenericDevice {

    /**
     * Return the identifier of the simulated environment currently bound to
     * this device.
     * 
     * @return the identifier of the simulated environment currently bound to
     *         this device, or {@code null} if no environment is currently
     *         bound.
     * @see SimulatedEnvironment#ENVIRONMENT_ID
     */
    String getEnvironmentId();

    /**
     * Bind this simulated device to the given simulated environment.
     * 
     * @param environment
     *            the binding simulated environment
     */
    void bindSimulatedEnvironment(SimulatedEnvironment environment);

    /**
     * Unbind this simulated device from the given simulated environment.
     * 
     * @param environment
     *            the unbinding simulated environment
     */
    void unbindSimulatedEnvironment(SimulatedEnvironment environment);

}
