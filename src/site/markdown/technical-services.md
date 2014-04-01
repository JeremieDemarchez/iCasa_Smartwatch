# Technical Service Usage

<a name="Technical"></a>
## 1. iCasa technical services

iCasa provides a set of technical services. In order to use them, it is needed to add the following maven dependency:

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>technical.services.api</artifactId>
    <version>1.1.0</version>


<a name="Configuration"></a>
## 2. iCasa preferences service

The iCasa preferences service allows to persist preferences that could be exploited by applications. There are three groups of preferences:

* Global preferences.
* Per-application preferences.
* Per-user preferences.

Any configuration could be retrieved and exploited by any running application. 

### 2.1. Configuration service interface

The service providing such functionality provides the following interface:


    /**
     * The Preferences interface provides a service to store preferences of:
     * <li>Global Preferences</li>
     * <li>Application Preferences</li>
     * <li>User Preferences</li>
     * 
     * @author Gabriel Pedraza Ferreira
     *
     */
    public interface Preferences {
    
    	/**
    	 * Gets the value of a property for a global preference
    	 * @param name the preference name
    	 * @return the value associated to the preference
    	 */
    	Object getGlobalPropertyValue(String name);
    	
    	/**
    	 * Gets the value of a property for a user preference
    	 * @param user the user name
    	 * @param name the preference name
    	 * @return the value associated to the preference
    	 */
    	Object getUserPropertyValue(String user, String name);
    	
    	/**
    	 * Gets the value of a property for a application preference
    	 * @param applicationId the application Id
    	 * @param name the preference name
    	 * @return the value associated to the preference
    	 */
    	Object getApplicationPropertyValue(String applicationId, String name);
    	
    	/**
    	 * Sets the value of a property for a global preference
    	 * @param name the preference name
    	 * @param value the new value associated to the preference
    	 */
    	void setGlobalPropertyValue(String name, Object value);
    	
    	/**
    	 * Sets the value of a property for a user preference
    	 * @param user the user name
    	 * @param name the preference name
    	 * @return the new value associated to the preference
    	 */
    	void setUserPropertyValue(String user, String name, Object value);
    	
    	/**
    	 * Sets the value of a property for a application preference
    	 * @param applicationId the application Id
    	 * @param name the preference name
    	 * @return the new value associated to the preference
    	 */
    	void setApplicationPropertyValue(String applicationId, String name, Object value);
    	
    	/**
    	 * Gets a set of global properties' names
    	 * @return set of properties' name
    	 */
    	Set<String> getGlobalProperties();
    	
    	/**
    	 * Gets a set of user properties' names
    	 * @param user the user name
    	 * @return set of properties' name
    	 */
    	Set<String> getUserProperties(String user);
    	
    	/**
    	 * Gets a set of application properties' names
    	 * @param applicationId the application Id
    	 * @return set of properties' name
    	 */
    	Set<String> getApplicationProperties(String applicationId);
    }
    

### 2.2. Using the configuration service
There are three ways to use this service.
#### 2.2.1. Using the OSGi service

Using iPOJ0 it is as simple as adding a service dependency.

	@Requires
	private Preferences preferenceService;

