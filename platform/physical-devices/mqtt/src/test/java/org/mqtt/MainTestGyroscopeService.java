package org.mqtt;
import fr.liglab.adele.cream.facilities.ipojo.annotation.ContextRequirement;
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.device.testable.TestReport;
import fr.liglab.adele.icasa.device.testable.Testable;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.mqtt.services.MqttGyroscopeService;
import org.mqtt.services.MqttService;

import java.util.List;
import java.util.function.Consumer;

@CommandProvider(namespace = "mqtt-test")
@Component
@Instantiate
public class MainTestGyroscopeService {

	@Requires(id="testableServices", specification = MqttGyroscopeService.class,optional=false, proxy=false)
	@ContextRequirement(spec = MqttService.class)
	List<MqttGyroscopeService> testableGyroscopeServices;

	@Command
	public void testAskCurrentGyroscopeValue() {
		for (MqttGyroscopeService test : testableGyroscopeServices){
			System.out.println("Ask for current gyroscope value to provider " + test.getProviderId());
			test.askXYZAxisValues(new TestConsumer());
		}
	}

	@Command
	public void testAskGyroscopeHistorye(String nodeId) {
		for (MqttGyroscopeService test : testableGyroscopeServices){
			System.out.println("Ask for gyroscope history to provider " + test.getProviderId());
			test.askHistory(new TestConsumer());
		}
	}


	public class TestConsumer implements Consumer<String[]> {

		public TestConsumer(){
			super();
		}
		@Override
		public void accept( String[] result) {
			System.out.println("Test result on topic " + result[0] + " , RESULT : " + result[1] + "." );
		}
	}
}


