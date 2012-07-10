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

import nextapp.echo.app.Button;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FloatingPane;
import nextapp.echo.app.ImageReference;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class FloatingButton extends Button implements FloatingPane {

    public static final String PROPERTY_POSITION_X = "positionX";
    public static final String PROPERTY_POSITION_Y = "positionY";

    /**
     * @Generated
     */
    private static final long serialVersionUID = 4268834983342304405L;

    /**
     * Create a new {@code DeviceSpot}, initially transparent.
     * 
     * @param x
     * @param y
     */
    public FloatingButton(final int x, final int y, final ImageReference icon,
            final String toolTipText) {
        super();
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive");
        }
        if (y < 0) {
            throw new IllegalArgumentException("y must be positive");
        }
        set(PROPERTY_POSITION_X, new Extent(x));
        set(PROPERTY_POSITION_Y, new Extent(y));
        setIcon(icon);
        setToolTipText(toolTipText);
    }
    
    public void setPosition(int x, int y) {
    	set(PROPERTY_POSITION_X, new Extent(x));
        set(PROPERTY_POSITION_Y, new Extent(y));
    }
    
    

}
