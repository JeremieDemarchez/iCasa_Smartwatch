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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a device declaration in a iCASA script.
 * 
 * @author bourretp
 */
public class DeviceModel {

    private final String m_id;
    private final String m_ipojoFactory;
    private final Map<String, String> m_configuration;
    private final int m_positionX;
    private final int m_positionY;

    public DeviceModel(String id, String ipojoFactory, Map<String, String> configuration, int positionX, int positionY) {
        m_id = id;
        m_ipojoFactory = ipojoFactory;
        m_configuration = Collections.unmodifiableMap(new HashMap<String, String>(configuration));
        m_positionX = positionX;
        m_positionY = positionY;
    }

    public String getId() {
        return m_id;
    }

    public String getIpojoFactory() {
        return m_ipojoFactory;
    }

    public Map<String, String> getConfiguration() {
        return m_configuration;
    }

    public int getPositionX() {
        return m_positionX;
    }

    public int getPositionY() {
        return m_positionY;
    }

}
