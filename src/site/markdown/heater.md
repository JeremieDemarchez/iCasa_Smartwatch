Heater/Cooler
====

* Heater
* Cooler


<div class="idCard">

<div class="titleCard">Heater</div>
 
<div class="descriptionCard">Overview</div> 
 
Heater can supply only one model of heater which is a 1000 Watts electrical heater. 
The heater power level can be adjusted between 0 and 1.0 which means into range of 0 Watt (heater is off) and 1000 Watts (completely turned on).

<div class="propertiesCard">Thermal properties</div>

<table>
	<thead>
		<tr>
    		<td>Model</td>
        	<td>heater.powerLevel</td>
        	<td>heater.maxPowerLevel</td>
    	</tr>
    </thead>
    <tbody>
		<tr>
    		<td>Heater</td>
       	 	<td>[0-1.0]</td>
        	<td>1000 Watts</td>
    	</tr>
    </tbody>
</table>

We take into account physical consideration to compute the temperature (expressed in Kelvin unit) returned by the device. We have considered that the room has no thermal loss and the external temperature does not influence the internal temperature.

        
<div class="methodsCard">Methods</div>
<ul>
<li><code>getSerialNumber()</code>: Get the device ID</li>
<li><code>getPowerLevel()</code>: Get the power level in percentage</li>
<li><code>setPowerLevel(double level)</code>: Set the power level of the heater in percentage</li>
<li><code>getMaxPowerLevel()</code>: Get the max power level of the heater in Watts</li>
</ul>

[Full datasheet here](http://example.net/)

</div>


* * * * 


<div class="idCard">

<div class="titleCard"></div>
 
<div class="descriptionCard">Overview</div> 
 
 
<div class="propertiesCard">Thermal properties</div>

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



        
<div class="methodsCard">Methods</div>
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


* * * * 

