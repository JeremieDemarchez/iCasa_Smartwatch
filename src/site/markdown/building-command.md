# How to build an iCasa Command

- [Tutorial Requirements](#Requirements)
- [iCasa Command Model](#Model)
- [iCasa Command Interface](#Interface)
    - [Command Signature](#Signature)
- [iCasa Command Implementation](#Implementation)
    - [Command Parameters](#Parameters)
- [iCasa Command Usage](#Usage)
- [Project Packaging](#Packaging)
    - [POM](#POM)
  
<a name="Requirements"></a>
## Tutorial Requirements

- OSGi & iPOJO : iCasa is built on top of [OSGi](http://www.osgi.org) platform and [iPOJO](http://felix.apache.org/site/apache-felix-ipojo.html) component model. This tutorial is made using [iPOJO annotations](http://felix.apache.org/site/how-to-use-ipojo-annotations.html). 
- Maven (Optional) : [Maven](http://maven.apache.org/) tool can be used to build new iCasa commands.
- JSON: The [JSON](http://www.json.org/) format is used to passed parameters

<a name="Model"></a>
## iCasa Command Model

iCasa provides an useful architecture, based on the whiteboard pattern, allowing the use of third party commands. This benefits the platform in two main ways:

* first, commands permits to introspect the information of the current simulation.
* second, commands permits to modify the state of any element in the simulation.

Commands could be used by iCasa in two different situations:

* Using the shell. Interacting with the shell it is possible to call iCasa commands.
* Using iCasa scripts. It is possible to call the commands from an iCasa script file.




<a name="Interface"></a>
## iCasa Command Interface

iCasa commands must be OSGi services implementing the following ICasaCommand interface. 

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
    	Signature getSignature(int arguments);
    
    }


The most important method is `execute`, which will execute the set of instructions defining the iCasa command.


<a name="Signature"></a>
### Command Signature

Commands could have more than one signature. That is, it is possible that a command behaves different based on the parameters used when called. However, the signature is based only on the parameters number, since using the shell, the parameters are anonymous.

In the command constructor, the setSignature(...) method should be called in order to register each signature. Even if the command does not receive parameters, an empty signature should be added.
For example:

    public MyCommand(){
        Signature empty = new Signature(new String[0]));//Empty signature
        Signature oneParam = new Signature(new String[]{"device-id"});//A signature with one parameter.
        setSignature(empty); 
        setSignature(oneParam);
    }

Signatures are used calling commands using the shell and also using the iCasa script files.

* Using the shell the parameters are anonymous, so the order of the parameters should be respected.
* Using the scrips, the order is not important, but the parameter name should be respected.


<a name="Implementation"></a>
## Implementation using an abstract class.
In order to facilitates the creation of iCasa Commands, an abstract implementation has been made. This facilitates for example the validation of each call. So, it is hardly recommended to extends such abstract class called `fr.liglab.adele.icasa.commands.impl.AbstractCommand`. This abstract class implements the _ICasaCommand_ interface.


Using the abstract class, a command should be as follow:


    package fr.liglab.adele.icasa.zones.command.impl;

    //Imports section omitted.
    
    /**
    *Prints in the console the list of zones.
    */
    @Component(name = "ShowZonesCommand")
    @Provides
    @Instantiate(name="show-zones-command")
    public class ShowZonesCommand extends AbstractCommand {
    
    
        @Requires
        private ContextManager manager;
    
        /**
        *The command name.
        */
        private static final String NAME= "show-zones";
    
        public ShowZonesCommand(){
            setSignature(EMPTY_SIGNATURE); //Signature with empty parameters.
        }
    
        /**
         * Get the name of the  Script and command gogo.
         *
         * @return The command name.
         */
        @Override
        public String getName() {
            return NAME;
        }
    
        /**
        * The execution of the command.
        */
        @Override
        public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {
            out.println("Zones: ");
            List<Zone> zones = manager.getZones();
            for (Zone zone : zones) {
                out.println("Zone " + zone.getId() + " : " + zone);
            }
            return null;
        }
        /**
        * This method should be overrided to be used when displaying the command help.
        */  
        @Override
        public String getDescription(){
            return "Shows the list of zones.\n\t" + super.getDescription();
        }
    }

     
<a name="Parameters"></a>
### Command parameters

The method to implement is `execute`. This method receives four parameters:

* InputStream in: Usually it is the standard input stream.
* OutputStream out: Usually it is the standard ouptut stream, to print in the console for example the result, or the execution progress.
* JSONObject param: It is the parameters in a JSON format.
* Signature signature: It is the signature used when calling the execute.

The abstract class has validated that the passed parameters is conformed with the given signature.

<a name="Usage"></a>
## iCasa Command Usage

As said before, iCasa Commands has two usages, the first one to be called from the shell, and the other one to be used from an iCasa Script.

###OSGi Shell
The `getName()` method returns the command method. This is the name of the command to be called from the shell. The parameters are annonymous, but they must respect the parameter order as described for each signature.

To see the parameter order, we can call the icasa:help command, which will show the command description, as well as the parameters.

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
    <version>1.0.1-SNAPSHOT</version>

___Context Impl - iCasa Command abstract class___

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>context.impl</artifactId>
    <version>1.0.1-SNAPSHOT</version>

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
