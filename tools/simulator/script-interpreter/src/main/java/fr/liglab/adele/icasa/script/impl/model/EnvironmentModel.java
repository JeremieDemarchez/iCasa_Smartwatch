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
package fr.liglab.adele.icasa.script.impl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of an environment declaration in a iCASA script.
 * 
 * @author bourretp
 */
public class EnvironmentModel {

    private final String m_id;
    private final String m_description;
    private final Map<String, Double> m_properties;
    private final List<DeviceModel> m_devices;
    private final int m_topLeftX;
    private final int m_topLeftY;
    private final int m_bottomRightX;
    private final int m_bottomRightY;

    public EnvironmentModel(String id, String description,
            Map<String, Double> properties, List<DeviceModel> devices,
            int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        m_id = id;
        m_description = description;
        m_properties = Collections.unmodifiableMap(new HashMap<String, Double>(properties));
        m_devices = Collections.unmodifiableList(new ArrayList<DeviceModel>(devices));
        m_topLeftX = topLeftX;
        m_topLeftY = topLeftY;
        m_bottomRightX = bottomRightX;
        m_bottomRightY = bottomRightY;
    }

    public String getId() {
        return m_id;
    }
    
    public String getDescription() {
        return m_description;
    }

    public Map<String, Double> getProperties() {
        return m_properties;
    }
    
    public List<DeviceModel> getDeclaredDevices() {
        return m_devices;
    }
    
    public int getTopLeftX() {
        return m_topLeftX;
    }

    public int getTopLeftY() {
        return m_topLeftY;
    }
    
    public int getBottomRightX() {
        return m_bottomRightX;
    }

    public int getBottomRightY() {
        return m_bottomRightY;
    }

}
