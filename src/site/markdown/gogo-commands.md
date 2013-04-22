# iCasa GOGO commands

iCasa provides a set of functional <a href="http://felix.apache.org/site/apache-felix-gogo.html">gogo</a> commands helping the manipulation of iCasa elements, such as devices, zones and persons.


- <a href="#Device">Manipulating devices</a>
- <a href="#Zone">Manipulating zones</a>
- <a href="#Persons">Manipulating persons</a>
- <a href="#General">Manipulating Simulation</a>

<a name="Device"></a>
## Manipulating Devices

### Create device
Creates a new simulated device.

Scope -> __icasa__

Name-> __createDevice__

Parameters:

- __${deviceType}__ The device type of the new device.
- __${deviceId}__ The device Id of the new device.

#### Example:
    > g! icasa:createDevice iCASA.Thermometer therm-1
   
### List of devices

Gets the list of devices in the iCasa execution platform, this command shows the devices identifiers, and their position.

Scope -> __icasa__

Name -> __listdevices__

Parameters -> __NONE__

#### Example:

    > g! icasa:listdevices
    Devices:
    Device Id: Pres-B1255D-D - Position: (413:185)
    Device Id: BiLi-C7496W-S - Position: (58:427)
    Device Id: Pres-A1255D-D - Position: (504:374)
    Device Id: Toogle-B1286X-Y - Position: (599:38)
    Device Id: Toogle-D1286X-Y - Position: (219:349)
    Device Id: Pres-D1255D-D - Position: (166:24)
    Device Id: Pres-C1255D-D - Position: (240:399)
    Device Id: BiLi-D7496W-S - Position: (202:59)
    Device Id: Toogle-C1286X-Y - Position: (97:384)
    Device Id: Toogle-A1286X-Y - Position: (509:451)
    Device Id: BiLi-A7496W-S - Position: (492:440)
    Device Id: BiLi-B7496W-S - Position: (505:270)

### Activate device
Active a device.

Scope -> __icasa__

Name-> __activateDevoce__

Parameters-> 

- __${deviceId}__  The device ID.


#### Example:
    > g! icasa:activateDevice Pres-B1255D-D
    
### Deactive device
Deactive a device.

Scope -> __icasa__

Name-> __deactivateDevice__

Parameters-> 

- __${deviceId}__ The device ID.


#### Example:
    > g! icasa:deactivateDevice Pres-B1255D-D
    
### List device properties
Shows the list of properties of a device.

Scope -> __icasa__

Name-> __listDeviceProperties__

Parameters-> 

- __${deviceId}__ The device ID.


#### Example:
    > g! icasa:listDeviceProperties Pres-B1255D-D
    Properties: 
    Property: presenceSensor.sensedPresence - Value: false
    Property: state - Value: activated
    Property: Location - Value: livingroom
    Property: fault - Value: no
    
### Simulate a fail in device
Simulate a fail in a device.

Scope -> __icasa__

Name-> __faultDevice__

Parameters-> 

- __${deviceId}__ the device ID.


#### Example:
    > g! icasa:faultDevice Pres-B1255D-D

### Simulate a reparation in device
Repair a device.

Scope -> __icasa__

Name-> __repairDevice__

Parameters-> 

- __${deviceId}__ the device ID to repair.


#### Example:
    > g! icasa:repairDevice Pres-B1255D-D
   
### Move device
Move a device into new X,Y coordinates

Scope -> __icasa__

Name-> __moveDevice__

Parameters-

- __${deviceId}__ the device ID to move.
- __${X}__  The new X-coordinate.
- __${Y}__  The new Y-coordinate


#### Example:
    > g! icasa:moveDevice Pres-B1255D-D 60 80
    
### Move device into zone
Move a device into new X,Y coordinates

Scope -> __icasa__

Name-> __moveDeviceIntoZone__

Parameters-

- __${deviceId}__ the device ID to move.
- __${zoneId}__  The new zone.



#### Example:
    > g! icasa:moveDeviceIntoZone Pres-B1255D-D kitchen
    
   
### Set device property
Set a new device property

Scope -> __icasa__

Name-> __setDeviceProperty__

Parameters-

- __${deviceId}__ the device ID to modify.
- __${property}__  The property name.
- __${value}__  The property value.


#### Example:
    > g! icasa:setDeviceProperty Pres-B1255D-D newProp newValue
    
### Remove a device
Remove a simulated device

Scope -> __icasa__

Name-> __removeDevice__

Parameters-

- __${deviceId}__ the device ID to remove.



#### Example:
    > g! icasa:removeDevice Pres-A1255D-D
 

### Attach device to a person
Attach or deatach a device from a person

Scope -> __icasa__

Name-> __attachDeviceToPerson__

Parameters-

- __${deviceId}__ the device ID.
- __${personId}__ the person ID.
- __${attach}__ true | false. True for attach, False to deatach.


#### Example:
    > g! icasa:attachDeviceToPerson Pres-A1255D-D Philippe true
    > g! icasa:attachDeviceToPerson Pres-A1255D-D Philippe false
  
<a name="Zone"></a>
## Manipulating Zones
### Create zone
Creates a new zone.

Scope -> __icasa__

Name-> __createZone__

Parameters-

- __${zoneId}__ the new zone ID.
- __${leftX}__ The topX value.
- __${topY}__ The topY value.
- __${width}__ The width of the new zone.
- __${height}__ The height of the new zone.


#### Example:
    > g! icasa:createZone kitchen  50 50 100 100

### List zones
Show the existant zones.

Scope -> __icasa__

Name-> __listZones__

Parameters-

