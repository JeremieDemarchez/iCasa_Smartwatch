# How to build an iCasa Device
  
<a name="Requirements"></a>
## 1. Tutorial Requirements

- OSGi & iPOJO : iCasa is built on top of [OSGi](http://www.osgi.org) platform and [iPOJO](http://felix.apache.org/site/apache-felix-ipojo.html) component model
- Maven (Optional) : [Maven](http://maven.apache.org/) tool can be used to build new iCasa devices

<a name="Model"></a>
## 2. iCasa Device Model

iCasa framework provides a device model that must be used to create new devices. In this model, each device has to publish a description based in a Java interface and a set of properties. Device interface is used to expose device functionality, on the other hand properties set is used to known device state.

To integrate a new device into the iCasa framework the device has to implement the ___fr.liglab.adele.icasa.device.GenericDevice___ interface. The GenericDevice interface defines methods to get the device unique identifier (___getSerialNumber()___), to access its state (___getState()___) and its fault situation (___getFault()___), and also a method to interrogate device to obtain its properties values (___getPropertyValue(String propertyName)___).

In addition, each device has the possibility of notify clients about changes in its state (when its properties' values have been changed) using the listener pattern. The device sends events to its listeners using a callback method (___devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue)___). Events sent must implement the Java interface ___fr.liglab.adele.icasa.device.DeviceEvent___ and listeners must implement ___fr.liglab.adele.icasa.device.DeviceListener___ interface.

iCasa framework provides an abstract implementation class for _GenericDevice_ interface: ___fr.liglab.adele.icasa.device.util.AbstractDevice___. This abstract class eases the development of new devices implementation by inheriting from it. There exists mainly two types of devices in iCasa: devices using a scope zone and localization (thermometer gets the temperature of a room by example) and devices that are independent of their localization i.e. a bathroom scale.


<a name="Simulated"></a>
## 3. Simulated Devices

Testing and debugging pervasive applications is a difficult task because usually developers have not access to all physical devices. On the other hand is not easy to generate the adequate events in the right moment in order to produce conditions for testing applications in different execution scenarios. For this reason iCasa framework includes a simulation module allowing testing and debugging of pervasive applications. 
The iCasa simulation module provides a set of prebuilt simulated devices, it is extensible, allowing developers to build new types of simulated devices. Every simulated device is a full compliant iCasa device, but simulated device must implement _fr.liglab.adele.icasa.simulator.SimulatedDevice_ interface to be recognized by the iCasa simulation framework. 
   
<a name="Device_Scope"></a>
## 4. Device using scope zone: A Thermometer Device

In this section we will show how to extend the iCasa simulation module adding a new simulated device that uses a scope zone; the thermometer device. The section is divided in 4 subsections:
     
<a name="Description"></a>
### 4.1. Device Description

As show in the next code fragment, the description of the Thermometer device includes is made in a Java interface (___fr.liglab.adele.icasa.device.temperature.Thermometer___) and a set of properties (_state_, _fault_ and _current_temperature_). Usually the properties name are defined as static fields (constants) of the Java interface as <em>THERMOMETER_CURRENT_TEMPERATURE</em> .

The Thermometer java interface is as follows

    package fr.liglab.adele.icasa.device.temperature;

    import fr.liglab.adele.icasa.device.GenericDevice;

    public interface Thermometer extends GenericDevice {

       static String THERMOMETER_CURRENT_TEMPERATURE = "thermometer.currentTemperature"; // Temperature Property

       double getTemperature();

    }

To access device current temperature the client has two options, call the method __getTemperature()__ or ask for the __thermometer.currentTemperature__ property.
   
<a name="Implementation"></a>
### 4.2. Device Implementation

Once the device interface defined a implementation class to this device must be provided. 

    package fr.liglab.adele.icasa.device.temperature.impl;

	// Imports section omitted
	
    @Component(name = "iCasa.Thermometer")
    @Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
    public class SimulatedThermometerImpl extends AbstractDevice implements Thermometer, SimulatedDevice {

	    @ServiceProperty(name = Thermometer.DEVICE_SERIAL_NUMBER, mandatory = true)
	    private String m_serialNumber;

	    private volatile Zone m_zone;

	    private ZoneListener listener = new ThermometerZoneListener();

	    public SimulatedThermometerImpl() {
		    super();

			// Property initialization
            super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);

			// Property initialization
		    setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, 0.0); 
	    }

	    @Override
	    public String getSerialNumber() {
		    return m_serialNumber;
	    }

	    @Override
	    public synchronized double getTemperature() {
    		return (Double) getPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE);
	    }

	    @Override
	    public void enterInZones(List<Zone> zones) {
		    if (!zones.isEmpty()) {
			    for (Zone zone : zones) {
				    if (zone.getVariableValue("Temperature") != null) {
					    m_zone = zone;
					    getTemperatureFromZone();
						
						// Zone listener registration
					    m_zone.addListener(listener);
					    break;
				    }
			    }
		    }
	    }

	    @Override
	    public void leavingZones(List<Zone> zones) {
		    setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, null);
			// Zone listener unregistration
		    if (m_zone != null)			
    			m_zone.removeListener(listener);
    	}

	    private void getTemperatureFromZone() {
		    if (m_zone != null) {
			    Object currentTemperature = m_zone.getVariableValue("Temperature");
			    if (currentTemperature != null)
				    setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, currentTemperature);
		    }
	    }

		// Zone listener implementation
	    class ThermometerZoneListener extends BaseZoneListener {

		    @Override
		    public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {

			    if (m_zone == zone) {
				    if (!(getFault().equalsIgnoreCase("yes")))
					    if (variableName.equals("Temperature"))
						    getTemperatureFromZone();
			    }
		    }
	    }

    }

