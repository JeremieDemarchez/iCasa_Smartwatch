Detectors
====

<br/>

* <a href="#COSensorCard">Carbon Monoxyde Sensor</a>
* <a href="#CO2SensorCard">Carbon Dioxyde Sensor</a>
* <a href="#PhotometerCard">Photometer</a>
* <a href="#PresenceSensorCard">Presence Sensor</a>
* <a href="#MotionSensorCard">Motion Sensor</a>
* <a href="#PowerSwitchCard">Power Switch</a>
* <a href="#ThermometerCard">Thermometer</a>

<div class="idCard">

<div class="titleCard"><a name="COSensorCard">Carbon Monoxyde Sensor</a></div>

<div class="photo"><img src="./devices/gazSensor.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
 <p>GasSensor can supply two models of gas sensor which are a standard CO2 sensor and a standard CO sensor.</p>
<p>Gas sensor can be used to detect the air quality in a room and prevent of asphyxiation. </p>
 
<div class="hCard">Physical properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>carbonMonoxydeSensor.currentConcentration</td>
<td>CARBON_MONOXYDE_SENSOR_CURRENT_CONCENTRATION</td>
<td>[0.0-undefined]</td>
<td>0.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>There is no physical consideration for this type of device. Indeed, this device is used to return a physical value. In our case, we do not care about the way the sensor gives this value.</p>

<p>It is necessary to have the global variable named CO2Concentration in a room if you want to use the CarbonDioxydeSensor sensor without getting errors. Likewise, it is required to set the global variable named COConcentration in a room if you want to use the sensor CarbonMonoxydeSensor.</p>

        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.gazSensor.CarbonMonoxydeSensor</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getCOConcentration()</code>: Get the current gaz (CO) concentration of the sensor in µg/m^3</li>
</ul>

<a href="./datasheets/Datasheet_GasSensor.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>

<div class="idCard">

<div class="titleCard"><a name="CO2SensorCard">Carbon Dioxyde Sensor</a></div>

<div class="photo"><img src="./devices/smokeDetector.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
 <p>GasSensor can supply two models of gas sensor which are a standard CO2 sensor and a standard CO sensor.</p>
<p>Gas sensor can be used to detect the air quality in a room and prevent of asphyxiation.</p>
 
<div class="hCard">Physical properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>carbonDioxydeSensor.currentConcentration</td>
<td>CARBON_DIOXYDE_SENSOR_CURRENT_CONCENTRATION</td>
<td>[0.0-undefined]</td>
<td>0.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>There is no physical consideration for this type of device. Indeed, this device is used to return a physical value. In our case, we do not care about the way the sensor gives this value.</p>

<p>It is necessary to have the global variable named CO2Concentration in a room if you want to use the CarbonDioxydeSensor sensor without getting errors. Likewise, it is required to set the global variable named COConcentration in a room if you want to use the sensor CarbonMonoxydeSensor.</p>

        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.gazSensor.CarbonDioxydeSensor</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getCO2Concentration()</code>: Get the current gaz (CO2) concentration of the sensor in µg/m^3</li>
</ul>

<a href="./datasheets/Datasheet_GasSensor.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>


<div class="idCard">

<div class="titleCard"><a name="PhotometerCard">Photometer</a></div>

<div class="photo"><img src="./devices/photometer.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>Photometer can supply only one model of photometer which is a standard photometer.
The photometer returns the value of the current illuminance. This value is expressed as a double, in lux units.</p>
 
<div class="hCard">Electro-optical properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>photometer.currentIlluminance</td>
<td>PHOTOMETER_CURRENT_ILLUMINANCE</td>
<td>current_illuminance</td>
<td>[0.0-undefined]</td>
<td>0.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>There is no physical consideration for this type of device. Indeed, this device is used to return a physical value. In our case, we do not care about the way the sensor gives this value.</p>

<p>It is necessary to have the global variable named Illuminance in a room if you want to use the photometer sensor without getting errors. </p>
        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.light.Photometer</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getIlluminance()</code>: Get the current illuminance in lux</li>
</ul>

