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
package org.mqtt.services;

import java.util.function.Consumer;

import org.mqtt.MqttRequester;

import configuration.SmartwatchOperations;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;

/**
 * Hello world!
 *
 */
@ContextEntity(services = { MqttGyroscopeService.class })
public class MqttGyroscopeServiceImpl implements MqttGyroscopeService{

	@ContextEntity.State.Field(service = MqttService.class,state = MqttGyroscopeService.PROVIDER_ID)
	private String providerId;
	
	@ContextEntity.State.Field(service = MqttService.class,state = MqttGyroscopeService.SERVICE_NAME)
	private String serviceName;
	
	private MqttRequester mqttRequester;
	
	private MqttRequester getMqttRequester(){
		if(mqttRequester == null) mqttRequester = new MqttRequester();
		return mqttRequester;
	}


	@Override
	public String getProviderId() {
		return providerId;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}
	
	@Override
	public void askXYZAxisValues(Consumer<String[]> callback) {
		
		System.out.println("MqttGyroscopeServiceImpl : askXYZAxisValues ");
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askXYZAxisValues");
		if(codeMethod != -1) {
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
			System.out.println("-> succed to run request");
		}
		else{
			System.out.println("-> failed to run request : codeMethod is equal to '-1' (invalid) ...");
		}
	}

	@Override
	public void askHistory(Consumer<String[]> callback) {
		
		System.out.print("MqttGyroscopeServiceImpl : askHistory");
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askHistory");
		if(codeMethod != -1) {
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
			System.out.println("-> succed to run request");
		}
		else{
			System.out.println("-> failed to run request : codeMethod is equal to '-1' (invalid) ...");
		}
	}

	@Override
	public void askDeviceType(Consumer<String[]> callback) {
		
		System.out.print("MqttGyroscopeServiceImpl : askDeviceType");
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askDeviceType");
		if(codeMethod != -1) {
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
			System.out.println("-> succed to run request");
		}
		else{
			System.out.println("-> failed to run request : codeMethod is equal to '-1' (invalid) ...");
		}
	}
}
