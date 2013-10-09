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
package fr.liglab.adele.icasa.gateway.box.test;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.Exception;
import java.lang.String;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.*;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.box.Box;
import fr.liglab.adele.icasa.service.preferences.Preferences;
import fr.liglab.adele.commons.test.utils.TestUtils;


/**
 * Tests of gateway reification as a box.
 *
 *
 * @author Thomas Leveque
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class GatewayAsBoxTest extends AbstractDistributionBaseTest {

    @Inject
    public BundleContext context;

    public Preferences _preferences;
    
    protected ContextManager _contextMgr;

    @Before
    public void setUp() throws Exception {
        //waitForStability(context); //TODO workaround for NPE bug in waitForStability

        // should wait for these services
        _contextMgr = (ContextManager) waitForService(context, ContextManager.class);
        _preferences = (Preferences) waitForService(context, Preferences.class);
    }

    @After
    public void tearDown() {
        _contextMgr = null;
        _preferences = null;
    }


    @Test
    public void boxExistsTest() {
        Box box = (Box) waitForService(context, Box.class);
        assertNotNull(box);

        String boxId = box.getSerialNumber();
        assertNotNull(boxId);
        assertFalse(boxId.isEmpty());
        assertTrue(boxId.startsWith("gateway-"));
    }

    @Test
    public void boxIdDefinedByGlobalPropTest() {
        Box box = (Box) waitForService(context, Box.class);
        assertNotNull(box);

        String globalPropValue = "myBoxId";
        _preferences.setGlobalPropertyValue(ContextManager.GATEWAY_ID_PROP_NAME, globalPropValue);

        // need to restart boxcomponent
        try {
            Bundle boxCompBundle = null;
            for (Bundle bundle : context.getBundles()) {
                if ("gateway.as.box".equals(bundle.getSymbolicName())) {
                    boxCompBundle = bundle;
                    break;
                }
            }
            if (boxCompBundle == null)
                fail("cannot find gateway as box bundle.");
            boxCompBundle.stop();
            boxCompBundle.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }

        box = (Box) waitForService(context, Box.class);
        assertNotNull(box);

        String boxId = box.getSerialNumber();
        assertEquals("gateway-" + globalPropValue, boxId);
    }

    public Object waitForService(BundleContext context, Class clazz) {
        TestUtils.testConditionWithTimeout(new ServiceExistsCondition(context, clazz), 10000, 20);

        return getService(context, clazz);
    }
    
}
