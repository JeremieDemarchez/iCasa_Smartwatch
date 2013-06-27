# Technical Service Usage
- [Technical Services](#Technical)
- [Preference Service](#Preference)
- [Scheduling Service](#Scheduling)
  


<a name="Technical"></a>
## iCasa Technical Services

iCasa provides a set of technical services. In order to use them, it is needed to add the following maven dependency:

    <groupId>fr.liglab.adele.icasa</groupId>
    <artifactId>technical.service.api</artifactId>
    <version>1.1.0</version>


<a name="Preference"></a>
## iCasa Configuration Service

To see how to use the configuration service, please visit the [Configuration Section](gogo-commands.html#Configuration) in iCasa Gogo Commands.

<a name="Scheduling"></a>
## iCasa Scheduling Service

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


### Periodic tasks

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
    
#### Implementing Periodic Tasks

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
    


### Scheduled tasks

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

    
#### Implementing Periodic Tasks

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
