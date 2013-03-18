Detectors
====

<br/>

* <a href="#PhotometerCard">Photometer</a>
* <a href="#Card"></a>


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
    		<td>Model</td>
        	<td>current_illuminance</td>
    	</tr>
    </thead>
    <tbody>
		<tr>
    		<td>Photometer</td>
       	 	<td>Default = 0.0</td>
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


