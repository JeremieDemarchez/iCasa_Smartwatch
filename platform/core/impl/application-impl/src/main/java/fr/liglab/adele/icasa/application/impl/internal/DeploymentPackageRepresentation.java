package fr.liglab.adele.icasa.application.impl.internal;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentPackage;

public class DeploymentPackageRepresentation {


	private String name;
	private Version version;
	
	private Set<String> bundlesIds;
	
	private DeploymentPackageRepresentation() {
		
	}
	
	public static DeploymentPackageRepresentation builFromDeploymentPackage(DeploymentPackage dp) {
		DeploymentPackageRepresentation dpr = new DeploymentPackageRepresentation();
		dpr.name = dp.getName();
		dpr.version = dp.getVersion();
		dpr.bundlesIds = new HashSet<String>();
		BundleInfo[] bundleInfos = dp.getBundleInfos();
		for (BundleInfo bundleInfo : bundleInfos) {
			dpr.bundlesIds.add(bundleInfo.getSymbolicName());
		}
		return dpr;
	}
		
	public Set<String> getBundleIds() {
		return bundlesIds;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}
	
	public boolean constainsBundle(String bundleSymbolicName) {
		if (bundlesIds.contains(bundleSymbolicName))
			return true;
		return false;
	}
}
