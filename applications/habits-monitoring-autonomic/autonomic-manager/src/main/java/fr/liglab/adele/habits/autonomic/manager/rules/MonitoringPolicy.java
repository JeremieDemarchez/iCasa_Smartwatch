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

package fr.liglab.adele.habits.autonomic.manager.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.Node;
import fr.liglab.adele.cilia.NodeCallback;
import fr.liglab.adele.cilia.SetUp;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalStateException;
import fr.liglab.adele.cilia.exceptions.CiliaInvalidSyntaxException;

/**
 * 
 * @author Denis Morand
 * 
 */
public class MonitoringPolicy implements NodeCallback {
	private static Logger logger = LoggerFactory.getLogger(MonitoringPolicy.class);
	private static final String LDAP_NODE = "(&(chain=*)(node=*))";
	private CiliaContext ciliaContext;

	public MonitoringPolicy() {
	}

	public void start() {
		try {
			/* Register  events level node ( arrival, departure ...) */
			ciliaContext.getApplicationRuntime().addListener(LDAP_NODE, this);
		} catch (CiliaIllegalParameterException e) {
			logger.error("LDAP filter error  [{}]", e.getMessage());
		} catch (CiliaInvalidSyntaxException e) {
			logger.error("LDAP syntax error [{}]", e.getMessage());
		} 
		logger.info("Service 'monitoring policy' started");

	}

	public void stop() {
		try {
			ciliaContext.getApplicationRuntime().removeListener(this);
		} catch (CiliaIllegalParameterException e) {
		}
		logger.info("Service 'monitoring policy' stopped");
	}

	@Override
	public void onArrival(Node node) {
	}

	@Override
	public void onDeparture(Node node) {
	}

	@Override
	public void onModified(Node node) {
	}

	@Override
	public void onBind(Node from, Node to) {
	}

	@Override
	public void onUnBind(Node from, Node to) {
	}
	
	@Override
	public void onStateChange(Node node, boolean isValid) {
		/* Apply the monitoring policy to the node valid  (even if already previously performed)*/
		try {
			SetUp setup = ciliaContext.getApplicationRuntime().nodeSetup(node);
			setup.setMonitoring("scheduler.count", 10, null, true);
			String version = ciliaContext.getApplicationRuntime().getModel(node).getVersion();
			logger.info("Node [{}], version [{}] monitoring policy successfully set ", node.nodeId(),version);

		} catch (CiliaIllegalParameterException e) {
			logger.error("LDAP filter error  [{}]", e.getMessage());
		} catch (CiliaInvalidSyntaxException e) {
			logger.error("LDAP syntax error [{}]", e.getMessage());
		} catch (CiliaIllegalStateException e) {
			logger.error("The node has been removed [{}]", e.getMessage());
		}
	}


}
