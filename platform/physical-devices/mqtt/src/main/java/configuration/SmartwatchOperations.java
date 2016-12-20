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
				return "MqttGyroscopeService";
			default:
				break;
			}
			return null;
		}
		
		
		public static int getIcasaServiceCode(String serviceName){
			
			if(serviceName.equals("MqttGyroscopeService")){
				return GYROSCOPE_SERVICE;
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
			return -1;
		}
		
		
		
		
	}