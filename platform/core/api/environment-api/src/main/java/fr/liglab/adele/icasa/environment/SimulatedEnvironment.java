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

import java.util.Set;

/**
 * Service definition of a fake environment used by simulated devices.
 * 
 * @author bourretp
 */
public interface SimulatedEnvironment {

    /**
     * The <code>SimulatedEnvironment</code> service property that contains the
     * unique identifier of the simulated environment. This property is
     * <em>mandatory</em> and must have a <em>unique</em> value. The type of the
     * value must be <code>String</code>.
     * <p>
     * It is an error to have two <code>SimulatedEnvironment</code> services
     * registered with the same identifier.
     * </p>
     */
    String ENVIRONMENT_ID = "environment.id";

    // TODO
    String TEMPERATURE = "temperature";
    String ILLUMINANCE = "illuminance";
    String VOLUME = "volume";
    String PRESENCE = "presence";
    String NOISE = "noise";

    /**
     * Return the identifier of this simulated environment.
     * 
     * @return the identifier of this simulated environment.
     * @see #ENVIRONMENT_ID
     */
    String getEnvironmentId();

    /**
     * Return the list of names of all the properties defined in this simulated
     * environment.
     * 
     * @return the list of names of all the properties defined in this simulated
     *         environment.
     */
    Set<String> getPropertyNames();

    /**
     * Get the value of the simulated environment property with the specified
     * name.
     * 
     * @param key
     *            the name of the property.
     * @return the value of the simulated environment property with the
     *         specified name, or <code>null</code> if the property is
     *         undefined.
     */
    Double getProperty(String key);

    /**
     * Set the value of the simulated environment property with the specified
     * name.
     * 
     * @param key
     *            the name of the property to change.
     * @param value
     *            the new value of the property.
     * @return the old value of the property, or <code>null</code> if the
     *         property was undefined.
     */
    Double setProperty(String key, Double value);
    
    /**
     * Lock the environment, so the current thread gains exclusive access to
     * this environment.
     */
    void lock();

    /**
     * Unlock the environment, so the current thread loses its exclusive access
     * to this environment.
     */
    void unlock();
    
    /**
     * TODO comments.
     * 
     * @param listener
     */
    void addListener(SimulatedEnvironmentListener listener);
    
    /**
     * TODO comments.
     * 
     * @param listener
     */
    void removeListener(SimulatedEnvironmentListener listener);

}