<a name="Properties"></a>	
#### 4.2.1. Device Properties Initialization
	
The device's properties should be initialized for two reasons: 
- to establish the initial state of the device
- to avoid any problem of associated to null pointers in the device implementation

Usually, this initialization should be done in the constructor as show in the class SimulatedThermometerImpl above.
	
<a name="Scope"></a>
#### 4.2.2. Zones Scope Utilisation

As is shown in the SimulatedThermometerImpl class two methods have been implemented: enterInZones and leaveZones, these methods are callback methods defined in GenericDevice interface. iCasa platform call these methods when de device is placed in a new Zone to indicate its new scope zones and which are it leaving. In our example the device will try to find the first Zone that contains the _Temperature_ variable. Then, the simulated device uses the Temperature variable to obtain the value used in to set its property current_temperature
   
<a name="Listener"></a>
#### 4.2.3. Zone Listener

The SimulatedThermometerImpl class defines an inner class _ThermometerZoneListener_ to be notified when variables in its scope zone have been modified. Our listener is interested in changes in Temperature variable in the device's scope zone. 
This listener has to be subscribed to zone events as is done in enterInZones method ( _m_zone.addListener(listener)_ ), and in leavingZones method the listener is unregistered.
      
<a name="Component"></a>
## 5. Device Component

The iCasa platform has been built on top of the OSGi and iPOJO technologies. Each device to be incorporated to iCasa framework has to be built as an iPOJO component and deployed in the runtime as an OSGi Bundle.
Some iPOJO annotations are defined in the previous implementation class, these annotations (_@Component_, _@Provides_) indicates the iPOJO manipulator tool and iPOJO runtime how to deal with the component. In our example we are indicating that the component is called "iCasa.Thermometer" and that it is providing all implemented interfaces (GenericDevice, SimulatedDevice and Thermometer) as OSGi services.
To know more about the iPOJO compoenent model see <a href="http://felix.apache.org/site/apache-felix-ipojo.html">this</a> and for OSGI <a href="http://www.osgi.org">this</a>.
   
