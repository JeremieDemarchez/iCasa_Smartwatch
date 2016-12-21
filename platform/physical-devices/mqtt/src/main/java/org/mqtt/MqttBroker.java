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
package org.mqtt;

public class MqttBroker {
	private final static String ADDRESS 			= "tcp://iot.eclipse.org";
	private final static String PORT 				= "1883";
	private final static String topicIotService 	= "IOT/AVAILABLE_SERVICES";
	
	public static String getUri(){
		return ADDRESS+":"+PORT;
	}
	public static String getTopicOfIotServices(){
		return topicIotService;
	}
}
