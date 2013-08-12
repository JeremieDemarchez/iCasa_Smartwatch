/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.temperature.impl;

import fr.liglab.adele.icasa.location.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of a zone used to compute thermal properties.
 *
 * @author Thomas Leveque
 */
public class ZoneModel {

    private double _thermalCapacity;
    private Map<String, Double> _wallSurfaceMap = new HashMap<String, Double>();
    private int _totalPower;
    private Zone _zone;

    public ZoneModel(Zone zone) {
        this._zone = zone;
    }

    public double getThermalCapacity() {
        return _thermalCapacity;
    }

    public void setThermalCapacity(double thermalCapacity) {
        this._thermalCapacity = thermalCapacity;
    }

    public int getTotalPower() {
        return _totalPower;
    }

    public void setTotalPower(int totalPower) {
        this._totalPower = totalPower;
    }

    public void addPower(int powerToAdd) {
        this._totalPower += powerToAdd;
    }

    public void reducePower(int powerToRemove) {
        this._totalPower -= powerToRemove;
    }

    public double getWallSurface(String zoneId) {
        Double surface = _wallSurfaceMap.get(zoneId);
        if (surface == null)
            return 0.0d;
        else
            return surface;
    }

    public void setWallSurface(String zoneId, double wallSurface) {
        _wallSurfaceMap.put(zoneId, wallSurface);
    }

    public void removeWallSurface(String zoneId) {
        _wallSurfaceMap.remove(zoneId);
    }
}
