
Lights
====

* Binary Light
* Dimmer Light


<div class="idCard">

<div class="titleCard">Binary Light</div>
 
<div class="descriptionCard">Overview</div> 
 
BinaryLight can supply only one model of lamp which is a 100 Watts incandescent. The radiation color is white monochromatic emission type. The lamp power is fixed at 100 Watts.
 
<div class="propertiesCard">Electro-optical properties</div>

|    Model    | power_status |  max_power  |
| ----------- | ------------ | ----------: |
| BinaryLight |	True - False |	100 Watts  |


        
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
