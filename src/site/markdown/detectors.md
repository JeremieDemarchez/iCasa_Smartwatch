Detectors
====

<br/>

* <a href="#PhotometerCard">Photometer</a>
* <a href="#PresenceSensorCard">Presence Sensor</a>
* <a href="#PowerSwitchCard">Power Switch</a>
* <a href="#ThermometerCard">Thermometer</a>


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
<td>Name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>


<p>There is no physical consideration for this type of device. Indeed, this device is used to return a physical value. In our case, we do not care about the way the sensor gives this value.</p>


        
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
<td>Name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

<p>If the sensed_presence property is true then there is someone close to the sensor. On the contrary, if the sensed_presence is false, there is no one close to the sensor.</p>


        
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

<div class="titleCard"><a name="PowerSwitchCard">Power Switch</a></div>

<div class="photo"><img src="./devices/T456/bouton_.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>PowerSwitch can supply only one model of power switch which is a standard binary switch. The power switch can switch ON and OFF an equipment (i.e.: binaryLight).</p>
 
<div class="hCard">Device properties</div>

<table>
<thead>
<tr>
<td>Name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>


<p>If the current_status property is true then all equipment in a room will be switched on. On the contrary, if the current_status is false, all equipment will be turn off.</p>


        
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
<td>Name</td>
<td>Value</td>
<td>Default value</td>
<td>Type</td>
<td>Modifiable</td>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>


        
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


