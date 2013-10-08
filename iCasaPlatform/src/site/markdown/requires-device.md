# Device Injection in iCasa Platform

The iCasa platform provides a development and access model to build digital home applications, in this model, applications must be created using the [iPOJO](http://felix.apache.org/site/apache-felix-ipojo.html) component model and packaged into OSGi deployment packages.

In iCasa development model any component requiring access to platform devices must use the special annotation _@RequiresDevice_ to indicate the platform its requirement. The platform is charged of watch if the application has rights on devices, and injects device dependencies into the component.

The __@RequiresDevice__ annotation must specify a unique identifier, the _id_ attribute. It must also specify the utilization mode in the _type_ attribute, it can be field, bind or unbind. If the type is field, the device is injected in a field, bind or unbind is used in methods as callbacks when the device is injected to the component or removed from it.

In the next example the component LightFollowMeApplication requires a set of devices of type PowerSwitch. In addition, two callbacks methods are indicate to bind and unbind operation, the _RequiresDevice_ annotation is also used  but type indicate are bind and unbind respectively.


      @Component(name="LightFollowMeApplication")
      @Instantiate
      public class LightFollowMeApplication extends EmptyDeviceListener {

          /** Field for powerSwitches dependency */
          @RequiresDevice(id="powerSwitches", type="field", optional=true)
          private PowerSwitch[] powerSwitches;

          /** Bind Method for powerSwitches dependency */
          @RequiresDevice(id="powerSwitches", type="bind")
          public void bindPowerSwitch(PowerSwitch powerSwitch, Map properties) {
              powerSwitch.addListener(this);
          }

          /** Unbind Method for powerSwitches dependency */
          @RequiresDevice(id="powerSwitches", type="unbind")    
          public void unbindPowerswitch(PowerSwitch powerSwitch, Map properties) {
              powerSwitch.removeListener(this);
          }
      }
