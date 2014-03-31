# ICasa Script Language

ICasa Simulation module provides a script language that can be used to create scenarios in order to test pervasive applications. In the next section we show the instructions set supported by the language and their (XML) syntax.
   
<a name="Syntax"></a>
## 1. Script File Syntax

Scenarios in iCasa simulation module are specified in a XML file using the tag __behavior__. 

**Example**:

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
## 2. General Instructions
   
### 2.1. Delay
   
Introduces a delay in (virtual) minutes between the previous instruction and the next one.

**Example**:

    <delay value="60" />
    
or

    <delay value="60" unit="h"/>
	
<table cellpadding="2" cellspacing="0" border="1">
<tr>
  <th>Attribute
  </th><th>Description
  </th>
</tr>
<tr>
  <td>value</td>
  <td>Time in (virtual) units</td>
</tr>
<tr>
  <td>unit</td>
  <td>Time unit, value it can be h (hours), m (minutes), s (seconds). Default value is m</td>
</tr>
</table>

<br>

<a name="Zone"></a>
## 3. Zone Instructions
   
### 3.1. Create Zone

Creates a zone in application context

**Example**:

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
   
### 3.2. Move Zone

Move a zone in application context

**Example**:

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
   
### 3.3. Resize Zone

Resize a zone in application context

**Example**:

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
    
### 3.4. Add Variable to Zone

It adds a simulation variable to a zone

**Example**:

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
   
### 3.5. Modify Variable in Zone

It modifies a simulation variable in a zone

**Example**:

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
## 4. Device Instructions

   
### 4.1. Create Device

It creates a device instance in the application context

**Example**:

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
   
### 4.2. Remove Device

**Example**:

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

### 4.3. Move device into zone

Places a device in a zone of application context.

**Example**:

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

### 4.4. Activate Device

Changes the device's state to __activated__. 

**Example**:
    
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

### 4.5. Deactivate Device

Changes the device's state to __deactivated__. 

**Example**:
    
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

### 4.6. Fault Device

Changes the device's fault state to __yes__. 

**Example**:
    
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

### 4.7. Repair Device

Changes the device's fault state to __no__. 

**Example**:
    
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

### 4.8. Set device property value

Set the value of the device property

**Example**:

    <set-device-property deviceId="BiLi-A7496W-S" property="powerStatus" value="true"/>
	
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
## 5. Person Instructions

<br>
### 5.1. Create Person

Adds a person to the simulation context

**Example**:

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
### 5.2. Move Person into zone

Places a person in a zone in the application context

**Example**:

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
## 6. Scenario Example

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
	   <set-device-property deviceId="BiLi-A7496W-S" property="powerStatus" value="true"/>
	
    </behavior>

<a name="Deployment"></a>
## 7. Script Deployment

New scripts have to be deployed to the directory __ICASA_HOME/scripts__ in the gateway. All scripts files must have the .bhv extensions to be recognized by the iCasa platform.
	