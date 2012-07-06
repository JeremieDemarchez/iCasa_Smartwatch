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
package fr.liglab.adele.icasa.dp.fileinstall;


import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

/**
 * Implementation of a file installer which is in charge of deployment packages.
 *
 * @author Thomas Leveque
 * 
 */
@Instantiate(name="dp-file-installer-1")
@Component(name="dp-file-installer")
@Provides
public class DPFileInstaller implements ArtifactInstaller {
	
	@Requires
	private DeploymentAdmin _deployAdmin;
	
	private Map<String /* file name */, DeploymentPackage> _installedDps = new ConcurrentHashMap<String, DeploymentPackage>();
	
	public boolean canHandle(File file) {
		if (file.getName().endsWith(".dp")) {
			return true;
		}
		return false;
   }

	public void install(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		String fileName = file.getName();
		try {
			DeploymentPackage dp = _deployAdmin.installDeploymentPackage(fis);
			if (dp != null) {
				_installedDps.put(fileName, dp);
			}
			
		} catch (DeploymentException e) {
			e.printStackTrace();
		} finally {
			fis.close();
		}
		
		startBundles();
   }

	private void startBundles() {
		for (DeploymentPackage dp : _deployAdmin.listDeploymentPackages()) {
			for (BundleInfo bundleInfo : dp.getBundleInfos()) {
				String symbolicName = bundleInfo.getSymbolicName();
				Bundle bundle = dp.getBundle(symbolicName);
				int bundleState = bundle.getState();
				if ((bundleState == Bundle.INSTALLED) || (bundleState == Bundle.RESOLVED))
					try {
						bundle.start();
					} catch (BundleException e) {
						// ignore it
					}
			}
		}
	}

	public void uninstall(File file) throws Exception {
		String fileName = file.getName();
		DeploymentPackage dp = _installedDps.get(fileName);
		if (dp != null) {
			try {
				dp.uninstall();
			} catch (DeploymentException e) {
				e.printStackTrace();
			}
			_installedDps.remove(fileName);
		}	
   }

	public void update(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		String fileName = file.getName();
		DeploymentPackage dp = _installedDps.get(fileName);
		try {
			if (dp != null) {
				// do it to ensure that deployment package will be reinstalled if the version remains the same
				try {
					dp.uninstall();
				} catch (DeploymentException e) {
					e.printStackTrace();
				} 
			}
			
			try {
				dp = _deployAdmin.installDeploymentPackage(fis);
			} catch (DeploymentException e) {
				e.printStackTrace();
			}
			if (dp != null) {
				_installedDps.put(fileName, dp);
			}
		} finally {
			fis.close();
		}
		
		startBundles();
   }

}