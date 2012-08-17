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
package fr.liglab.adele.icasa.context.impl;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.context.Context;
import fr.liglab.adele.icasa.context.ContextEvent;
import fr.liglab.adele.icasa.context.ContextListener;
import fr.liglab.adele.icasa.context.ContextService;

/**
 * Implementation of the {@code ContextService}.
 * 
 * @author bourretp
 */
@Component
@Instantiate
@Provides
public class ContextServiceImpl implements ContextService {

    @LogConfig
    private ComponentLogger m_logger;

    /**
     * The root context of this context service.
     */
    final Context m_rootContext = new ContextImpl(this);

    /**
     * The context listeners associated with their listened context path.
     * 
     * <p>
     * {@code IdentityHashMap} is used so the map do not rely on the
     * {@code ContextListener.equals()} user-defined implementations.
     * </p>
     */
    private final Map<ContextListener, ContextListenerDescription> m_listeners = new IdentityHashMap<ContextListener, ContextListenerDescription>();

    @Override
    public Context getRootContext() {
        return m_rootContext;
    }

    @Override
    public void addContextListener(ContextListener listener, String pattern) {
        if (listener == null) {
            throw new NullPointerException("listener");
        } else if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        final ContextListenerDescription description = new ContextListenerDescription();
        if (pattern.endsWith(CONTEXT_PATH_SEPARATOR + CONTEXT_PATH_WILDCARD)) {
            // Context path ends with "/*" => wildcard
            description.m_prefix = pattern.substring(0, pattern.length() - 1);
            description.m_isWildcard = true;
        } else {
            // No wildcard
            if (pattern.endsWith(CONTEXT_PATH_SEPARATOR)) {
                throw new IllegalArgumentException("contextPath cannot ends with '" + CONTEXT_PATH_SEPARATOR + "'");
            }
            description.m_prefix = pattern + CONTEXT_PATH_SEPARATOR;
            description.m_isWildcard = false;
        }
        if (!description.m_prefix.startsWith(CONTEXT_PATH_SEPARATOR)) {
            throw new IllegalArgumentException("contextPath must begin with '" + CONTEXT_PATH_SEPARATOR + "'");
        } else if (description.m_prefix.contains(CONTEXT_PATH_SEPARATOR + CONTEXT_PATH_SEPARATOR)) {
            throw new IllegalArgumentException("contextPath contains an empty child name");
        } else if (description.m_prefix.contains(CONTEXT_PATH_WILDCARD)) {
            throw new IllegalArgumentException("contextPath cannot contain inner '" + CONTEXT_PATH_WILDCARD + "'");
        }
        synchronized (m_listeners) {
            m_listeners.put(listener, description);
        }
    }

    @Override
    public void removeContextListener(ContextListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        synchronized (m_listeners) {
            m_listeners.remove(listener);
        }
    }

    void notifyListeners(ContextEvent event) {
        m_logger.debug("Notifying context event " + event);
        synchronized (m_listeners) {
            for (Entry<ContextListener, ContextListenerDescription> e : m_listeners.entrySet()) {
                if (e.getValue().matches(event)) {
                    e.getKey().contextChanged(event);
                } else {
                }
            }
        }
    }

    private static class ContextListenerDescription {
        private String m_prefix;
        private boolean m_isWildcard;

        public boolean matches(ContextEvent event) {
            final String absoluteName = event.getContext().getAbsoluteName();
            return m_isWildcard ? absoluteName.startsWith(m_prefix) : absoluteName.equals(m_prefix);
        }
    }

}
