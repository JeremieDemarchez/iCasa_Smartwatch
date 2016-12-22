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
package configuration;

//TODO : créer un jar indépendant de ce module maven correspondant à la classe ci-dessous
	public class SmartwatchOperations {
		
		//code of services
		private final static int STEP_COUNTER_SERVICE = 0;
		private final static int GYROSCOPE_SERVICE = 1;
		
		//code of methods for each service 
		//STEP_COUNTER_SERVICE
		private final static int GET_NUMBER_OF_STEP = 0;
		private final static int GET_NUMBER_OF_STEP_HISTORY = 1;
		//GYROSCOPE_SERVICE
		private final static int GET_XYZ_AXIS_VALUES = 2;
		private final static int GET_GYROSCOPE_HISTORY = 3;
		private final static int GET_DEVICE_TYPE = 4;
		
		//TODO
		public static String getServiceName(int code){
			switch (code) {
			case 0:
				
				break;
			case 10:
						
				break;
			case 11:
				
				break;

			default:
				break;
			}
			return null;
		}
		
		//TODO
		public static int getServiceCode(String serviceName){
			
			int code = -1;
			if(serviceName.equals("")){
				
			}
			else if(serviceName.equals("")){
				
			}
			return code;
		}
		
		//TODO
		public static String getMethodName(int serviceCode, int methodCode){
			switch (serviceCode) {
			case 0:
				switch (methodCode) {
				case 0:
					
					break;
				default:
					break;
				}
				break;
			case 10:
						
				break;
			case 11:
				
				break;

			default:
				break;
			}
			return null;
		}
		
		//TODO
		public static int getMethodCode(String serviceName, String methodName){
			
			int code = -1;
			
			if(serviceName.equals("MqttGyroscopeService")){
				if(methodName.equals("askXYZAxisValues")){
					code = GET_XYZ_AXIS_VALUES;
				}
				else if(methodName.equals("askHistory")){
					code = GET_GYROSCOPE_HISTORY;
				}
				else if(methodName.equals("askDeviceType")){
					code = GET_DEVICE_TYPE;
				}
			}
			return code;
		}
		
		
		public static String getIcasaServiceName(int code){
			switch (code) {
			case GYROSCOPE_SERVICE:
				return "MqttGyroscopeServiceImpl";
			case STEP_COUNTER_SERVICE:
				return "MqttStepCounterServiceImpl";
			default:
				break;
			}
			return null;
		}
		
		
		public static int getIcasaServiceCode(String serviceName){
			
			if(serviceName.equals("MqttGyroscopeService")){
				return GYROSCOPE_SERVICE;
			}
			else if(serviceName.equals("MqttStepCounterServiceImpl")){
				return STEP_COUNTER_SERVICE;
			}
			return -1;
		}
		
		
		public static String getIcasaMethodName(int serviceCode, int methodCode){
			switch (serviceCode) {
			case GYROSCOPE_SERVICE:
				switch (methodCode) {
				case GET_XYZ_AXIS_VALUES:
					return "askXYZAxisValues";
				case GET_GYROSCOPE_HISTORY:
					return "askHistory";
				case GET_DEVICE_TYPE:
					return "askDeviceType";
				default:
					break;
				}
				break;
			case STEP_COUNTER_SERVICE:
				switch (methodCode) {
				case GET_NUMBER_OF_STEP:
					return "askNumberOfStep";
				case GET_NUMBER_OF_STEP_HISTORY:
					return "askHistoryOfStepCounter";
				case GET_DEVICE_TYPE:
					return "askDeviceType";
				default:
					break;
				}	
				break;
			default:
				break;
			}
			return null;
		}
		
		
		public static int getIcasaMethodCode(String serviceName, String methodName){
			
			if(serviceName.equals("MqttGyroscopeService")){
				if(methodName.equals("askXYZAxisValues")){
					return GET_XYZ_AXIS_VALUES;
				}
				else if(methodName.equals("askHistory")){
					return GET_GYROSCOPE_HISTORY;
				}
				else if(methodName.equals("askDeviceType")){
					return GET_DEVICE_TYPE;
				}
			}
			else if(serviceName.equals("MqttStepCounterServiceImpl")){
				if(methodName.equals("askNumberOfStep")){
					return GET_NUMBER_OF_STEP;
				}
				else if(methodName.equals("askHistoryOfStepCounter")){
					return GET_NUMBER_OF_STEP_HISTORY;
				}
				else if(methodName.equals("askDeviceType")){
					return GET_DEVICE_TYPE;
				}
			}
			return -1;
		}
		
		
		
		
	}