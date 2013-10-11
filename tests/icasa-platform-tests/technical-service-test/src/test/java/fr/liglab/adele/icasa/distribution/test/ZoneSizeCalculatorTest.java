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
package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.service.preferences.Preferences;
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import fr.liglab.adele.commons.test.utils.TestUtils;

import javax.inject.Inject;

/**
 * Tests of ZoneSizeCalculator service.
 *
 * @author Thomas Leveque
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ZoneSizeCalculatorTest extends AbstractDistributionBaseTest {

    @Inject
    public BundleContext context;

    public ZoneSizeCalculator _zoneSizeCalculator;

    public ContextManager _contextMgr;

    public Preferences _preferences;

    @Before
    public void setUp() {
        waitForStability(context);

        // should wait for these services
        _contextMgr = (ContextManager) waitForService(context, ContextManager.class);
        _zoneSizeCalculator = (ZoneSizeCalculator) waitForService(context, ZoneSizeCalculator.class);
        _preferences = (Preferences) waitForService(context, Preferences.class);
    }

    @After
    public void tearDown() {
        _zoneSizeCalculator = null;
        _contextMgr = null;
        _preferences = null;
    }

    /**
     * Test scheduling a periodic task.
     */
    @Ignore // It is ignored since it uses preferences service which persist old values.
    @Test
    public void defaultScaleFactorsTest(){
        Assert.assertEquals(0.014f, _zoneSizeCalculator.getXScaleFactor());
        Assert.assertEquals(0.014f, _zoneSizeCalculator.getYScaleFactor());
        Assert.assertEquals(0.014f, _zoneSizeCalculator.getZScaleFactor());
    }

    /**
     * Test scheduling a periodic task.
     */
    @Test
    public void definedByGlobalPropScaleFactorsTest(){

        float xFactor = 0.023f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.X_SCALE_FACTOR_PROP_NAME, xFactor);
        Assert.assertEquals(xFactor, _zoneSizeCalculator.getXScaleFactor());

        float yFactor = 0.034f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Y_SCALE_FACTOR_PROP_NAME, yFactor);
        Assert.assertEquals(yFactor, _zoneSizeCalculator.getYScaleFactor());

        float zFactor = 0.045f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Z_SCALE_FACTOR_PROP_NAME, zFactor);
        Assert.assertEquals(zFactor, _zoneSizeCalculator.getZScaleFactor());
    }

    /**
     * Test scheduling a periodic task.
     */
    @Test
    public void zoneLengthInMetersTest(){

        int xLength = 30;
        int yLength = 80;
        int zLength = 300;
        Zone zone = _contextMgr.createZone("test-zone1", 10, 10, 10, xLength, yLength, zLength);

        float xFactor = 0.023f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.X_SCALE_FACTOR_PROP_NAME, xFactor);
        Assert.assertEquals(xLength * xFactor, _zoneSizeCalculator.getXInMeter(zone.getId()));

        float yFactor = 0.034f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Y_SCALE_FACTOR_PROP_NAME, yFactor);
        Assert.assertEquals(yLength * yFactor, _zoneSizeCalculator.getYInMeter(zone.getId()));

        float zFactor = 0.045f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Z_SCALE_FACTOR_PROP_NAME, zFactor);
        Assert.assertEquals(zLength * zFactor, _zoneSizeCalculator.getZInMeter(zone.getId()));

        //cleanup
        _contextMgr.removeZone(zone.getId());
    }

    /**
     * Test scheduling a periodic task.
     */
    @Test
    public void zoneSurfaceInMeterSquareTest(){

        int xLength = 30;
        int yLength = 80;
        int zLength = 300;
        Zone zone = _contextMgr.createZone("test-zone2", 100, 20, 40, xLength, yLength, zLength);

        float xFactor = 0.023f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.X_SCALE_FACTOR_PROP_NAME, xFactor);

        float yFactor = 0.034f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Y_SCALE_FACTOR_PROP_NAME, yFactor);

        float zFactor = 0.045f;
        _preferences.setGlobalPropertyValue(ZoneSizeCalculator.Z_SCALE_FACTOR_PROP_NAME, zFactor);

        Assert.assertEquals(xLength * xFactor * yLength * yFactor, _zoneSizeCalculator.getSurfaceInMeterSquare(zone.getId()));

        //cleanup
        _contextMgr.removeZone(zone.getId());
    }

    public Object waitForService(BundleContext context, Class clazz) {
        TestUtils.testConditionWithTimeout(new ServiceExistsCondition(context, clazz), 20000, 20);

        return getService(context, clazz);
    }
}
