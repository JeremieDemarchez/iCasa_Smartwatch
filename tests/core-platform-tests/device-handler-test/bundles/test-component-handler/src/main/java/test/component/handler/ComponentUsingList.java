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
package test.component.handler;


import java.util.List;

import org.apache.felix.ipojo.annotations.Component;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

@Component(name="ComponentUsingList")
public class ComponentUsingList {
	
	@RequiresDevice(id="lights", type="field", specification="fr.liglab.adele.icasa.device.light.BinaryLight")
	private List<BinaryLight> lights;

	protected void start() {
	    BinaryLight.class.getName();
	    System.out.println("Vector Size : " + lights.size());
    }
	
	public List getLights() {
        return lights;
    }
	
}
