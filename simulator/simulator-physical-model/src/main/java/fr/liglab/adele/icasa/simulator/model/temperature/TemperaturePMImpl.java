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

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.PowerObservable;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.model.api.TemperatureModel;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.List;

@ContextEntity(services = TemperatureModel.class)
public class TemperaturePMImpl implements TemperatureModel {

    @ContextEntity.State.Field(service = TemperatureModel.class,state = TemperatureModel.CURRENT_TEMPERATURE,value = "293.15")
    public double currentTemperature;

    @Override
    public double getCurrentTemperature() {
        return 0;
    }

    /**
     * Define constants to compute the value of the thermal capacity
     */
    public static final double AIR_MASS_CAPACITY = 1000; // mass capacity of the air in J/(Kg.K)
    public static final double AIR_MASS = 1.2; // mass of the air in Kg/m^3
    public static final double HIGHEST_TEMP = 303.16;
    public static final double LOWER_TEMP = 283.16;
    public static final double DEFAULT_TEMP_VALUE = 293.15; // 20 celsius degrees in kelvin

    public static final double DEFAULT_MAX_POWER = 1000;

    private volatile long m_lastUpdateTime;

    @Requires
    private Clock _clock;

    @Requires(specification = Zone.class)
    Zone zoneAttached;

    @Requires(specification = Cooler.class)
    List<Cooler> coolersInZone;

    @Requires(specification = Zone.class)
    List<Heater> heatersInZone;

    ZoneModel zoneModel;


    /**
     * Computes the temperature property value of specified zone according to time difference from the last computation.
     *
     * @param timeDiff time difference in ms from the last computation
     * @return the temperature computed for time t + dt
     */
    private void computeTemperature(long timeDiff) {


        double newTemperature = DEFAULT_TEMP_VALUE; // 20 degrees by default

        double powerLevelTotal = getPowerInZone();
        double timeDiffInSeconds = timeDiff / 1000.0d;

        if (powerLevelTotal == 0){
            if ( currentTemperature > (DEFAULT_TEMP_VALUE + 0.5) ) {
                powerLevelTotal = -50.0;
            } else if ( currentTemperature < (DEFAULT_TEMP_VALUE - 0.5) ){
                powerLevelTotal = 50.0;
            } else {
                return;
            }
        }

        if ( (powerLevelTotal > 0) && (currentTemperature < DEFAULT_TEMP_VALUE) ) {
            powerLevelTotal = 50.0 + getPowerInZone();
        } else if( (powerLevelTotal) < 0 && (currentTemperature > DEFAULT_TEMP_VALUE) ) {
            powerLevelTotal = -50.0 + getPowerInZone();
        }

        double delta = (powerLevelTotal  * timeDiffInSeconds) / zoneModel.getThermalCapacity();

        newTemperature = currentTemperature  + delta;


        /**
         * Clipping function to saturate the temperature at a certain level
         */
        if (newTemperature > HIGHEST_TEMP)
            currentTemperature = HIGHEST_TEMP;
        else if (newTemperature < LOWER_TEMP)
            currentTemperature = LOWER_TEMP;
        else currentTemperature = newTemperature;
    }

    private double getPowerInZone(){
        double powerInZone = 0;
        for (Heater heater : heatersInZone){
            if (heater instanceof PowerObservable){
                powerInZone +=((PowerObservable) heater).getCurrentConsumption();
            }else {
                powerInZone += heater.getPowerLevel() * DEFAULT_MAX_POWER;
            }
        }
        for (Cooler cooler : coolersInZone){
            if (cooler instanceof PowerObservable){
                powerInZone -= ((PowerObservable) cooler).getCurrentConsumption();
            }else {
                powerInZone -= cooler.getPowerLevel() * DEFAULT_MAX_POWER;
            }
        }
        return powerInZone;
    }
}
