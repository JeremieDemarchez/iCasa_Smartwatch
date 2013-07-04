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

import javax.inject.Inject;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.LocatedDevice;
import org.osgi.framework.ServiceReference;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DeviceTest extends AbstractDistributionBaseTest {
	
	@Inject
	public BundleContext context;
	
	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}
	
	/**
	 * Test the creation of a new zone.
	 */
	@Test
	public void getDevicesWithoutDevicesTest(){
        ContextManager contextMgr = (ContextManager)getService(context,ContextManager.class);
        if (contextMgr == null){
            Assert.fail("Unable to get ServiceReference for ContextManager");
        }

		List<LocatedDevice> devices = contextMgr.getDevices();
		Assert.assertNotNull(devices);
		Assert.assertEquals(0, devices.size());
	}

    @Test
    public void testDeviceListener(){

    }

}
