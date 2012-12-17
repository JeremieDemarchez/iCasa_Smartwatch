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
package fr.liglab.adele.icasa.simulator.listener;

import fr.liglab.adele.icasa.simulator.Zone;

/**
 * @author Thomas Leveque
 */
public interface ZonePropListener extends IcasaListener {

    public void zoneVariableAdded(Zone zone, String variableName);

    public void zoneVariableRemoved(Zone zone, String variableName);

    public void zoneVariableModified(Zone zone, String variableName, Object oldValue);
}
