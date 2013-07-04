# ICasa Script Language

ICasa Simulation module provides a script language that can be used to create scenarios in order to test pervasive applications. In the next section we show the instructions set supported by the language and their (XML) syntax.

___Outline___

- [Script Syntax](#Syntax)
- [General Instructions](#General)
- [Zone Instructions](#Zone)
- [Device Instructions](#Device)
- [Person Instructions](#Person)
- [Scenario Example](#Scenario)
- [Script Deployment](#Deployment)
   
<a name="Syntax"></a>
## Script File Syntax

Scenarios in iCasa simulation module are specified in a XML file using the tag __behavior__. 

_Example:_

    <behavior startdate="03/10/2011-00:00:00" factor="1440">
       <!-- Scenario instructions -->
    </behavior>

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>startdate</td>
  <td>Starting date of script execution in format "dd/MM/yyyy-HH:mm:ss"</td>
</tr>
<tr>
  <td>factor</td>
  <td>speed factor for script execution</td>
</tr>
</table>
    
<a name="General"></a>
## General Instructions
   
### Delay
   
Introduces a delay in (virtual) minutes between the previous instruction and the next one.

_Example:_

    <delay value="60" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>value</td>
  <td>Time in (virtual) minutes</td>
</tr>
</table>

<br>

<a name="Zone"></a>
## Zone Instructions
   
### Create Zone

Creates a zone in application context

_Example:_

    <create-zone id="kitchen"  leftX="410" topY="370" X-Length="245" Y-Length="210" />	

or

    <create-zone id="kitchen"  leftX="410" topY="370" bottomZ="0" X-Length="245" Y-Length="210" Z-Length="300" />	

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>id</td>
  <td>Zone's Identifier</td>
</tr>
<tr>
  <td>leftX</td>
  <td>Left X coordinate value</td>
</tr>
<tr>
  <td>topY</td>
  <td>Top Y coordinate value</td>
</tr>
<tr>
  <td>bottomZ</td>
  <td>Bottom Z coordinate value</td>
</tr>
<tr>
  <td>X-Length</td>
  <td>Zone X-Length</td>
</tr>
<tr>
  <td>Y-Length</td>
  <td>Zone Y-Length</td>
</tr>
<tr>
  <td>Z-Length</td>
  <td>Zone Z-Length</td>
</tr>
</table>
   
### Move Zone

Move a zone in application context

_Example:_

    <move-zone id="kitchen"  leftX="410" topY="370" />	

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>id</td>
  <td>Zone's Identifier</td>
</tr>
<tr>
  <td>leftX</td>
  <td>New left X coordinate value</td>
</tr>
<tr>
  <td>topY</td>
  <td>New top Y coordinate value</td>
</tr>
</table>
   
### Resize Zone

Resize a zone in application context

_Example:_

    <resize-zone id="kitchen"  X-Length="245" Y-Length="210" />	

or

    <resize-zone id="kitchen"  X-Length="245" Y-Length="210" Z-Length="210" />	

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>id</td>
  <td>Zone's Identifier</td>
</tr>
<tr>
  <td>X-Length</td>
  <td>New zone X-Length</td>
</tr>
<tr>
  <td>Y-Length</td>
  <td>New zone Y-Length</td>
</tr>
<tr>
  <td>Z-Length</td>
  <td>New zone Z-Length</td>
</tr>
</table>
    
### Add Variable to Zone

It adds a simulation variable to a zone

_Example:_

    <add-zone-variable zoneId="livingroom" variable="Temperature" />

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>zoneId</td>
  <td>Zone's Identifier</td>
</tr>
<tr>
  <td>variable</td>
  <td>Variable name</td>
</tr>
</table>
   
### Modify Variable in Zone

It modifies a simulation variable in a zone

_Example:_

    <modify-zone-variable zoneId="kitchen" variable="Temperature" value="296.15"/>
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>zoneId</td>
  <td>Zone's Identifier</td>
</tr>
<tr>
  <td>variable</td>
  <td>Variable name</td>
</tr>
<tr>
  <td>value</td>
  <td>Variable value</td>
</tr>
</table>
   
<a name="Device"></a>
## Device Instructions

   
### Create Device

It creates a device instance in the application context

_Example:_

    <create-device id="Pres-A1255D-D" type="iCasa.PresenceSensor" />

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>id</td>
  <td>Device's Identifier</td>
</tr>
<tr>
  <td>type</td>
  <td>Device's type</td>
</tr>
</table>
   
### Remove Device

_Example:_

    <remove-device deviceId="Pres-A1255D-D" />

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
</table>

<br>

### Move device into zone

Places a device in a zone of application context.

_Example:_

    <move-device-zone deviceId="BiLi-A7496W-S" zoneId="kitchen" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
<tr>
  <td>zoneId</td>
  <td>Zone's Identifier</td>
</tr>
</table>

<br>

### Activate Device

Changes the device's state to __activated__. 

_Example:_
    
	<activate-device deviceId="Pres-A1255D-D" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
</table>	

<br>

### Deactivate Device

Changes the device's state to __deactivated__. 

_Example:_
    
	<deactivate-device deviceId="Pres-A1255D-D" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
</table>	

<br>

### Fault Device

Changes the device's fault state to __yes__. 

_Example:_
    
	<fault-device deviceId="Pres-A1255D-D" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
</table>	

<br>

### Repair Device

Changes the device's fault state to __no__. 

_Example:_
    
	<repair-device deviceId="Pres-A1255D-D" />
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
</table>

<br>

### Set device property value

Set the value of the device property

_Example:_

    <set-device-property deviceId="BiLi-A7496W-S" property="power_status" value="true"/>
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>deviceId</td>
  <td>Device's Identifier</td>
</tr>
<tr>
  <td>property</td>
  <td>property name</td>
</tr>
<tr>
  <td>value</td>
  <td>property value</td>
</tr>
</table>

<br>

<a name="Person"></a>
## Person Instructions

<br>
### Create Person

Adds a person to the simulation context

_Example:_

    <create-person id="Paul" type="Grandfather" />

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>id</td>
  <td>Person's Identifier</td>
</tr>
<tr>
  <td>type</td>
  <td>Person's type</td>
</tr>
</table>

<br>
### Move Person into zone

Places a person in a zone in the application context

_Example:_

    <move-person-zone personId="Paul" zoneId="bedroom"  />

<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>personId</td>
  <td>Person's Identifier</td>
</tr>
<tr>
  <td>zoneId</td>
  <td>Zone's identifier</td>
</tr>
</table>

<br>

<a name="Scenario"></a>
## Scenario Example

    <behavior startdate="27/10/2011-00:00:00" factor="1440">
	   
       <!-- Creation of kitchen zone -->   
	   <create-zone id="kitchen"  leftX="410" topY="370" X-Length="245" Z-Length="210" />	 
	
	   <!-- Adding and setting variables in kitchen zone -->
	   <add-zone-variable zoneId="kitchen" variable="Temperature" />
	   <add-zone-variable zoneId="kitchen" variable="Illuminance" />
	   <modify-zone-variable zoneId="kitchen" variable="Temperature" value="296.15"/>
	   <modify-zone-variable zoneId="kitchen" variable="Illuminance" value="0"/>
	
	   <!-- Creating and placing devices in kitchen zone -->
	   <create-device id="Pres-A1255D-D" type="iCasa.PresenceSensor" />
	   <create-device id="Ther-A3654Q-S" type="iCasa.Thermometer" />
	   <create-device id="Phot-A4894S-S" type="iCasa.Photometer" />
	   <create-device id="BiLi-A7496W-S" type="iCasa.BinaryLight" />	
	   <move-device-zone deviceId="Pres-A1255D-D" zoneId="kitchen" />
	   <move-device-zone deviceId="Ther-A3654Q-S" zoneId="kitchen" />
	   <move-device-zone deviceId="Phot-A4894S-S" zoneId="kitchen" />
	   <move-device-zone deviceId="BiLi-A7496W-S" zoneId="kitchen" />

	   <!-- Creation of livingroom zone --> 
       <create-zone id="livingroom" leftX="410" topY="28" X-Length="245" Y-Length="350" />
	   <!-- Adding and setting variables in kitchen zone -->
	   
	   <add-zone-variable zoneId="livingroom" variable="Temperature" />
	   <add-zone-variable zoneId="livingroom" variable="Illuminance" />
	   <add-zone-variable zoneId="livingroom" variable="Volume" />
	   <modify-zone-variable zoneId="livingroom" variable="Temperature" value="295.15"/>
	   <modify-zone-variable zoneId="livingroom" variable="Illuminance" value="5"/>
	   <modify-zone-variable zoneId="livingroom" variable="Volume" value="10"/>
	
	   <!-- Others instructions --> 
	   <delay value="100" />	
	   <deactivate-device deviceId="Pres-A1255D-D" />
	   <delay value="100" />
	   <activate-device deviceId="Pres-A1255D-D" />
	   <delay value="100" />
	   <fault-device deviceId="Pres-A1255D-D" />
	   <delay value="100" />
	   <repair-device deviceId="Pres-A1255D-D" />
	   <delay value="100" />
	   <remove-device deviceId="Pres-A1255D-D" />	
       <delay value="100" />
	   <set-device-property deviceId="BiLi-A7496W-S" property="power_status" value="true"/>
	
    </behavior>

<a name="Deployment"></a>
## Script Deployment

New scripts have to be deployed to the directory __ICASA_HOME/scripts__ in the gateway. All scripts files must have the .bhv extensions to be recognized by the iCasa platform.
	