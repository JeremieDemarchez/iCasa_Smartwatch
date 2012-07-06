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
package fr.liglab.adele.icasa.context;

import java.util.EventObject;

public final class ContextEvent extends EventObject {
    private static final long serialVersionUID = -4933860505093444196L;

    /**
     * This context has been created.
     * <p>
     * This event is synchronously delivered <strong>after</strong> the context
     * has been created.
     * </p>
     * 
     * @see Context#createChild(String)
     */
    public final static int CREATED = 0x00000001;

    /**
     * A property of this context has been modified.
     * <p>
     * This event is synchronously delivered <strong>after</strong> the context
     * property has been modified.
     * </p>
     * 
     * @see Context#setProperty(String, Object)
     */
    public final static int MODIFIED = 0x00000002;

    /**
     * This context has been removed.
     * <p>
     * This event is synchronously delivered <strong>after</strong> the context
     * has been removed. So the context has already been flushed.
     * </p>
     * 
     * @see Context#removeChild(String)
     */
    public final static int REMOVED = 0x00000004;

    private final int m_type;
    private final Context m_context;
    private final String m_propertyName;
    private final Object m_propertyOldValue;
    private final Object m_propertyNewValue;

    public ContextEvent(final int type, final Context context,
            final String propertyName, final Object propertyOldValue,
            final Object propertyNewValue) {
        super(context);
        m_type = type;
        m_context = context;
        m_propertyName = propertyName;
        m_propertyOldValue = propertyOldValue;
        m_propertyNewValue = propertyNewValue;
    }

    public int getType() {
        return m_type;
    }

    public Context getContext() {
        return m_context;
    }

    public String getPropertyName() {
        return m_propertyName;
    }

    public Object getPropertyOldValue() {
        return m_propertyOldValue;
    }

    public Object getPropertyNewValue() {
        return m_propertyNewValue;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[type=");
        switch (m_type) {
        case CREATED:
            sb.append("CREATED");
            break;
        case MODIFIED:
            sb.append("MODIFIED");
            break;
        case REMOVED:
            sb.append("REMOVED");
            break;
        default:
            sb.append("???");
            break;
        }
        sb.append(", context=");
        sb.append(m_context.getAbsoluteName());
        if (m_type == MODIFIED) {
            sb.append(", propertyName=");
            sb.append(m_propertyName);
            sb.append(", propertyOldValue=");
            sb.append(m_propertyOldValue);
            sb.append(", propertyNewValue=");
            sb.append(m_propertyNewValue);
        }
        sb.append("]");
        return sb.toString();
    }
}
