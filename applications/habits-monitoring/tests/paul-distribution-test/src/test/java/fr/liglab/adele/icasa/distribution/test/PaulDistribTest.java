/*
 * Copyright Adele Team LIG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.liglab.adele.icasa.distribution.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.repository;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.cilia.helper.CiliaHelper;
import fr.liglab.adele.cilia.helper.MediatorTestHelper;
import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.habits.monitoring.measure.generator.Measure;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PaulDistribTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;
	
	@Inject
	public SimulationManager simulationManager;
	
	@Inject
	public ContextManager icasa;
	
	@Inject
	public ScriptExecutor scriptExecutor;
	
	@Before
	public void setUp() {
		waitForStability(context);
	}
	
	@After
	public void tearDown() {

	}
	
	public static Option junitAndMockitoBundles() {
        return new DefaultCompositeOption(
                // Repository required to load harmcrest (OSGi-fied version).
                repository("http://repository.springsource.com/maven/bundles/external").id(
                        "com.springsource.repository.bundles.external"),

                // Repository required to load harmcrest (OSGi-fied version).
                repository("http://repo1.maven.org/maven2/").id(
                        "central"),

                // Mockito without Hamcrest and Objenesis
                mavenBundle("org.mockito", "mockito-core", "1.9.5"),
                
                // cilia helper
                mavenBundle("fr.liglab.adele.cilia", "cilia-helper", "1.6.4-SNAPSHOT"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.test.helpers").versionAsInProject(),

                // Hamcrest with a version matching the range expected by Mockito
                mavenBundle("org.hamcrest", "com.springsource.org.hamcrest.core", "1.1.0"),

                // Objenesis with a version matching the range expected by Mockito
                wrappedBundle(mavenBundle("org.objenesis", "objenesis", "1.2"))
                        .exports("*;version=1.2"),

                // The default JUnit bundle also exports Hamcrest, but with an (incorrect) version of
                // 4.9 which does not match the Mockito import. When deployed after the hamcrest bundles, it gets
                // resolved correctly.
                CoreOptions.junitBundles(),

                /*
                 * Felix has implicit boot delegation enabled by default. It conflicts with Mockito:
                 * java.lang.LinkageError: loader constraint violation in interface itable initialization:
                 * when resolving method "org.osgi.service.useradmin.User$$EnhancerByMockitoWithCGLIB$$dd2f81dc
                 * .newInstance(Lorg/mockito/cglib/proxy/Callback;)Ljava/lang/Object;" the class loader
                 * (instance of org/mockito/internal/creation/jmock/SearchingClassLoader) of the current class,
                 * org/osgi/service/useradmin/User$$EnhancerByMockitoWithCGLIB$$dd2f81dc, and the class loader
                 * (instance of org/apache/felix/framework/BundleWiringImpl$BundleClassLoaderJava5) for interface
                 * org/mockito/cglib/proxy/Factory have different Class objects for the type org/mockito/cglib/
                 * proxy/Callback used in the signature
                 *
                 * So we disable the bootdelegation. this property has no effect on the other OSGi implementation.
                 */
                frameworkProperty("felix.bootdelegation.implicit").value("false")
        );
    }
	
	 @Configuration
	    public Option[] configuration() {
		 
		 	List<Option> lst = super.config();
		 	lst.add(junitAndMockitoBundles());
		 	Option conf[] = lst.toArray(new Option[0]); 
		 	return conf;
	    }

	/**
	 * Test the reception of a valid measure.
	 */
	@org.junit.Test
	public void validMeasureTest(){
		
		String firstScript = "demo_config.bhv";
		String secondScript = "init_demo_oseo.bhv";
		
		// instrument a cilia helper class
		CiliaHelper helper = new CiliaHelper(context);
		Assert.assertEquals(true, helper.waitToChain("generator-mesures", 2000));
		MediatorTestHelper transformer = helper.instrumentMediatorInstance("generator-mesures", "transformer", new String[]{"in"}, new String[]{"out"});
		Assert.assertNotNull(transformer);
		
		// init data : run scripts
		ScriptExecutorListener listener = mock(ScriptExecutorListener.class);
		scriptExecutor.addListener(listener);
		
		// execute script for zones and devices
		scriptExecutor.execute(firstScript);
		wait(10000);
		
		Set<String> devices = icasa.getDeviceIds();
		Set<String> zones = icasa.getZoneIds();
		Assert.assertEquals(4, devices.size());
		Assert.assertEquals(4, zones.size());
		
		//verify(listener).scriptStopped(firstScript);
		// execute second script to trigger events
		scriptExecutor.execute(secondScript);
		
		wait(3000);
		scriptExecutor.stop();
		
		Assert.assertEquals(1, transformer.getAmountData());
		Data lastData = transformer.getLastData();
		assertThat(lastData.getContent(), instanceOf(Measure.class));
		
		Measure measure = (Measure) lastData.getContent();
		Assert.assertEquals(measure.getLocalisation(), simulationManager.getPerson("Paul").getLocation());
		assertThat(devices, hasItem(measure.getDeviceId()));
		assertThat(true, equalTo(measure.getReliability() >  (float)50));
		assertThat(true, equalTo((measure.getTimestamp() - scriptExecutor.getStartDate(secondScript)) < 1000));
	}

	/**
	 * wait a specific amount of time.
	 * @param time
	 */
	private void wait(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}