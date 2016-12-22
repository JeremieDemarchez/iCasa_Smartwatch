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

import fr.liglab.adele.cream.annotations.ContextService;
import fr.liglab.adele.cream.annotations.State;

public @ContextService interface MqttService {
	 /**
     * The distant device id
     */
    static final @State String PROVIDER_ID = "mqtt.providerId";
    
    /**
     * the distant service name
     */
    static final @State String SERVICE_NAME = "mqtt.serviceName";
    
    public String getProviderId();
    
    public String getServiceName();
    
    public void askDeviceType(Consumer<String[]> callback);
}
