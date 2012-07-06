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
package org.medical.application.device.dashboards.impl.component;

import java.util.Iterator;
import java.util.StringTokenizer;

import nextapp.echo.app.Component;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.util.Context;
import nextapp.echo.extras.webcontainer.service.CommonService;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.UserInstance;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;

/**
 * Synchronization peer for <code>FloatingButtonDragSource</code>es.
 */
public class FloatingButtonDragSourcePeer extends AbstractComponentSynchronizePeer {

    public class DropTarget {
    	
    	public DropTarget(Component specificTarget, int targetX, int targetY) {
    		this.specificTarget = specificTarget;
    		this.targetX = targetX;
    		this.targetY = targetY;
    	}
    	
    	private Component specificTarget;
    	private int targetX;
    	private int targetY;
    	
    	public Component getSpecificTarget() {
            return this.specificTarget;
        }
        
        public int getTargetX() {
    		return targetX;
    	}

    	public int getTargetY() {
    		return targetY;
    	}
	}

	private static final Service FLOATING_BUTTON_DRAG_SOURCE_SERVICE = JavaScriptService.forResources("Echo.FloatingButtonDragSource",
            new String[] {  "FloatingButtonDragSource.js",  
                            "Sync.FloatingButtonDragSource.js"});
    
    private static final String DROP_TARGET_IDS = "dropTargetIds";
    
    static {
        WebContainerServlet.getServiceRegistry().add(FLOATING_BUTTON_DRAG_SOURCE_SERVICE);
    }
    
    /**
     * Default constructor.
     */
    public FloatingButtonDragSourcePeer() {
        super();
        
        addOutputProperty(DROP_TARGET_IDS, true);
        
        addEvent(new AbstractComponentSynchronizePeer.EventPeer(FloatingButtonDragSource.INPUT_DROP, 
        		FloatingButtonDragSource.DROP_LISTENERS_CHANGED_PROPERTY, String.class) {
            public boolean hasListeners(Context context, Component component) {
                return ((FloatingButtonDragSource) component).hasDropListeners();
            }
            
            public void processEvent(Context context, Component component, Object eventData) {
                ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
                UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
                
                // deserialize data
                String str = (String) eventData;
            	str = str.trim();
                StringTokenizer st = new StringTokenizer(str, "###");
                int count = 0;
                String[] values = new String[3];
                for (int i = 0; i < values.length && st.hasMoreTokens(); ++i) {
                    values[i] = st.nextToken();
                    ++count;
                }
                if (count !=3)
                	return;
                
                Component specificComponent = userInstance.getComponentByClientRenderId(values[0]);
                int targetX = Integer.parseInt(values[1]);
                int targetY = Integer.parseInt(values[2]);
                clientUpdateManager.setComponentAction(component, FloatingButtonDragSource.INPUT_DROP, new DropTarget(specificComponent, targetX, targetY));
            }
        });
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getClientComponentType(boolean)
     */
    public String getClientComponentType(boolean mode) {
        return "Echo.FloatingButtonDragSource";
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getComponentClass()
     */
    public Class getComponentClass() {
        return FloatingButtonDragSource.class;
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getOutputProperty(
     *      nextapp.echo.app.util.Context, nextapp.echo.app.Component, java.lang.String, int)
     */
    public Object getOutputProperty(Context context, Component component, String propertyName, int propertyIndex) {
        if (propertyName.equals(DROP_TARGET_IDS)) {
        	FloatingButtonDragSource dragSource = (FloatingButtonDragSource) component;
            UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
            String dropTargetId = dragSource.getDropTarget(propertyIndex);
            Component dropTarget = (Component) component.getApplicationInstance().getComponentByRenderId(dropTargetId);
            if (dropTarget == null) {
                return null;
            } else {
                return userInstance.getClientRenderId(dropTarget);
            }
        } else {
            return super.getOutputProperty(context, component, propertyName, propertyIndex);
        }
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getOutputPropertyIndices(nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component, java.lang.String)
     */
    public Iterator getOutputPropertyIndices(Context context, Component component, String propertyName) {
        if (propertyName.equals(DROP_TARGET_IDS)) {
        	FloatingButtonDragSource dragSource = (FloatingButtonDragSource) component;
            final int count = dragSource.getDropTargetCount();
            return new Iterator() {
                int i = 0;
                
                /**
                 * @see java.util.Iterator#hasNext()
                 */
                public boolean hasNext() {
                    return i < count;
                }
                
                /**
                 * @see java.util.Iterator#next()
                 */
                public Object next() {
                    return new Integer(i++);
                }
                
                /**
                 * @see java.util.Iterator#remove()
                 */
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            return super.getOutputPropertyIndices(context, component, propertyName);
        }
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(nextapp.echo.app.util.Context, Component)
     */
    public void init(Context context, Component component) {
        super.init(context, component);
        ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
        serverMessage.addLibrary(CommonService.INSTANCE.getId());
        serverMessage.addLibrary(FLOATING_BUTTON_DRAG_SOURCE_SERVICE.getId());
    }
}