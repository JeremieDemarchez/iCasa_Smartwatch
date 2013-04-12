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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.repository;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PaulDistribTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;
	
//	@Mock
//	public ScriptExecutorListener listener;

	@Inject
	public ContextManager icasa;
	
	@Inject
	public ScriptExecutor scriptExecutor;
	
	@Before
	public void setUp() {
		//MockitoAnnotations.initMocks(this);
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
	 * Test the creation of a new zone.
	 */
	@org.junit.Test
	public void creationZoneTest(){
		
		System.out.println("1");
		String firstScript = "demo_config.bhv";
		// init data : run scripts
		ScriptExecutorListener listener = mock(ScriptExecutorListener.class);
		scriptExecutor.addListener(listener);
		
		scriptExecutor.getScriptList();
		for (String script : scriptExecutor.getScriptList()){
			System.out.println("script n : " + script);
		}
		System.out.println("2");
		
		// execute script for zones and devices
		scriptExecutor.execute(firstScript);
		System.out.println("3");
		
		verify(listener).scriptStopped(firstScript);
		
		
		Set<String> devices = icasa.getDeviceIds();
		System.out.println("devices length : " + devices.size());
		for (String device : devices){
			System.out.println("device n : " + device);
		}
		System.out.println("4");
		
//		String zone_id_0 = "myZone-0";
//		int zone_0_scope = 5;
//		Position positionZone_0 = new Position(0,0);
//		Zone zone_0 = icasa.createZone(zone_id_0, positionZone_0, zone_0_scope);
//		//Test the zone and its Id. 
//		Assert.assertNotNull(zone_0);
//		Assert.assertEquals(zone_id_0, zone_0.getId());
	}

}