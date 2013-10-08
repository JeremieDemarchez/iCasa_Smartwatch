/*
 * Copyright ADELE Research Group LIG
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
package fr.imag.adele.appstore.gateway.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.service.deploymentadmin.DeploymentPackage;

/**
 * @author <a href="mailto:cilia-devel@lists.ligforge.imag.fr">Cilia Project
 *         Team</a>
 */
public class DPResourceManipulator {

	
	public static Map<String, String> toMap(DeploymentPackage dpackage){
		Map<String, String> resultMap = new Hashtable<String, String>();
		resultMap.put("version", dpackage.getVersion().toString());
		resultMap.put("description", dpackage.getDisplayName());
		resultMap.put("name", dpackage.getName());
		if(dpackage.getIcon() != null) {
			resultMap.put("iconUrl", dpackage.getIcon().toExternalForm());
		}
		return resultMap;
	}
	
	public static List <Map<String, String>> toList(DeploymentPackage[] packages) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		for (DeploymentPackage dpackage : packages) {
			resultList.add(DPResourceManipulator.toMap(dpackage));
		}
		return resultList;
	}
}
