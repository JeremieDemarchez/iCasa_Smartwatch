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
package fr.liglab.adele.icasa.device.handler.test;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.IOException;
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
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.handler.test.mock.devices.BinaryLightMockImpl;
import fr.liglab.adele.icasa.device.handler.test.mock.devices.ThermometerMockImpl;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

/**
 * 
 * @author Gabriel
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BaseDeviceHandlerTest extends AbstractDistributionBaseTest {

    @Inject
    public BundleContext context;

    protected DeploymentAdmin dpAdmin;

    protected AccessManager accessManager;
    
    protected ContextManager contextManager;
    
    protected final String TEST_APPLICATION_NAME = "test-handler-app";

    @Before
    public void setUp() throws Exception {
        waitForStability(context);
        dpAdmin = (DeploymentAdmin) getService(context, DeploymentAdmin.class);
        accessManager = (AccessManager) getService(context, AccessManager.class);
        contextManager = (ContextManager) getService(context, ContextManager.class);
        installDeploymentPackage("test-handler-app-dp-1");
    }

    @After
    public void tearDown() {

    }
    
    @Configuration
    public Option[] configuration() {
        List<Option> lst = super.config();
        lst.add(new DefaultCompositeOption(systemProperty(Constants.DISABLE_ACCESS_POLICY_PROPERTY).value(getAccessPolicyPropertyValue().toString())));
        Option conf[] = lst.toArray(new Option[0]);
        return conf;
    }
    

    protected Boolean getAccessPolicyPropertyValue() {
        return Boolean.FALSE;
    }

    protected ComponentInstance createComponentInstance(String factoryName) throws Exception {
        Factory factory = (Factory) getService(context, Factory.class, "(factory.name=" + factoryName + ")");
        assertNotNull(factory);

        ComponentInstance instance = factory.createComponentInstance(null);
        assertNotNull(instance);

        return instance;
    }

    protected GenericDevice createBinaryLigth(String deviceId) {
        BinaryLightMockImpl mockImpl = new BinaryLightMockImpl(deviceId);

        Dictionary properties = new Hashtable();
        properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

        context.registerService(new String[] { GenericDevice.class.getName(), BinaryLight.class.getName() }, mockImpl,
                properties);
        
        return mockImpl;
    }

    protected GenericDevice createThermometer(String deviceId) {
        ThermometerMockImpl mockImpl = new ThermometerMockImpl(deviceId);

        Dictionary properties = new Hashtable();
        properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

        context.registerService(new String[] { GenericDevice.class.getName(), Thermometer.class.getName() }, mockImpl,
                properties);
        
        return mockImpl;
    }

    private URL getDeploymentPackageArtifactURL(String artifactID) throws MalformedURLException {
        MavenArtifactProvisionOption option = mavenBundle().groupId("fr.liglab.adele.icasa").artifactId(artifactID)
                .type("dp").versionAsInProject();
        return new URL(option.getURL());
    }

    protected DeploymentPackage installDeploymentPackage(String dpName) throws DeploymentException, IOException {

        URL url = getDeploymentPackageArtifactURL(dpName);

        return dpAdmin.installDeploymentPackage(url.openStream());
    }

    private void printBundles(String header) {
        Bundle[] bundles = context.getBundles();

        System.out.println("=================== " + header + " =================");
        System.out.println("Total " + bundles.length);
        for (Bundle bundle : bundles) {
            System.out.println(" ---> " + bundle.getSymbolicName());
        }

    }
    


    @Test
    public void simpleTest() {
        // Only created to avoid JUnit error
    }
    
}
