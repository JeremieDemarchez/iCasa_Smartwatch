/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulation.test.scheduled;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.ow2.chameleon.runner.test.ChameleonRunner;

import javax.inject.Inject;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


@RunWith(ChameleonRunner.class)
public class SimulatedScheduledTest {

    static final long ONE_MINUTE=60000;

    @Inject
    public BundleContext context;

    @Inject
    public Clock clock;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    /**
     * Test the creation of a new zone.
     */
    @Test
    public void scheduleAPeriodictaskTest(){
        clock.setFactor(60);
        clock.resume();
        PeriodicRunnable runnable = new PeriodicScheduledTestTask(clock,1,TimeUnit.MINUTES);
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
        clock.setFactor(60);
        clock.resume();
        Date now = new Date();
        Date scheduledTime = new Date(now.getTime() + ONE_MINUTE*5);//Scheduled in 5 min from now

        ScheduledTestTask runnable = new ScheduledTestTask(clock, scheduledTime);
        ServiceRegistration register = context.registerService(ScheduledRunnable.class.getName(), runnable, new Hashtable());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        register.unregister();

    }

}
