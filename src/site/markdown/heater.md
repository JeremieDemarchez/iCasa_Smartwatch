Heater/Cooler
====

<br/>

* <a href="#heaterCard">Heater</a>
* <a href="#coolerCard">Cooler</a>


<div class="idCard">

<div class="titleCard"><a name="heaterCard">Heater</a></div>
 
<div class="photo"><img src="./devices/T456/radiateur.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>Heater can supply only one model of heater which is a 1000 Watts electrical heater. 
The heater power level can be adjusted between 0 and 1.0 which means into range of 0 Watt (heater is off) and 1000 Watts (completely turned on).</p>

<div class="hCard">Thermal properties</div>

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

<p>We take into account physical consideration to compute the temperature (expressed in Kelvin unit) returned by the device. We have considered that the room has no thermal loss and the external temperature does not influence the internal temperature.</p>

        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.temperature.Heater</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerLevel()</code>: Get the power level in percentage</li>
<li><code>setPowerLevel(double level)</code>: Set the power level of the heater in percentage</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the heater in Watts</li>
</ul>

<a href="./datasheets/Datasheet_Heater.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>






<div class="idCard">

<div class="titleCard"><a name="coolerCard">Cooler</a></div>
 
<div class="photo"><img src="./devices/T456/airConditionne.png" width="100"/></div>
 
<div class="description"> 
<div class="hCard">Overview</div> 
 
<p>Cooler can supply only one model of cooler which is a 1000 Watts electrical cooler. 
The cooler power level can be adjusted between 0 and 1.0 which means into range of 0 Watt (cooler is off) and 1000 Watts (completely turned on).</p>

<div class="hCard">Thermal properties</div>

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


<p>We take into account physical consideration to compute the temperature (expressed in Kelvin unit) returned by the device. We have considered that the room has no thermal loss and the external temperature does not influence the internal temperature.</p>

        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.temperature.Cooler</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerLevel()</code>: Get the power level in percentage</li>
<li><code>setPowerLevel(double level)</code>: Set the power level of the cooler in percentage</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the cooler in Watts</li>
</ul>

<a href="./datasheets/Datasheet_Cooler.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>


