Lights
====

* Binary Light
* Dimmer Light


<div class="idCard">

<div class="titleCard">Binary Light</div>
 
<div class="hCard">Overview</div> 
 
BinaryLight can supply only one model of lamp which is a 100 Watts incandescent. The radiation color is white monochromatic emission type. The lamp power is fixed at 100 Watts.
 
<div class="hCard">Electro-optical properties</div>

<table>
	<thead>
		<tr>
    		<td>Model</td>
        	<td>power_status</td>
        	<td>max_power</td>
    	</tr>
    </thead>
    <tbody>
		<tr>
    		<td>BinaryLight</td>
       	 	<td>True - False</td>
        	<td>100 Watts</td>
    	</tr>
    </tbody>
</table>

We take into account physical consideration to compute the illuminance (expressed in Lux unit) returned by the device. We have considered that:

_1 Watt=680.0 lumens at 555nm_

This conversion is only applicable at wavelength of 555 nm (maximum of sensibility for human vision).


        
<div class="hCard">Methods</div>

Interface: <code>fr.liglab.adele.icasa.device.light.BinaryLight</code>

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

[Full datasheet here](http://example.net/)

</div>


<div class="idCard">

<div class="titleCard">Dimmer Light</div>
 
<div class="hCard">Overview</div> 
 
DimmerLight can supply only one model of lamp which is a 100 Watts halogen. The radiation color is white monochromatic emission type.

The lamp power level can be adjusted between 0 and 1.0 which means into range of 0 Watt (lamp is off) and 100 Watts (completely turned on). 
 
<div class="hCard">Electro-optical properties</div>

<table>
	<thead>
		<tr>
    		<td>Model</td>
        	<td>power_level</td>
        	<td>max_power</td>
    	</tr>
    </thead>
    <tbody>
		<tr>
    		<td>DimmerLight</td>
       	 	<td>[0-1.0]</td>
        	<td>100 Watts</td>
    	</tr>
    </tbody>
</table>

We take into account physical consideration to compute the illuminance (expressed in Lux unit) returned by the device. We have considered that:

_1 Watt=680.0 lumens at 555nm_

This conversion is only applicable at wavelength of 555 nm (maximum of sensibility for human vision).


        
<div class="hCard">Methods</div>

Interface: <code>fr.liglab.adele.icasa.device.light.DimmerLight</code>

<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerLevel()</code>: Get the power level in percentage</li>
<li><code>setPowerLevel(double level)</code>: Set the power level of the lamp in percentage</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the lamp in Watts</li>
</ul>



[Full datasheet here](http://example.net/)

</div>
