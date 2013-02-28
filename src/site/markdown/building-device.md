# How to build an iCasa Device

## Platform Device Model

iCasa framework provides a device model that must be used to create new devices. In this model, each device has to publish a description based in a Java interface and a set of properties. Device interface is used to expose device's functionality and the properties set are used to known its state.
<br>
To integrate a new device with the iCasa framework, this device has to implement the GenericDevice interface (_fr.liglab.adele.icasa.device.GenericDevice_). The GenericDevice interface defines methods to get the device serial number (unique ID), to access state and fault values, and to interrogate device to obtain its properties values.
<br>
In addition, each device in the framework has the possibility of notify clients about changes in its state, mainly when the properties' values had been changed using a listener pattern. The device sends events (using _fr.liglab.adele.icasa.device.DeviceListener.DeviceEvent_ interface) to its listeners; a device listener must implement the DeviceListener interface (_fr.liglab.adele.icasa.device.DeviceListener_).
<br>
The framework includes an abstract implementation class for GenericDevice that eases the development of new devices implementation in the framework, the class is _fr.liglab.adele.icasa.simulator.AbstractDevice_.
There exists mainly two types of devices in iCasa: devices using a scope zone and localization (thermometer gets the temperature of a room by example) and devices that are independent of their localization i.e. a bathroom scale.
<br>

## Simulated Devices

Testing and debugging pervasive applications is a difficult task because usually developers have not access to all physical devices. On the other hand is not easy to generate the adequate events in the right moment in order to produce conditions for testing applications in different execution scenarios. For this reason iCasa platform includes a simulation module allowing testing and debugging of pervasive applications. 
The iCasa simulation module provides a set of prebuilt simulated devices, but it is extensible allowing developers to build new types of simulated devices. 
Every simulated device is a full compliant iCasa device, in addition of GenericDevice a simulated device must implement the SimulatedDevice interface to be recognized by the iCasa simulation framework. 
<br>

## Device with scope zone: A Thermometer Device

In this section we will show how to extend the iCasa simulation module adding a new simulated device that uses a scope zone; the thermometer device.
<br>

### Device Description

As show in the figure the description of the Thermometer device includes one Java interface (Thermometer) and a set of properties (state, fault and current_temperature).
The Thermometer java interface is as follows

    package fr.liglab.adele.icasa.device.temperature;

    import fr.liglab.adele.icasa.device.GenericDevice;

    public interface Thermometer extends GenericDevice {

       String THERMOMETER_CURRENT_TEMPERATURE = "current_temperature";

       double getTemperature();

    }

To access device current temperature the client has two options, call the method __getTemperature()__ or ask for the __current_temperature__ property.

<br>

### Device Implementation Class

Once the device interface defined a implementation to this device must be provided. 

    package fr.liglab.adele.icasa.device.temperature.impl;

    import java.util.List;

    import org.apache.felix.ipojo.annotations.Component;
    import org.apache.felix.ipojo.annotations.Invalidate;
    import org.apache.felix.ipojo.annotations.Provides;
    import org.apache.felix.ipojo.annotations.ServiceProperty;
    import org.apache.felix.ipojo.annotations.StaticServiceProperty;
    import org.apache.felix.ipojo.annotations.Validate;
    import org.osgi.framework.Constants;

    import fr.liglab.adele.icasa.device.temperature.Thermometer;
    import fr.liglab.adele.icasa.device.util.AbstractDevice;
    import fr.liglab.adele.icasa.location.Zone;
    import fr.liglab.adele.icasa.location.ZoneListener;
    import fr.liglab.adele.icasa.simulator.SimulatedDevice;
    import fr.liglab.adele.icasa.simulator.listener.util.BaseZoneListener;


    @Component(name = "iCASA.Thermometer")
    @Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
    public class SimulatedThermometerImpl extends AbstractDevice implements Thermometer, SimulatedDevice {

	    @ServiceProperty(name = Thermometer.DEVICE_SERIAL_NUMBER, mandatory = true)
	    private String m_serialNumber;

	    private volatile Zone m_zone;

	    private ZoneListener listener = new ThermometerZoneListener();

	    public SimulatedThermometerImpl() {
		    super();
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
					    m_zone.addListener(listener);
					    break;
				    }
			    }
		    }
	    }

	    @Override
	    public void leavingZones(List<Zone> zones) {
		    setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, null);
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

	    class ThermometerZoneListener extends BaseZoneListener {

		    @Override
		    public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {

			    if (m_zone == zone) {
				    if (!(getFault().equalsIgnoreCase("yes")))
					    if (variableName.equals("Temperature"))
						    getTemperatureFromZone();
			    }
		    }
	    }

    }
	
### Device Properties Initialization
	
The device's properties should be initialized to avoid any problem of null pointer in the device implementation. This initialization is generally done in the constructor.
	
### Zones Callbacks Methods
As is shown in the SimulatedThermometerImpl class two methods have been implemented: enterInZones and leaveZones, these methods are callback methods defined in GenericDevice interface. iCasa platform call these methods when de device is placed in a different Zone to indicate its new scope zones and which are it leaving. In our example the device will try to find the first Zone that contains the "Temperature" variable. 

<br>

### Zone Listener 

In addition our Thermometer implements itself the ZoneListener to be notified when variables in its scope zone have been modified. This listener has to be subscribed to zone events as is done in enterInZones method ( _m_zone.addListener(listener)_).

<br>

## Device Component

The iCasa platform has been built on top of the OSGi and iPOJO technologies. Each device to be incorporated to iCasa framework has to be built as an iPOJO component and deployed in the runtime as an OSGi Bundle.
Some iPOJO annotations are defined in the previous implementation class, these annotations indicates the iPOJO manipulator tool and iPOJO runtime how to deal with the component. In our example we are indicating that the component is called "iCASA.Thermometer" and that it is providing all implemented interfaces (GenericDevice, SimulatedDevice and Thermometer) as OSGi services.
To know more about the iPOJO compoenent model see <a href="http://felix.apache.org/site/apache-felix-ipojo.html">this</a> and for OSGI <a href="http://www.osgi.org">this</a>.

<br>

## Device Building

You can use <a href="http://felix.apache.org/site/apache-felix-ipojo.html">maven</a>  tool to build device projects

Artifacs :

### Context API - Device interfaces

      <groupId>fr.liglab.adele.icasa</groupId>
      <artifactId>platform.parent</artifactId>
      <version>1.0.0-SNAPSHOT</version>

### Simulation API - Simulated Device interface

		<groupId>fr.liglab.adele.icasa</groupId>
		<artifactId>parent</artifactId>
		<version>1.0.0-SNAPSHOT</version>