<a href="./datasheets/Datasheet_Photometer.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>




<div class="idCard">

<div class="titleCard"><a name="PresenceSensorCard">Presence Sensor</a></div>

<div class="photo"><img src="./devices/T456/detecteurMouvements.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>PresenceSensor can supply only one model of presence sensor switch which is a standard presence sensor. The presence sensor can be used to detect if someone is present in a room.</p>
 
<div class="hCard">Device properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>presenceSensor.sensedPresence</td>
<td>PRESENCE_SENSOR_SENSED_PRESENCE</td>
<td>True/False</td>
<td>False</td>
<td>Boolean</td>
<td>No</td>
</tr>
</tbody>
</table>

<p>If the sensedPresence property is true then there is someone close to the sensor. On the contrary, if the sensedPresence is false, there is no one close to the sensor.</p>


        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.presence.PresenceSensor</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getSensedPresence()</code>: Get the current status of the switch:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
</ul>

<a href="./datasheets/Datasheet_PresenceSensor.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>

<div class="idCard">

<div class="titleCard"><a name="MotionSensorCard">Motion Sensor</a></div>

<div class="photo"><img src="./devices/T456/detecteurMouvements.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>MotionSensor can supply only one model of motion detection which is a standard
transformation of a movement, into a signal or event.
The motion sensor can be used to detect if someone is moving in a room. We
describe in section MotionSensor device Outline methods linked to this device.
</p>
 
        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.motion.MotionSensor</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>addListener(DeviceListener listener)</code>: Subscribe to motion events</li>
</ul>

<p>Hereafter we explain methods that can be useful for the user to retrieve motion detection events.</p>
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.DeviceListener</code>

<ul>
<li><code>deviceEvent(MotionSensor device, Object value)</code>: Event triggered when the motion
sensor detects a movement:
<ul>
<li>device: the motion sensor object</li>
<li>value: always Boolean.TRUE</li>
</ul>
</li>
</ul>

<a href="./datasheets/Datasheet_MotionSensor.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>


<div class="idCard">

<div class="titleCard"><a name="PowerSwitchCard">Power Switch</a></div>

<div class="photo"><img src="./devices/T456/bouton_.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>PowerSwitch can supply only one model of power switch which is a standard binary switch. The power switch can switch ON and OFF an equipment (i.e.: binaryLight).</p>
 
<div class="hCard">Device properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>powerSwitch.currentStatus</td>
<td>POWER_STATUS_CURRENT_STATUS</td>
<td>True/False</td>
<td>False</td>
<td>Boolean</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>If the currentStatus property is true then all equipment in a room will be switched on. On the contrary, if the currentStatus is false, all equipment will be turn off.</p>


        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.power.PowerSwitch</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getStatus()</code>: Get the current status of the switch:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
<li><code>switchOn()</code>: Set the power switch status ON:
<ul>
<li>switched On: true</li>
</ul>
</li>
<li><code>switchOff()</code>: Set the power switch status OFF:
<ul>
<li>switched Off: false</li>
</ul>
</li>
</ul>

<a href="./datasheets/Datasheet_PowerSwitch.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>




<div class="idCard">

<div class="titleCard"><a name="ThermometerCard">Thermometer</a></div>

<div class="photo"><img src="./devices/thermometer.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>Thermometer can supply only one model of thermometer which is a standard thermometer. The thermometer can be used to have the current temperature in a room. The thermometer device returns a value in Kelvin degree for the temperature.</p>
 
<div class="hCard">Device properties</div>

<table>
<thead>
<tr>
<td>Property name</td>
<td>Constant name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td>thermometer.currentTemperature</td>
<td>THERMOMETER_CURRENT_TEMPERATURE</td>
<td>[0.0-undefined]</td>
<td>0.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>

<p>It is necessary to have the global variable named Temperature in a room if you want to use the Thermometer sensor without getting errors. </p>

        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.temperature.Thermometer</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getTemperature()</code>: Get the current temperature of the thermometer in Kelvin degree</li>

</ul>

<a href="./datasheets/Datasheet_Thermometer.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>


