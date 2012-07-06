/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
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
package org.medical.test.common;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.osgi.service.log.LogService.LOG_WARNING;
import static org.medical.test.common.ICasaHelper.waitForIt;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.ops4j.pax.exam.Constants;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.ops4j.pax.exam.container.def.options.VMOption;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnitOptions;
import org.ops4j.pax.exam.options.FrameworkOption;
import org.ops4j.pax.exam.options.TestContainerStartTimeoutOption;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.ow2.chameleon.rose.ImporterService;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

public abstract class ICasaAbstractTest {
	
	protected static String HTTP_PORT = "8042";
	
    /*
     * Number of mock object by test.
     */
	protected static final int MAX_MOCK = 10;

    @Inject
    protected BundleContext context;

    protected OSGiHelper osgi;
    
    protected IPOJOHelper ipojo;
    
    protected ICasaHelper icasa;
    
	@Mock private LogService logService; //Mock LogService
    @Mock private LogEntry logEntry; //Mock Device

    /**
     * Done some initializations.
     */
    @Before
    public void setUp() {
        osgi = new OSGiHelper(context);
        ipojo = new IPOJOHelper(context);
        icasa = new ICasaHelper(context);
        
        //initialise the annoted mock object
        initMocks(this);
    }

    /**
     * Closing the test.
     */
    @After
    public void tearDown() {
        osgi.dispose();
        ipojo.dispose();
    }
    
    @Configuration
    public static Option[] configure() {
        Option[] platform = options(felix(),systemProperty( "org.osgi.service.http.port" ).value( HTTP_PORT ));

        Option[] bundles = options(provision(
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo").versionAsInProject(),
                mavenBundle().groupId("org.ow2.chameleon.testing").artifactId("osgi-helpers").versionAsInProject(), 
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").versionAsInProject(), 
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.enterprise").versionAsInProject(), 
                mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").versionAsInProject(),
				mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").versionAsInProject()
                )); 

        Option[] r = OptionUtils.combine(platform, bundles);

        return r;
    }
    
    /**
     * Mockito bundles
     */
    @Configuration
    public static Option[] mockitoBundle() {
        return options(JUnitOptions.mockitoBundles());
    }

}