<a name="Packaging"></a>
## 6. Device Packaging

You can use <a href="http://felix.apache.org/site/apache-felix-ipojo.html">maven</a> tool to build a device project. Two iCasa maven artifacts are necessary to build your project, the first one context.api defines the interfaces used in the Device model, the simulation.api provides the SimulatedDevice interface.

Artifacs :

___Context API - Device interfaces___

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>context.api</artifactId>
    <version>${project.version}</version>

___Simulation API - Simulated Device interface___

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>simulator.api</artifactId>
    <version>${project.version}</version>

Repositories :	

	<repositories>
	  <repository>
		<snapshots>
		  <enabled>false</enabled>
		</snapshots>
		<id>adele-central-snapshot</id>
		<name>adele-repos</name>
		<url>http://repository-icasa.forge.cloudbees.com/snapshot</url>
	  </repository>
	  <repository>
		<snapshots />
		<id>snapshots</id>
		<name>adele-central-release</name>
		<url>http://repository-icasa.forge.cloudbees.com/release</url>
	  </repository>
	</repositories>

<a name="POM"></a>	
### 6.1 Thermometer Pom Model File

The pom file used in iCasa simulator module to build the simulated Thermometer is shown in the next fragment code. In addition to the two iCasa artifacts this pom includes other dependencies to iPojo and OSGi code. Finally, the pom defines the utilization of two plugins to iPOJO manipulation and OSGi bundle packaging.

    <?xml version="1.0" encoding="UTF-8"?>

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
       <modelVersion>4.0.0</modelVersion>
  
       <!-- Project coordinates -->
       <artifactId>device.temperature</artifactId>
       <packaging>bundle</packaging>
       <version>1.0.0-SNAPSHOT</version>
       
	   <!-- Project repositories -->
	   <repositories>
	     <repository>
		   <snapshots>
		    <enabled>false</enabled>
		   </snapshots>
		   <id>adele-central-release</id>
		   <name>adele-repos</name>
		   <url>http://repository-icasa.forge.cloudbees.com/release</url>
	     </repository>
	    <repository>
		  <snapshots />
		  <id>snapshots</id>
		  <name>adele-central-snapshot</name>
		  <url>http://repository-icasa.forge.cloudbees.com/snapshot</url>
	     </repository>
	   </repositories>
  
       <!-- Project dependencies -->
       <dependencies>
          <dependency>
             <groupId>org.osgi</groupId>
             <artifactId>org.osgi.core</artifactId>
	         <version>4.2.0</version>
          </dependency>
          <dependency>
             <groupId>org.apache.felix</groupId>
             <artifactId>org.apache.felix.ipojo</artifactId>
	         <version>1.8.2</version>
          </dependency>  
          <dependency>
             <groupId>org.apache.felix</groupId>
             <artifactId>org.apache.felix.ipojo.annotations</artifactId>
	         <version>1.8.2</version>
          </dependency>
          <dependency>
             <groupId>fr.liglab.adele.icasa</groupId>
             <artifactId>context.api</artifactId>
	         <version>${project.version}</version>
          </dependency>
          <dependency>
            <groupId>fr.liglab.adele.icasa</groupId>
            <artifactId>simulator.api</artifactId>
	        <version>${project.version}</version>
          </dependency>
       </dependencies>

       <build>
         <plugins>
           <plugin>
              <groupId>org.apache.felix</groupId>
              <artifactId>maven-bundle-plugin</artifactId>
			  <version>2.3.7</version>
              <configuration>
                <instructions>
                   <Private-Package>fr.liglab.adele.icasa.device.temperature.impl</Private-Package>
                </instructions>
              </configuration>
            </plugin>
         <plugin>
           <groupId>org.apache.felix</groupId>
           <artifactId>maven-ipojo-plugin</artifactId>
		   <version>1.8.6</version>
         </plugin>
       </plugins>
    </build>
  
</project>
