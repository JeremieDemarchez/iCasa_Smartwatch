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

import static fr.liglab.adele.icasa.context.ContextEvent.CREATED;
import static fr.liglab.adele.icasa.context.ContextEvent.MODIFIED;
import static fr.liglab.adele.icasa.context.ContextEvent.REMOVED;
import static fr.liglab.adele.icasa.context.ContextService.CONTEXT_PATH_SEPARATOR;
import static fr.liglab.adele.icasa.context.ContextService.CONTEXT_PATH_WILDCARD;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.liglab.adele.icasa.context.Context;
import fr.liglab.adele.icasa.context.ContextEvent;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class ContextImpl implements Context {

    private final ContextServiceImpl m_service;
    private final String m_name;
    private final String m_absoluteName;
    private final ContextImpl m_parent;
    private final Map<String, Object> m_properties;
    private final Map<String, ContextImpl> m_children;

    /**
     * TODO comments.
     * 
     * @param service
     */
    public ContextImpl(ContextServiceImpl service) {
        m_service = service;
        m_name = "";
        m_parent = null;
        m_properties = new HashMap<String, Object>();
        m_children = new HashMap<String, ContextImpl>();
        m_absoluteName = CONTEXT_PATH_SEPARATOR;
    }

    /**
     * TODO comments.
     * 
     * @param service
     * @param name
     * @param parent
     */
    public ContextImpl(ContextServiceImpl service, String name, ContextImpl parent) {
        // Ensure that the name is valid and parent is defined.
        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Invalid context name : '" + name + "'");
        } else if (parent == null) {
            throw new IllegalArgumentException("Context must have a parent");
        }
        m_service = service;
        m_name = name;
        m_parent = parent;
        m_properties = new HashMap<String, Object>();
        m_children = new HashMap<String, ContextImpl>();
        m_absoluteName = m_parent.m_absoluteName + m_name + CONTEXT_PATH_SEPARATOR;
    }

    // ======================================================

    @Override
    public Context getParent() {
        return m_parent;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String getAbsoluteName() {
        return m_absoluteName;
    }

    @Override
    public synchronized Map<String, Context> getChildren() {
        return Collections.unmodifiableMap(new HashMap<String, Context>(m_children));
    }

    @Override
    public synchronized Context getChild(String name) {
        return m_children.get(name);
    }

    @Override
    public synchronized Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(m_properties));
    }

    @Override
    public synchronized Object getProperty(String name) {
        return m_properties.get(name);
    }

    // ======================================================

    @Override
    public synchronized Context createChild(String name) {
        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Invalid name : '" + name + "'");
        }
        final ContextImpl child;
        if (m_properties.containsKey(name) || m_children.containsKey(name)) {
            throw new IllegalArgumentException("Cannot create child '" + name
                    + "' : a property/child with the same name exists");
        }
        child = new ContextImpl(m_service, name, this);
        m_children.put(name, child);
        m_service.notifyListeners(new ContextEvent(CREATED, child, null, null, null));
        return child;
    }

    @Override
    public synchronized void removeChild(String name) {
        final ContextImpl child = m_children.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Cannot remove child '" + name + "' : not such child");
        }
        synchronized (child) {
            // Remove all the children of the child to remove.
            for (String grandChildName : child.m_children.keySet().toArray(new String[0])) {
                child.removeChild(grandChildName);
            }
            // Remove all the properties of the child to remove.
            for (String childPropertyName : child.m_properties.keySet().toArray(new String[0])) {
                child.setProperty(childPropertyName, null);
            }
            // Notify the child removal.
            m_children.remove(name);
            m_service.notifyListeners(new ContextEvent(REMOVED, child, null, null, null));
        }
    }

    // ======================================================

    @Override
    public synchronized Object setProperty(String name, Object value) {
        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Invalid name : '" + name + "'");
        } else if (value != null && value instanceof Context) {
            throw new IllegalArgumentException("Cannot use context as value for property '" + name + "'");
        }
        if (m_children.containsKey(name)) {
            throw new IllegalArgumentException("Cannot set value for '" + name
                    + "' : a child with the same name exists");
        }
        final Object oldValue;
        if (value != null) {
            oldValue = m_properties.put(name, value);
        } else {
            oldValue = m_properties.remove(name);
        }
        // Notify the property change.
        m_service.notifyListeners(new ContextEvent(MODIFIED, this, name, oldValue, value));
        return oldValue;
    }

    // ======================================================

    /**
     * TODO comments.
     * 
     * @param name
     * @return
     */
    private static boolean isNameValid(String name) {
        return (name != null) && !name.isEmpty() && !name.contains(CONTEXT_PATH_SEPARATOR)
                && !name.contains(CONTEXT_PATH_WILDCARD);
    }

}
