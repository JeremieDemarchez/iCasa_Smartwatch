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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import nextapp.echo.app.Component;

import org.medical.application.device.web.common.impl.component.FloatingButtonDragSourcePeer.DropTarget;
import org.medical.application.device.web.common.impl.component.event.DropEvent;
import org.medical.application.device.web.common.impl.component.event.DropListener;

/**
 * A container <code>Component</code> that enables its child to be dragged and
 * dropped by the user onto any <code>Component</code> registered as a drop
 * target. When a <code>Component</code> is successfully dropped onto a valid
 * drop target, a <code>DropEvent</code> is fired and all registered
 * <code>DropTargetListener</code>s are notified.
 * 
 * Note that this component API is new and may change before the release candidate (though likely minorly/not at all).
 */
public class FloatingButtonDragSource extends Component {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1577151280447721817L;
	
	public static final String DROP_LISTENERS_CHANGED_PROPERTY = "dropListeners";
    public static final String DROP_TARGETS_CHANGED_PROPERTY = "dropTargets";
    public static final String INPUT_DROP = "drop";

    /**
     * Collection of drop target components.
     */
    private List<String> dropTargetIdList = new ArrayList<String>();
    
    /**
     * Creates an empty DragSource.
     */
    public FloatingButtonDragSource() { 
        super();
    }
    
    /**
     * Creates a DragSource making the given Component visually draggable.
     *  
     * @param draggable the draggable component
     */
    public FloatingButtonDragSource(Component draggable) {
        super();
        this.add(draggable);
    }
   
    /**
     * Adds a target component to the drop target list.
     * 
     * @param dropTargetId the <code>renderId</code> of the drop target component to add
     */
    public void addDropTarget(String dropTargetId) {
        if (dropTargetIdList.indexOf(dropTargetId) != -1) {
            return;
        }
        if (dropTargetId == null) {
            throw new IllegalArgumentException("Cannot add null drop target id.");
        }
        dropTargetIdList.add(dropTargetId);
        firePropertyChange(DROP_TARGETS_CHANGED_PROPERTY, null, dropTargetId);
    }
    
    /**
     * Adds a <code>DropListener</code> to the listener list
     * 
     * @param listener the listener
     */
    public void addDropListener(DropListener listener) {
        getEventListenerList().addListener(DropListener.class, listener);
        firePropertyChange(DROP_LISTENERS_CHANGED_PROPERTY, null, listener);
    }
    
    /**
     * Notifies all listeners that have registered for this event type.
     * 
     * @param event the <code>DropEvent</code> to send
     */
    private void fireDropEvent(DropEvent event) {
        if (!hasEventListenerList()) {
            return;
        }
        EventListener[] listeners = getEventListenerList().getListeners(DropListener.class);
        for (int i=0; i<listeners.length; i++) {
            DropListener listener = (DropListener) listeners[i]; 
            listener.dropPerformed(event);
        }
    }
    
    /**
     * Returns an iterator over the render ids (Strings) of all drop targets. 
     * 
     * @return the <code>renderId</code> of the drop target component at the specified index 
     */
    public String getDropTarget(int index) {
        return (String) dropTargetIdList.get(index);
    }
    
    /**
     * Returns the total number of drop targets.
     * 
     * @return the total number drop targets
     */
    public int getDropTargetCount() {
        return dropTargetIdList.size();
    }
    
    /**
     * Determines if any <code>DropListener</code>s are currently registered.
     * 
     * @return true if any <code>DropListener</code>s are currently registered
     */
    public boolean hasDropListeners() {
        return hasEventListenerList() && getEventListenerList().getListenerCount(DropListener.class) > 0;
    }
    
    /**
     * @see nextapp.echo.app.Component#processInput(java.lang.String, java.lang.Object)
     */
    public void processInput(String name, Object value) {
        super.processInput(name, value);
        if (INPUT_DROP.equals(name)) {
        	DropTarget dropTarget = (DropTarget) value;
        	
            // Specific component provided as event value.
            Component specificComponent = (Component) dropTarget.getSpecificTarget();

            // Determine drop tagret component (will be either specificComponent or an ancestor thereof.
            Component targetComponent = specificComponent;
            while (targetComponent != null && dropTargetIdList.indexOf(targetComponent.getRenderId()) == -1) {
                targetComponent = targetComponent.getParent();
            }
            if (targetComponent == null) {
                // Unable to find registered drop target component (should not occur).
                return;
            }
            
            // Fire event.
            fireDropEvent(new DropEvent(this, targetComponent, specificComponent, dropTarget.getTargetX(), dropTarget.getTargetY()));
        }
    }
    
    /**
     * Removes all <code>Components</code> from the drop target list
     */
    public void removeAllDropTargets() {
        dropTargetIdList.clear();
        firePropertyChange(DROP_TARGETS_CHANGED_PROPERTY, null, null);
    }
    
    /**
     * Removes a target component to the drop target list.
     * 
     * @param dropTargetId the <code>renderId</code> of the drop target component to remove
     */
    public void removeDropTarget(String dropTargetId) {
        if (dropTargetIdList.remove(dropTargetId)) {
            firePropertyChange(DROP_TARGETS_CHANGED_PROPERTY, dropTargetId, null);
        }
    }
    
    /**
     * Removes a <code>DropListener</code> from the listener list
     * 
     * @param listener the listener
     */
    public void removeDropListener(DropListener listener) {
        if (!hasEventListenerList()) {
            return;
        }
        getEventListenerList().removeListener(DropListener.class, listener);
        firePropertyChange(DROP_LISTENERS_CHANGED_PROPERTY, listener, null);
    }
}
