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
		return bundlesIds.contains(bundleSymbolicName);
	}
}
