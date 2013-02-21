
Lights
====

* Binary Light
* Dimmer Light


<div class="idCard">

<div class="titleCard">Binary Light</div>
 
<div class="descriptionCard">Overview</div> 
 
BinaryLight can supply only one model of lamp which is a 100 Watts incandescent. The radiation color is white monochromatic emission type. The lamp power is fixed at 100 Watts.
 
<div class="propertiesCard">Electro-optical properties</div>

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

This conversion is only applicable at wavelength of 555 nm (maximum of sensibility of vision for human eyes).


        
<div class="methodsCard">Methods</div>
<ul>
<li>getSerialNumber(): Get the device ID</li>
<li>getPowerStatus(): Get the power status of the lamp:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
<li>setPowerStatus(Boolean state): Set the power status of the lamp:
<ul>
<li>switched On: true</li>
<li>switched Off: false</li>
</ul>
</li>
<li>getMaxPowerLevel(): Get the max power level of the lamp in Watts</li>
</ul>

</div>