#### 2.2.2. Using Gogo commands
To see how to use the preferences service, please visit the [Configuration Section](gogo-commands.html#Configuration) in iCasa Gogo Commands.

#### 2.2.3. Using the iCasa scripts
There are the following instructions that can be used into the iCasa scripts:

* set a global preference property

        <set-global-property name="period" value="80.8" type="Float"/>
        
        The type must be one of this [String, Boolean, Integer, Long, Float] (Optional Parameter- Default Value is String)
        
* set an user property

        <set-user-property user="Paul" name="period" value="80.8" type="Float"/>
        
        The type must be one of this [String, Boolean, Integer, Long, Float] (Optional Parameter- Default Value is String)
        
        
* set an application property

        <set-application app="LightFollowMe" name="period" value="80.8" type="Float"/>
        
        The type must be one of this [String, Boolean, Integer, Long, Float] (Optional Parameter- Default Value is String)
  

<a name="PersonLocation"></a>
## 3. iCasa person location lervice

In order to retrieve the occupants in a given zone, iCasa provides the following service interface: `fr.liglab.adele.icasa.simulator.services.PersonLocationService`

    /**
     The PersonLocationService interface allows to introspect a given zone and obtain information about its occupants.
     */
    public interface PersonLocationService {
    
    	/**
    	 * get return the names of the persons in a given zone
    	 * @param zoneId the zoneId
    	 * @return a set with persons' names
    	 */
    	Set<String> getPersonInZone(String zoneId);
    	
    }
    
### 3.1. Using person location service

Using iPOJ0 it is as simple as adding a service dependency.

	@Requires
	private PersonLocationService personLocationService;   
           

<a name="Scheduling"></a>
## 4. iCasa scheduling service

iCasa provides an scheduling service that provides the execution of tasks. This service allows two different tasks:

* Periodic tasks: to run periodically based on the iCasa Clock.
* Scheduled tasks: to run in a specific date based on the iCasa Clock.

The scheduling service uses a whiteboard pattern to ease the use and creation of threads.
So, in order to create iCasa tasks, it is needed only to register OSGi services wich will give the task configuration.

There are two interfaces, one for each kind of task:

* `fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable`
* `fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable`

Both interfaces inherit from a `fr.liglab.adele.icasa.service.scheduler.ICasaRunnable`:

    /**
     * This Interface is the parent interface allowing to schedule tasks using the
     * iCasa Clock {@link fr.liglab.adele.icasa.clock.Clock}
     */
    public interface ICasaRunnable extends Runnable{
        /**
         * Gets the job's group.
         * Jobs sharing a group use the same thread pool.
         * @return the job's group name.
         */
        public String getGroup();
    }
    
The group, represents the threadpool wich will handle the iCasa task.


### 4.1. Periodic tasks

In order to build periodic tasks, an OSGi service must provide the `fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable` interface.

    /**
     * An OSGi service providing this interface will allow to schedule periodic tasks using the
     * iCasa Clock {@link fr.liglab.adele.icasa.clock.Clock}
     */
    public interface PeriodicRunnable extends ICasaRunnable {
        /**
         * Gets the scheduled period.
         * @return the period in milliseconds.
         */
        public long getPeriod();
    
    }
    
#### 4.1.1. Implementing periodic tasks

As said before, in order to register a periodic tasks it is needed to provide the `fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable` interface.

    @Component(name = "MyPeriodicTask")
    @Provides
    @Instantiate
    public class MyPeriodicTasks implements PeriodicRunnable {
        /**
        * Gets the job's group.
        * Jobs sharing a group use the same thread pool.
        * @return the job's group name. If null, it will be added to the default group.
        */
        public String getGroup(){
            return "threadPool-1";
        }
        /**
        * Gets the scheduled period.
        * @return the period in milliseconds.
        */
        public long getPeriod(){
            return 1000; // this tasks will be executed each second.
        }
        /**
        * This method will be called every second.
        */
        public void run(){
            //do some cool stuff here every second.
        }
    
    }
    


### 4.2. Scheduled tasks

In order to build scheduled tasks, an OSGi service must provide the `fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable` interface.

    /**
     * An OSGi service providing this interface will allow to schedule tasks at a fixed date using the
     * iCasa Clock {@link fr.liglab.adele.icasa.clock.Clock}
     */
    public interface ScheduledRunnable extends ICasaRunnable {
        /**
         * Get the date to be scheduled the iCasa tasks using the iCasa Clock.
         * @return the date in milliseconds.
         */
        long getExecutionDate();
    
    }

    
#### 4.2.1. Implementing periodic tasks

As said before, in order to register a periodic tasks it is needed to provide the `fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable` interface.

    @Component(name = "MyScheduledTask")
    @Provides
    public class MyScheduledTasks implements ScheduledRunnable {
       
        /**
        * Injected by iPOJO. The date in string format yyy,MM,dd
        */
        @Property(mandatory = true)
        private String date
        
        private Date dateToExecute;
    
        public MyScheduledTasks(){
            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy, MM, dd");
            dateToExecute = sdf.parse(date);
        }
        /**
        * Gets the job's group.
        * Jobs sharing a group use the same thread pool.
        * @return the job's group name. If null, it will be added to the default group.
        */
        public String getGroup(){
            return "threadPool-2";
        }
        /**
         * Get the date to be scheduled the iCasa tasks using the iCasa Clock.
         * @return the date in milliseconds.
         */
        public long getExecutionDate(){
            dateToExecute.getTime();    
        }
        /**
        * This method will be called once at the specific date.
        */
        public void run(){
            //do some cool stuff here at the specific date.
        }
    
    }
