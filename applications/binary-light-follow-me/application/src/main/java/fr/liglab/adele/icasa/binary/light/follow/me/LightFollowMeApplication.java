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
package fr.liglab.adele.icasa.binary.light.follow.me;

import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.location.LocatedObject;
import org.apache.felix.ipojo.annotations.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component(name="LightFollowMeApplication")

@Provides(properties= {
	@StaticServiceProperty(name="icasa.application", type="boolean", value="true", immutable=true)
})

@Instantiate
public class LightFollowMeApplication {



    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {

    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        // do nothing
    }

    @Requires(id="lights",optional = true,specification = BinaryLight.class,filter = "(!(locatedobject.object.zone="+LocatedObject.LOCATION_UNKNOWN+"))")
    private List<BinaryLight> binaryLights;

    @Requires(id="sensors",optional = true,specification = PresenceSensor.class,filter = "(!(locatedobject.object.zone="+LocatedObject.LOCATION_UNKNOWN+"))")
    private List<PresenceSensor> presenceSensors;

    @Bind(id="lights")
    public void bindBinaryLight(BinaryLight binaryLight){
        if (computePresenceInZone(binaryLight.getZone())){
            binaryLight.turnOn();
        }else {
            binaryLight.turnOff();
        }

    }

    @Modified(id="lights")
    public void modifiedBinaryLight(BinaryLight binaryLight){
        if (computePresenceInZone(binaryLight.getZone())){
            binaryLight.turnOn();
        }else {
            binaryLight.turnOff();
        }
    }


    @Unbind(id="lights")
    public void unbindBinaryLight(BinaryLight binaryLight){
        binaryLight.turnOff();
    }

    @Bind(id="sensors")
    public void bindPresenceSensor(PresenceSensor presenceSensor){
        String zoneName = presenceSensor.getZone();
        Set<BinaryLight> lightInZone = getLightInZone(zoneName);
        if (computePresenceInZone(zoneName)){
            lightInZone.stream().forEach((light) ->light.turnOn() );
        }else {
            lightInZone.stream().forEach((light) ->light.turnOff() );
        }
    }

    @Modified(id="sensors")
    public void modifiedPresenceSensor(PresenceSensor presenceSensor){
        String zoneName = presenceSensor.getZone();
        Set<BinaryLight> lightInZone = getLightInZone(zoneName);
        if (computePresenceInZone(zoneName)){
            lightInZone.stream().forEach((light) ->light.turnOn() );
        }else {
            lightInZone.stream().forEach((light) ->light.turnOff() );
        }
    }


    @Unbind(id="sensors")
    public void unbindPresenceSensor(PresenceSensor presenceSensor){
        String zoneName = presenceSensor.getZone();
        Set<BinaryLight> lightInZone = getLightInZone(zoneName);
        if (computePresenceInZone(zoneName)){
            lightInZone.stream().forEach((light) ->light.turnOn() );
        }else {
            lightInZone.stream().forEach((light) ->light.turnOff() );
        }
    }

    private boolean computePresenceInZone(String zone){
        if (zone == null)return false;
        return presenceSensors.stream().anyMatch((presenceSensor)-> presenceSensor.getSensedPresence() && zone.equals(presenceSensor.getZone()));
    }

    private Set<BinaryLight> getLightInZone(String zone){

        Set<BinaryLight> lightInZone = new HashSet<>();
        if (zone == null){
            return lightInZone;
        }
        binaryLights.stream().forEach((light) -> {
            if (zone.equals(light.getZone()))lightInZone.add(light);
        });

        return lightInZone;
    }

}
