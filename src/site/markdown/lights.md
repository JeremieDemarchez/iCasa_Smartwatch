Lights
====

<br/>

* <a href="#binaryCard">Binary Light</a>
* <a href="#dimmerCard">Dimmer Light</a>


<div class="idCard">

<div class="titleCard"><a name="binaryCard">Binary Light</a></div>

<div class="photo"><img src="./devices/T456/lampe.png" width="100"/></div>

<div class="description">
<div class="hCard">Overview</div> 
 
<p>BinaryLight can supply only one model of lamp which is a 100 Watts incandescent. The radiation color is white monochromatic emission type. The lamp power is fixed at 100 Watts.</p>
 
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
<td>power_status</td>
<td>True/False</td>
<td>False</td>
<td>Boolean</td>
<td>Yes</td>
</tr>
<tr>
<td>max_power</td>
<td>100.0</td>
<td>100.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>We take into account physical consideration to compute the illuminance (expressed in Lux unit) returned by the device. We have considered that: <i>1 Watt=680.0 lumens at 555nm</i> This conversion is only applicable at wavelength of 555 nm (maximum of sensibility for human vision).</p>


        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.light.BinaryLight</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerStatus()</code>: Get the power status of the lamp:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
<li><code>setPowerStatus(Boolean state)</code>: Set the power status of the lamp:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the lamp in Watts</li>
</ul>

<a href="./datasheets/Datasheet_BinaryLight.pdf">Full datasheet here</a>
</div>
<div class="separator"></div>
</div>




<div class="idCard">

<div class="titleCard"><a name="dimmerCard">Dimmer Light</a></div>

<div class="photo"><img src="./devices/T456/lampeVariable.png" width="100"/></div>

<div class="description">
 
<div class="hCard">Overview</div> 
 
<p>DimmerLight can supply only one model of lamp which is a 100 Watts halogen. The radiation color is white monochromatic emission type.</p>

<p>The lamp power level can be adjusted between 0 and 1.0 which means into range of 0 Watt (lamp is off) and 100 Watts (completely turned on).</p>
 
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
<td>power_level</td>
<td>[0-1.0]</td>
<td>0.0</td>
<td>Double</td>
<td>Yes</td>
</tr>
<tr>
<td>max_power</td>
<td>100.0</td>
<td>100.0</td>
<td>Double</td>
<td>No</td>
</tr>
</tbody>
</table>


<p>We take into account physical consideration to compute the illuminance (expressed in Lux unit) returned by the device. We have considered that:</p>

<i>1 Watt=680.0 lumens at 555nm</i>

<p>This conversion is only applicable at wavelength of 555 nm (maximum of sensibility for human vision).</p>


        
<div class="hCard">Methods</div>

<strong>Interface:</strong> <code>fr.liglab.adele.icasa.device.light.DimmerLight</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerLevel()</code>: Get the power level in percentage</li>
<li><code>setPowerLevel(double level)</code>: Set the power level of the lamp in percentage</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the lamp in Watts</li>
</ul>

<a href="./datasheets/Datasheet_DimmerLight.pdf">Full datasheet here</a>
</div>

<div class="separator"></div>
</div>
