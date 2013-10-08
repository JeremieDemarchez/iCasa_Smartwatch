# How to build an iCasa Command

- [Tutorial Requirements](#Requirements)
- [iCasa Command Model](#Model)
    - [iCasa Command Interface](#Interface)
    - [Command Signature](#Signature)
- [iCasa Command Implementation](#Implementation)
    - [Command without parameters](#Implementation_No_Parameters)
	- [Command with parameters](#Implementation_Parameters)
- [iCasa Command Usage](#Usage)
- [Project Packaging](#Packaging)
    - [POM](#POM)
  
<a name="Requirements"></a>
## Tutorial Requirements

- OSGi & iPOJO : iCasa is built on top of [OSGi](http://www.osgi.org) platform and [iPOJO](http://felix.apache.org/site/apache-felix-ipojo.html) component model. This tutorial is made using [iPOJO annotations](http://felix.apache.org/site/how-to-use-ipojo-annotations.html). 
- Maven (Optional) : [Maven](http://maven.apache.org/) tool can be used to build new iCasa commands.
- JSON: The [JSON](http://www.json.org/) format is used to passed parameters to iCasa commands

<a name="Model"></a>
## iCasa Command Model

iCasa platform provides a command architecture allowing extension by third party developers. This architecture is based on the [OSGi whiteboard pattern](http://www.osgi.org/wiki/uploads/Links/whiteboard.pdf). In this tutorial we will show how to add new commands to the iCasa platform.

iCasa Commands could be used two different ways:

* Using the gogo shell. Interacting with the shell it is possible to call [iCasa commands](./gogo-commands.html).
* Using iCasa scripts. It is possible to use the commands from an [iCasa script file](./script.html) .

This benefits the platform in two main ways:

* commands allow to introspect the information of the current simulation (usually in the shell mode).
* commands allow to modify the state of elements in current simulation.


<a name="Interface"></a>
### iCasa Command Interface

In order to add a new command to the iCasa platform, an OSGi service must be provided by the developer. This service has to implement the _fr.liglab.adele.icasaICasaCommand_ interface, it is show below

    /**
     * An ICommandService is a service that can be executed with a set of arguments. It can execute a block of code and then
     * returns the result object of executing this code.
     */
    public interface ICasaCommand {
    	/**
    	 * Execute a block of code and then returns the result from execution. This method should use {@code in} and
    	 * {@code out} stream to print result instead of the direct use of {@code System.in} and {@code System.out}
    	 * 
    	 * @param in : the input stream.
    	 * @param out : the output stream.
    	 * @param param a json object of parameters
    	 * 
    	 * @return the result from the execution
    	 * @throws Exception if anything goes wrong
    	 */
    	Object execute(InputStream in, PrintStream out, JSONObject param) throws Exception;
    
    	/**
    	 * To validate the given parameters.
    	 * 
    	 * @param param The parameters in JSON format
    	 * @return true if parameters are valid, false if not.
    	 * @throws Exception
    	 */
    	boolean validate(JSONObject param, Signature signature) throws Exception;
    
    	/**
    	 * Get the command description.
    	 * 
    	 * @return The description of the Script and command gogo.
    	 */
    	String getDescription();
    
    	/**
    	 * Get the name of the Script and command gogo.
    	 * 
    	 * @return The command name.
    	 */
    	String getName();
    
    	/**
    	 * Get signature by passing the number of arguments..
    	 * 
    	 * @return
    	 */
    	Signature getSignature(int size);
    
    }

* The  _getName()_ method provides the command name (used in the shell as command).
* The _getDescription()_ method is called by the platform when users are looking for help of this command. 
* The _validate(JSONObject param, Signature signature)_ method is invoked to validate parameters provided to the command. The validation is realized against a command signature.
* The _getSignature(int size)_ must return a command signature of the provided size.
* The _execute(InputStream in, PrintStream out, JSONObject param)_ will execute the set of instructions defining the iCasa command. This method is called by the platform after invocation of _validate_ method and only if it returned true. The _in_ and _out_ parameter correspond to the standard input stream and output stream. Finally, the JSON object _param_ contains the values of command parameters.


<a name="Signature"></a>
### Command Signature

A command __signature__ defines number and order of its parameters. When a command is used in the shell arguments are anonymous, iCasa platform determines the parameter correspondence only based in order. On the other hand, if the command is used in a script file, parameters are determined based on their names.

Commands could have more than one signature, however signatures cannot contain the same number of parameters. The _fr.liglab.adele.icasa.Signature_ class is presented in the next code snipped, to create a Signature only is necessary to provide an array of string corresponding to the name of parameters in the expected order (see constructor).

	public class Signature {

		/**
		 * The list of parameters for a given iCasaCommand signature.
		 */
		private final String[] parameters;

		public Signature(String[] params) {
			this.parameters = params;
		}

		/**
		 * Get the list of parameters of this signature.
		 * 
		 * @return the list of parameters.
		 */
		public String[] getParameters() {
			return parameters;
		}
		
		...
	}


<a name="Implementation"></a>
## iCasa Command Implementation
In order to facilitate the creation of iCasa Commands, the abstract class _fr.liglab.adele.icasa.commands.impl.AbstractCommand_  is provided by the iCasa platform. This class implements the _ICasaCommand_ interface, it implements the _validate_ and _getSignature_ methods.

A command implementation should extend the AbstractCommand and implements the _getName_, _getDescription_ and _execute_ methods. In addition, in the constructor of the implementation class must be defined the signatures of the command using the method _addSignature_. Finally, the implementation class must be annotated with the iPOJO annotations to expose itself as OSGi a service, this service will implement the right interface _ICasaCommand_. 

In order to illustrate how to create iCasa commands will show to different examples, the first one using no parameters, and other using parameters.

<a name="Implementation_No_Parameters"></a>
### Command without parameters

The following command displays the list of current devices in the iCasa platform. It does not take any parameter.

		@Component(name = "ShowDevicesCommand")
		@Provides
		@Instantiate(name="show-devices-command")
		public class ShowDevicesCommand extends AbstractCommand {

			@Requires
			private ContextManager manager;

			/**
			 * Get the name of the  Script and command gogo.
			 *
			 * @return The command name.
			 */
			@Override
			public String getName() {
				return "show-devices";
			}

			public ShowDevicesCommand(){
				addSignature(new Signature(new String[0])); // Adding an empty signature, without parameters
			}

			@Override
			public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {
				out.println("Devices: ");
				List<LocatedDevice> devices = manager.getDevices();
				for (LocatedDevice locatedDevice : devices) {
					out.println("Device " + locatedDevice);
				}
				return null;
			}

			@Override
			public String getDescription(){
				return "Shows the list of devices.\n\t" + super.getDescription();
			}

		}

<a name="Implementation_Parameters"></a>
### Command with parameters

The following command displays the details of a particular device. 

		@Component(name = "ShowDeviceCommand")
		@Provides
		@Instantiate(name = "show-device-command")
		public class ShowDeviceInfoCommand extends AbstractCommand {

			@Requires
			private ContextManager simulationManager;

			public ShowDeviceInfoCommand(){
				addSignature(new Signature(new String[]{"deviceId"})); // Adding a signatue with one parameter named deviceId
			}

			/**
			 * Get the name of the  Script and command gogo.
			 *
			 * @return The command name.
			 */
			@Override
			public String getName() {
				return "show-device";
			}

			@Override
			public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {
				String[] params = signature.getParameters();
				String deviceId = param.getString(params[0]);
				out.println("Properties: ");
				LocatedDevice device = simulationManager.getDevice(deviceId);
				if (device==null) {
					throw new IllegalArgumentException("Device ("+ deviceId +") does not exist");
				}
				Set<String> properties = device.getProperties();
				for (String property : properties) {
					out.println("Property: " + property + " - Value: " +device.getPropertyValue(property));
				}
				return null;
			}

			@Override
			public String getDescription(){
				return "Shows the information of a device.\n\t" + super.getDescription();
			}

		}

<a name="Usage"></a>
## iCasa Command Usage

As said before, iCasa Commands has two usages, the first one to be called from the shell, and the other one to be used from an iCasa Script.

###OSGi Shell

To see the parameter order in the shell, we can call the icasa:help command, which will show the commands description, as well as the parameters list.

    g!icasa:help
    
    icasa:show-zones
        Shows the list of zones.
        Parameters: 
        ()
    icasa:move-person
        Move a person to a new X,Y position.
        Parameters: 
        ( personId  newX  newY )
        ( personId  newX  newY  newZ )
    g!
    

So, for example, to move a person, we have two signatures with three and four parameters respectively:

    g! move-person Jean 40 40

and

    g! move-person Jean 50 50 50
    

###iCasa Scripts
iCasa provides a mechanism to execute scripts. This is presented in the [script](script.html) section. But also, the script language can be extended by using commands. So, new commands could be executed in a given script.
The command name (the string returned in the getName() method)  must be represented by an XML tag. And the parameters are represented by the attributes contained in those tags.

For example, it is possible to move a person by putting in the script the following:

    <move-person personId="Jean" newX="40" newY="40"/>
    
or

    <move-person personId="Jean" newX="50" newY="50" newZ="50"/>
    
   
<a name="Packaging"></a>
## Project Packaging

You can use <a href="http://felix.apache.org/site/apache-felix-ipojo.html">maven</a> tool to build a command project. Two iCasa maven artifacts are necessary to build your project, the first one context.api defines the interfaces used in the Command model, the context.impl provides the AbstractCommand class.

Artifacs :

___Context API - iCasa Command interfaces___

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>context.api</artifactId>
    <version>1.1.2-SNAPSHOT</version>

___Context Impl - iCasa Command abstract class___

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>context.impl</artifactId>
    <version>1.1.2-SNAPSHOT</version>

Repositories :	

	<repositories>
	  <repository>
		<snapshots>
		  <enabled>false</enabled>
		</snapshots>
		<id>adele-central-snapshot</id>
		<name>adele-repos</name>
		<url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
	  </repository>
	  <repository>
		<snapshots />
		<id>snapshots</id>
		<name>adele-central-release</name>
		<url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
	  </repository>
	</repositories>

<a name="POM"></a>	
### Command Pom Model File

This is an extract of a maven project using the needed dependencies to build iCasa Commands. Also it shows dependencies for OSGi and iPOJO bundles, as well as their plugins configuration.

    <?xml version="1.0" encoding="UTF-8"?>

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
       <modelVersion>4.0.0</modelVersion>
  
       <!-- Project coordinates -->
       <artifactId>myCommand.project</artifactId>
       <packaging>bundle</packaging>
       <version>1.0.0-SNAPSHOT</version>
       
	   <!-- Project repositories -->
	   <repositories>
	     <repository>
		   <snapshots>
		    <enabled>false</enabled>
		   </snapshots>
		   <id>adele-central-snapshot</id>
		   <name>adele-repos</name>
		   <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
	     </repository>
	    <repository>
		  <snapshots />
		  <id>snapshots</id>
		  <name>adele-central-release</name>
		  <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
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
	         <version>1.10.1</version>
          </dependency>  
          <dependency>
             <groupId>org.apache.felix</groupId>
             <artifactId>org.apache.felix.ipojo.annotations</artifactId>
	         <version>1.10.1</version>
          </dependency>
          <dependency>
            <groupId>fr.liglab.adele.icasa</groupId>
             <artifactId>context.api</artifactId>
	         <version>1.0.1-SNAPSHOT</version>
          </dependency>
          <dependency>
            <groupId>fr.liglab.adele.icasa</groupId>
            <artifactId>context.impl</artifactId>
            <version>1.0.1-SNAPSHOT</version>
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
                   <Private-Package>fr.liglab.adele.icasa.zones.command.impl</Private-Package>
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
