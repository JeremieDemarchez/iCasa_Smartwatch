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
package org.mqtt.importer;

import org.mqtt.services.MqttGyroscopeServiceImpl;
import org.mqtt.services.MqttService;

import fr.liglab.adele.cream.annotations.provider.Creator;

public class IndexCreator {
	private static @Creator.Field  Creator.Entity<MqttGyroscopeServiceImpl> gyroscopeServiceCreator;
	
	private static Creator.Entity<MqttGyroscopeServiceImpl> getGyroscopeServiceCreator(){
		return gyroscopeServiceCreator;
	}
	public static Creator.Entity<? extends MqttService> getContextCreator(String icasaServiceName){
		if(icasaServiceName.equals("MqttGyroscopeServiceImpl")){
			return gyroscopeServiceCreator;
		}
		return null;
	}
}
