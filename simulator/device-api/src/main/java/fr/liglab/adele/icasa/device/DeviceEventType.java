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
package fr.liglab.adele.icasa.device;

/**
 *
 *
 * @author Thomas Leveque
 */
public enum DeviceEventType {

    ADDED("added"), REMOVED("removed"), PROP_ADDED("prop-added"), PROP_REMOVED("prop-removed"), PROP_MODIFIED("prop-modified");

    private String _eventTypeStr;

    private DeviceEventType(String eventTypeStr) {
        _eventTypeStr = eventTypeStr;
    }

    public String toString() {
        return _eventTypeStr;
    }
}