- __NONE__ 


#### Example:
    > g! icasa:listZones
    Zones: 
    Zone livingroom des Zone: livingroom X: 410 Y: 28 -- Width: 245 Height: 350 - Parent: Unset - Use parent: false
    Zone kitchen des Zone: kitchen X: 410 Y: 370 -- Width: 245 Height: 210 - Parent: Unset - Use parent: false
    Zone bathroom des Zone: bathroom X: 55 Y: 20 -- Width: 260 Height: 350 - Parent: Unset - Use parent: false
    Zone bedroom des Zone: bedroom X: 55 Y: 370 -- Width: 259 Height: 210 - Parent: Unset - Use parent: false


### Move zone
Move a zone to a new position

Scope -> __icasa__

Name-> __moveZone__

Parameters-

- __${zoneId}__ the new zone ID.
- __${leftX}__ The topX value.
- __${topY}__ The topY value.


#### Example:
    > g! icasa:moveZone livingroom 410 25
    
### Resize zone
Resize a zone

Scope -> __icasa__

Name-> __resizeZone__

Parameters-

- __${zoneId}__ the new zone ID.
- __${width}__ The new width value.
- __${height}__ The new height value.


#### Example:
    > g! icasa:resizeZone livingroom 245 300
    
### Add parent
Add a parent to a zone.

Scope -> __icasa__

Name-> __setZoneParent__

Parameters-

- __${zoneId}__ the child zone ID.
- __${parentId}__ The parent zone ID.
- __${useParentVariables}__ true if zone will use the parent variables, false if not.

Throws-> Exception when zone does not fit in the parent.


#### Example:
    > g! icasa:setZoneParent livingroom chair true
           
  
### Add variable
Add a variable to a zone.

Scope -> __icasa__

Name-> __addZoneVariable__

Parameters-

- __${zoneId}__ the zone ID.
- __${variableName}__ The variable name.


#### Example:
    > g! icasa:addZoneVariable livingroom comfortable

### Modify variable
Modifies a variable in a given zone.

Scope -> __icasa__

Name-> __modifyZoneVariableValue__

Parameters-

- __${zoneId}__ the zone ID.
- __${variableName}__ The variable name.
- __${variableValue}__ The numeric value.


#### Example:
    > g! icasa:modifyZoneVariableValue livingroom comfortable 100
     
### Attach Device to zone
Attach a specific device to a given zone.

Scope -> __icasa__

Name-> __attachZoneToDevice__

Parameters-

- __${deviceId}__ the zone ID.
- __${zoneId}__ The variable name.
- __${attach}__ True to attach, false to deatach.


#### Example:
    > g! icasa:attachZoneToDevice Pres-A1255D-D chair true
    > g! icasa:attachZoneToDevice Pres-A1255D-D chair false
       
### List zone variables
Shows the list of variables of a zone.

Scope -> __icasa__

Name-> __listZoneVariables__

Parameters-> 

- __${zoneId}__ The zone ID.


#### Example:
    > g! icasa:listZoneVariables livingroom
    Variables: 
    Variable: Volume - Value: 10.0
    Variable: Illuminance - Value: 5.0
    Variable: Temperature - Value: 295.15

<a name="Persons"></a>
##Manipulating Persons
### Create persons
Creates a new simulated person.

Scope -> __icasa__

Name-> __createPerson__

Parameters:

- __${personId}__ The new person Id.
- __${personType}__ The person type.


#### Example:
    > g! icasa:createPerson Pierre Boy

### Get persons in zone
Get the zones where a given person is located.

Scope -> __icasa__

Name-> __getPersonZones__

Parameters:

- __${personId}__ The person Id.


#### Example:
    > g! icasa:getPersonZones Pierre 
    Zones: 
    Zone : Zone: bedroom X: 55 Y: 370 -- Width: 259 Height: 210 - Parent: Unset - Use parent: false
    
### List persons
Shows the list of persons.

Scope -> __icasa__

Name-> __listPersons__

Parameters-> 

- __${personId}__ The person ID.


#### Example:
    > g! icasa:listPersons
    Persons: 
    Person Person: Pierre - Position: (215:444) - Type: Boy

### Move person
Move a person to new positions

Scope -> __icasa__

Name-> __movePerson__

Parameters-> 

- __${personId}__ The person ID.


#### Example:
    > g! icasa:movePerson Pierre 100 100

### Move person to a zone
Move a person to a zone.

Scope -> __icasa__

Name-> __movePersonIntoZone__

Parameters-> 

- __${personId}__ The person ID.
- __${zoneId}__ The zone ID.


#### Example:
    > g! icasa:movePersonIntoZone Pierre kitchen

### Attach person to a zone
Move a person to a zone.

Scope -> __icasa__

Name-> __movePersonIntoZone__

Parameters-> 

- __${personId}__ The person ID.
- __${zoneId}__ The zone ID.
- __${attach}__ True to attach, false to deatach.


#### Example:
    > g! icasa:attachPersonToZone Pierre chair true
    > g! icasa:attachPersonToZone Pierre chair false


<a name="General"></a>
##Manipulating Simulation
### Reset context
Clear the simulation context, it will remove all zones, simulated devices and persons.

Scope -> __icasa__

Name-> __resetContext__

Parameters-> 

- __NONE__ 


#### Example:
    > g! icasa:resetContext

### Execute script
Execute a script. The scripts must be already loaded (available in the script directory)

Scope -> __icasa__

Name-> __executeScript__

Parameters-> 

- __${scriptName}__ script name to execute.


#### Example:
    > g! icasa:executeScript SetupHouseWithLights.bhv
