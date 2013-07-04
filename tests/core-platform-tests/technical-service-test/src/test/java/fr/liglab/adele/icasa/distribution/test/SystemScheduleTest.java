/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research Group
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.inject.Inject;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SystemScheduleTest extends AbstractDistributionBaseTest {

    static final long ONE_SECOND=1000;

	@Inject
	public BundleContext context;

    @Inject
    public Clock clock;
	
	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}

    public static Option helpBundles() {

        return new DefaultCompositeOption(
                systemProperty( "iCasa.ThreadPool.default.maxThread" ).value( "10" ),
                systemProperty( "iCasa.ThreadPool.group1.maxThread" ).value( "3" ),
                systemProperty( "iCasa.ThreadPool.group2.maxThread" ).value( "5" )
        );
    }

    @org.ops4j.pax.exam.Configuration
    public Option[] configuration() {

        List<Option> lst = super.config();
        lst.add(helpBundles());
        Option conf[] = lst.toArray(new Option[0]);
        return conf;
    }

	/**
	 * Test the creation of a new zone.
	 */
	@Test
	public void scheduleAPeriodictaskTest(){

        PeriodicRunnable runnable = new PeriodicScheduledTestTask(clock);
        ServiceRegistration register = context.registerService(PeriodicRunnable.class.getName(), runnable, new Hashtable());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        register.unregister();

    }


    /**
     * Test the creation of a new zone.
     */
    @Test
    public void scheduleTaskTest(){

        Date now = new Date();
        Date scheduledTime = new Date(now.getTime() + ONE_SECOND*5);//Scheduled in 5 seconds from now

        ScheduledTestTask runnable = new ScheduledTestTask(clock, scheduledTime, "group2");
        ServiceRegistration register = context.registerService(ScheduledRunnable.class.getName(), runnable, new Hashtable());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        register.unregister();

    }

    public void testGroupConfiguration(){
        //TODO: use a service to introspect configurations and reports.
        PeriodicRunnable runnable = new PeriodicScheduledTestTask(clock);
        ServiceRegistration register = context.registerService(PeriodicRunnable.class.getName(), runnable, new Hashtable());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        register.unregister();
    }

}
