/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.model.temperature;


import fr.liglab.adele.icasa.location.Zone;


/**
 * Model of a zone used to compute thermal properties.
 *
 */
public class ZoneModel {

    private final Zone myZone;

    public ZoneModel(Zone zone) {
        this.myZone = zone;
    }

    public String getZoneId() {
        return myZone.getZoneName();
    }

    public double getThermalCapacity() {
        double newVolume = 2.5d; // use this value as default to avoid divide by zero
        double zoneVolume =  (myZone.getYLength()*myZone.getXLength()*myZone.getZLength());
        if (zoneVolume > 0.0d){
            newVolume =  zoneVolume;
        }
        return TemperaturePMImpl.AIR_MASS * TemperaturePMImpl.AIR_MASS_CAPACITY * newVolume;

    }
}
