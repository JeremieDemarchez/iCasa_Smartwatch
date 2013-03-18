# Simulator Web Guide

The simulator module of iCasa platform provides a web interface for interaction. In the home page the user can select the simulation platform to be used, it can create, modify or remove the platforms of the list. To access the home page locally in your browser uses this url : [http://localhost:9000](http://localhost:9000). 
    
Once the platform selected, the main interface is displayed. It is divide in two sections, the map section presents the house plan and the actions section shows information about the execution state of the iCasa platform and allows execution of actions to modify it.


- [Platforms Operations](#Platform)
- [Map Section](#Map)
- [Actions Section](#Actions)

<a name="Platform"></a> 
## Platforms Operations

Each platform has to define an unique ID, a name, the platform URL and a house map (png file). In this page the user can add a platform, edit it or remove it. Platforms will persist in the GUI server and can be reused among different server executions. 

![Devices Tab](guide/home-page.png "Device Tab")

The user picks a platform to start the main interface of the simulator shown in the next image. Users can also go directly to simulator main interface using the url pattern _http://localhost:9000/map/$platformID_, for example to access the platform having the id Default_House the user have to go to [http://localhost:9000/map/Default_House](http://localhost:9000/map/Default_House)

![Devices Tab](guide/main-interface.png "Device Tab")

<a name="Map"></a> 
## Map Section

Map section shows a house plan defined by the user, in this plan are placed the different objects of iCasa context as devices, persons and zones. In the map all of these objects can be moved using drag-and-drop operations. In addition, zones surfaces can be modified graphically.

![Map section](guide/map-section.png "Map section")

<a name="Actions"></a> 
## Actions Section

This section includes 4 Tabs: Devices, Zones, Persons, Script Player.

- [Devices Tab](#Devices)
  - [Create Device](#Description)
  - [Remove Device](#Description)
- [Zones Tab](#Zones)
  - [Create Zone](#Description)
  - [Remove Zone](#Description)
- [Persons Tab](#Persons)
  - [Create Person](#Description)
  - [Remove Person](#Description)
- [Script Player Tab](#Script)
  - [Clock Manipulation](#Description)
  - [Execute/Pause/Stop Script](#Description)
  - [Save State in Script](#Description)

<a name="Devices"></a> 
### Devices Tab

The device tab presents the list of devices existing in the iCasa platform. Each device is listed with its type and its name as show in the next image. Device details (as properties) are displayed when user clicks on device name. The device tab also presents the list of device types (superior _ComboBox_) deployed in the platform. When a new device type is added, the list is updated automatically.

![Devices Tab](guide/device-tab.png "Device Tab")

#### Create Device

To create a device instance the user must select the device type in the type list and then click on _Create_ Button. If the device is correctly created, the device is added to the device list and also in the map section. The user can modify the device position in the map section.

#### Remove Device

To remove a device instance the user must select the device to be removed in the device list and the click in then _Removed Selected Devices_ button. 

<a name="Zones"></a>
### Zones Tab

The zones tab presents the list of zones existing in the platform. Zone details (as variables) are displayed when user clicks on zone name.

![Zones Tab](guide/zone-tab.png "Zone Tab")

#### Create Zone

To create a zone the user must provide a name and then click on _Create_ Button. If the zone is correctly created the device is added to the device list and in the map section. The user can modify the zone surface or position in the map section.

#### Remove Zone

To remove a zone the user must select the zone to be removed in the zone list and the click in then _Removed Selected Zones_ button. 

<a name="Persons"></a>
### Persons Tab

The persons tab presents the list of persons "existing" in the platform. 

![Persons Tab](guide/person-tab.png "Persons Tab")

#### Create Person

To create a person the user must provide a name, select a person type and then click on _Create_ Button. If the person is correctly created the person is added to the persons list and in the map section. The user can modify the person position in the map section.

#### Remove Person

To remove a person the user select the person to be removed in the persons list and the click in then _Removed Selected Persons_ button. 

<a name="Script"></a>
### Script Player Tab

This tab allows execution of scripts and control of simulated clock.

![Script Player Tab](guide/script-tab.png "Script Player Tab")

#### Clock Manipulation

The clock section displays the (virtual) time in the simulator. Also the time factor (time speed) is showed, the time can be accelerated using a bigger factor.

#### Script Execution

To execute a script select it from the script list and click on _Start_ button. To pause it click on the _Pause_ button, and to resume it, click in _Resume_. To stop the script clicl on _Stop_ button. The user can change the script date of execution selecting a new date in the box. To 

#### Saving State

This operation allows saving the current state of simulator, it creates a new script including instructions to create the current objects and to place them in the right zones. The user must provide a name to the script and the click on the button _Save as_.