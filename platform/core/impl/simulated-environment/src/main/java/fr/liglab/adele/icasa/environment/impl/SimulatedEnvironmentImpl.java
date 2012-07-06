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
package fr.liglab.adele.icasa.environment.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulatedEnvironmentListener;

/**
 * Implementation of a simulated environment.
 * 
 * @author bourretp
 */
@Component
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION),
        @StaticServiceProperty(type = "java.lang.Integer", name = "leftX", mandatory=true),
        @StaticServiceProperty(type = "java.lang.Integer", name = "rightX", mandatory=true),
        @StaticServiceProperty(type = "java.lang.Integer", name = "topY", mandatory=true),
        @StaticServiceProperty(type = "java.lang.Integer", name = "bottomY", mandatory=true)})
public class SimulatedEnvironmentImpl implements SimulatedEnvironment {

    @ServiceProperty(name = SimulatedEnvironment.ENVIRONMENT_ID, mandatory = true)
    private String m_id;

    @LogConfig
    private ComponentLogger m_logger;

    private final Map<String, Double> m_properties;

    private final Map<SimulatedEnvironmentListener, Object> m_listeners = new IdentityHashMap<SimulatedEnvironmentListener, Object>();

    private final Lock m_lock;

    public SimulatedEnvironmentImpl() {
        m_lock = new ReentrantLock(true);
        m_properties = new HashMap<String, Double>();
        // Set common properties to their default values
        m_properties.put(TEMPERATURE, 0.0d);
        m_properties.put(ILLUMINANCE, 0.0d);
        m_properties.put(VOLUME, Double.POSITIVE_INFINITY);
        m_properties.put(PRESENCE, 0.0d);
        m_properties.put(NOISE, 0.0d);
    }

    @Override
    public String getEnvironmentId() {
        return m_id;
    }

    @Override
    public Set<String> getPropertyNames() {
        synchronized (m_properties) {
            return m_properties.keySet();
        }
    }

    @Override
    public Double getProperty(String key) {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        synchronized (m_properties) {
            return m_properties.get(key);
        }
    }

    @Override
    public Double setProperty(String key, Double value) {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        // Safety nets
        if (TEMPERATURE.equals(key) || ILLUMINANCE.equals(key)
                || VOLUME.equals(key) || PRESENCE.equals(key)) {
            if (value == null) {
                throw new IllegalArgumentException(key + "cannot be null");
            } else if (value.isNaN()) {
                throw new IllegalArgumentException(key + "cannot be NaN");
            } else if (value < 0.0d) {
                // Temperature cannot be below the absolute zero.
                // Set to zero and silently ignore.
                value = 0.0d;
            }
        }
        Double old;
        synchronized (m_properties) {
            old = m_properties.put(key, value);
        }
        m_logger.debug("Property '" + key + "' set to " + value);
        final Set<SimulatedEnvironmentListener> listeners;
        synchronized (m_listeners) {
            listeners = new HashSet<SimulatedEnvironmentListener>(
                    m_listeners.keySet());
        }
        // Call all listeners sequentially
        for (SimulatedEnvironmentListener listener : listeners) {
            try {
                listener.environmentPropertyChanged(key, old, value);
            } catch (Exception e) {
                m_logger.warning(
                        "Exception in simulated envoronment listener '"
                                + listener + "'", e);
            }
        }
        return old;
    }

    @Override
    public void lock() {
        m_lock.lock();
        m_logger.debug("LOCKED");
    }

    @Override
    public void unlock() {
        m_lock.unlock();
        m_logger.debug("UNLOCKED");
    }

    @Override
    public void addListener(SimulatedEnvironmentListener listener) {
        synchronized (m_listeners) {
            m_listeners.put(listener, null);
        }
    }

    @Override
    public void removeListener(SimulatedEnvironmentListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
        }
    }

}
