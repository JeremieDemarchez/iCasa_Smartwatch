package fr.liglab.adele.icasa.simulation.test.util;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.util.List;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;

public abstract class IPojoApiBaseTest extends AbstractDistributionBaseTest {

	@Override
	public List<Option> config() {
		List<Option> options = super.config();
		options.add(ipojoApiBundles());
		return options;
	}

	protected CompositeOption ipojoApiBundles() {
		CompositeOption iPojoAPICoreConfig = new DefaultCompositeOption(mavenBundle().groupId("org.apache.felix")
		      .artifactId("org.apache.felix.ipojo.api").versionAsInProject());
		return iPojoAPICoreConfig;
	}
	
}
