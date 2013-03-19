# Tutorial: Light Follow Me

// Introduction


## 1. Prerequisite




## 2. Application Development

### 2.1. Project Creation and Skeleton Generation

You need to create and generate the skeleton of the unique class of your application.

1. Create a new iPOJO Project.

2. Configure the iPOJO Preferences. Go to *Windows -> Preferences -> iPOJO Preferences.*

3. Create a new component *BinaryFollowMe*. Open the `metadata.xml` file with the iPOJO Metadata Editor.

4. Click *Add* and change the component name to *BinaryFollowMe*.

5. Add to service dependencies (i.e. required services) with **Multiple** and **Optional** characteristics:

   * one dependency to `BinaryLight` with a field `binaryLights` and bind/unbind methods named respectively `bindBinaryLight` and `unbindBinaryLight`;
   * one dependency to `PresenceSensor` with a field `presenceSensors` and bind/unbind methods named respectively `bindPresenceSensor` and `unbindPresenceSensor`.

6. Add new methods `start` and `stop` for the *Component Lifecycle Callbacks*.

7. Generate the class with right click on your component.

8. Complete the package field.

9. If you have done that successfully, you will have a skeleton like this:

    package com.example.binary.follow.me;

    import fr.liglab.adele.icasa.device.light.BinaryLight;
    import fr.liglab.adele.icasa.device.presence.PresenceSensor;
    import java.util.Map;
    
    public class BinaryLightFollowMeImpl {
    
    	/** Field for binaryLights dependency */
    	private BinaryLight[] binaryLights;
    	/** Field for presenceSensors dependency */
    	private PresenceSensor[] presenceSensors;
    
    	/** Bind Method for null dependency */
    	public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
    		// TODO: Add your implementation code here
    	}
    
    	/** Unbind Method for null dependency */
    	public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
    		// TODO: Add your implementation code here
    	}

    	/** Bind Method for null dependency */
    	public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
    		// TODO: Add your implementation code here
    	}
    
    	/** Unbind Method for null dependency */
    	public void unbindPresenceSensor(PresenceSensor presenceSensor,
    			Map properties) {
    		// TODO: Add your implementation code here
    	}
    
    	/** Component Lifecycle Method */
    	public void stop() {
    		// TODO: Add your implementation code here
    	}
    
    	/** Component Lifecycle Method */
    	public void start() {
    		// TODO: Add your implementation code here
    	}
    
    }

### 2.2. Complete the Code Skeleton

1. In order to be notified when something is modified in the environment, the `BinaryLightFollowMeImpl` class must implement `DeviceListener` interface.

    public class BinaryLightFollowMeImpl implements DeviceListener

2. Add two attributes in this `BinaryLightFollowMeImpl` class:
   * `listBinaryLight` is a list containing all the lights available in the environment;
   * `mapPresenceSensor`is a map containing all the presence sensors available in the environment.
   
3.    


## 3. Application Deployment and Test
