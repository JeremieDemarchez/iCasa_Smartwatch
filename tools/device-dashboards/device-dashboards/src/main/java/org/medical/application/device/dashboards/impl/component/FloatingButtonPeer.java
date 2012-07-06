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

import nextapp.echo.app.Component;
import nextapp.echo.app.button.AbstractButton;
import nextapp.echo.app.util.Context;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class FloatingButtonPeer extends AbstractComponentSynchronizePeer {

    /** The associated client-side JavaScript module <code>Service</code>. */
    private static final Service FLOATING_BUTTON_SERVICE = JavaScriptService
            .forResources("Echo.FloatingButton", new String[] {
                    "FloatingButton.js", "Sync.FloatingButton.js" });

    static {
        WebContainerServlet.getServiceRegistry().add(FLOATING_BUTTON_SERVICE);
    }

    /**
     * Default constructor.
     */
    public FloatingButtonPeer() {
        super();

        addEvent(new AbstractComponentSynchronizePeer.EventPeer("action",
                AbstractButton.ACTION_LISTENERS_CHANGED_PROPERTY) {
            public boolean hasListeners(Context context, Component component) {
                return ((AbstractButton) component).hasActionListeners();
            }
        });
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(Context,
     *      Component)
     */
    public void init(Context context, Component component) {
        super.init(context, component);
        ServerMessage serverMessage = (ServerMessage) context
                .get(ServerMessage.class);
        serverMessage.addLibrary(FLOATING_BUTTON_SERVICE.getId());
    }

    /**
     * @see nextapp.echo.webcontainer.sync.component.AbstractButtonPeer#getClientComponentType(boolean)
     */
    public String getClientComponentType(boolean mode) {
        return mode ? "FB" : "FloatingButton";
    }

    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getComponentClass()
     */
    public Class<FloatingButton> getComponentClass() {
        return FloatingButton.class;
    }
}
