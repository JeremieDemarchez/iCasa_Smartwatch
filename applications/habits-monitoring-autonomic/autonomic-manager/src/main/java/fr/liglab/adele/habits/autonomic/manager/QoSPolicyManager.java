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

package fr.liglab.adele.habits.autonomic.manager;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.Measure;
import fr.liglab.adele.cilia.Node;
import fr.liglab.adele.cilia.NodeCallback;
import fr.liglab.adele.cilia.SetUp;
import fr.liglab.adele.cilia.VariableCallback;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalStateException;
import fr.liglab.adele.cilia.exceptions.CiliaInvalidSyntaxException;
import fr.liglab.adele.cilia.model.MediatorComponent;

/**
 * 
 * @author Denis Morand
 * 
 */
public class QoSPolicyManager extends TimerTask implements NodeCallback, VariableCallback {

	private static final Logger logger = LoggerFactory.getLogger(QoSPolicyManager.class);
	private CiliaContext ciliaContext; /* injected by iPOJO */
	private String FILTER_MEDIATOR_TYPE = "MeasureFilterMediator";
	private Timer timer; /* countdown timer */
	private int m_period; /* unit of time */
	private int m_threshold; /* # filter messages per unit of time */
	private int counterFilterMsg = 0;
	private final Object _lock = new Object();
	private boolean done = false;
	private Node nodeFilter;

	public QoSPolicyManager() {
		timer = new Timer();
	}

	public void start() {
		try {
			/* Register all events level node ( arrival, departure ...) */
			ciliaContext.getApplicationRuntime().addListener("(&(chain=*)(node=*))",
					(NodeCallback) this);
			/* start the countdown Timer */
			timer.schedule(this, m_period, m_period);
			logger.debug("Service [QoS Policies Manager] started");
		} catch (CiliaIllegalParameterException e) {
			logger.error("LDAP filter error  [{}]", e.getMessage());
		} catch (CiliaInvalidSyntaxException e) {
			logger.error("LDAP syntax error [{}]", e.getMessage());
		}
	}

	public void stop() {
		try {
			/* Stop the countdown timer */
			timer.cancel();
			/* unregister callback */
			ciliaContext.getApplicationRuntime().removeListener((NodeCallback) this);
			ciliaContext.getApplicationRuntime().removeListener((VariableCallback) this);
		} catch (CiliaIllegalParameterException e) {
		}
		logger.debug("Service [QoS Policies Manager] stopped");
	}

	/*
	 * return true if the node has the Filter type
	 */
	private boolean isFilterMediator(Node node) throws CiliaIllegalParameterException,
			CiliaIllegalStateException {
		MediatorComponent model = ciliaContext.getApplicationRuntime().getModel(node);
		return (model.getType().compareTo(FILTER_MEDIATOR_TYPE) == 0);
	}

	/* --- NodeCallback --- */
	@Override
	/**
	 * On node arrival , set the monitoring policies 
	 * 
	 */
	public void onArrival(Node node) {
		/* set the monitoring for all node */
		SetUp setup;
		try {
			/* set the monitoring for all node */
			setup = ciliaContext.getApplicationRuntime().nodeSetup(node);
			/*
			 * set the state var , no flow control and buffer size = #5 mesures
			 * stored
			 */
			setup.setMonitoring("process.entry.count", 5, null, true);
			/* if the node is an instacne of Filter */
			if (isFilterMediator(node)) {
				nodeFilter = node;
				/* install the callback on event new measure stored */
				/* set the state var dispatcher.data */
				setup.setMonitoring("process.exit.data", 1, null, true);
				StringBuffer sb = new StringBuffer("(&(chain=*)(node=");
				sb.append(node.nodeId()).append("))");
				ciliaContext.getApplicationRuntime().addListener(sb.toString(),
						(VariableCallback) this);
				logger.info(
						"Node [{}], state Var [process.exit.data] successfully configured",
						node.nodeId());
			}
		} catch (CiliaIllegalParameterException e) {
			logger.error(e.getMessage());
		} catch (CiliaIllegalStateException e) {
			logger.error(e.getMessage());
		} catch (CiliaInvalidSyntaxException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void onDeparture(Node node) {
		/* remove the callback variable */
		try {
			nodeFilter = null;
			if (isFilterMediator(node))
				ciliaContext.getApplicationRuntime().removeListener(
						(VariableCallback) this);
		} catch (CiliaIllegalParameterException e) {
		} catch (CiliaIllegalStateException e) {
		}
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
	/*
	 * Callback on Mediator/Adapter state change (valid , invalid)
	 */
	public void onStateChange(Node node, boolean isValid) {
		MediatorComponent model;
		String version;
		try {
			/* Retreive the model and display de mediator version number */
			model = ciliaContext.getApplicationRuntime().getModel(node);
			version = model.getVersion();
			logger.info("Node [{}] is running , version [{}]", node.nodeId(), version);
		} catch (CiliaIllegalParameterException e) {
		} catch (CiliaIllegalStateException e) {
			logger.error("The node has been removed [{}]", e.getMessage());
		}
	}

	/* --- Callback TimerTask (counterdown) --- */
	/*
	 * Checks if #message filtered > threshold during a unit of time
	 */
	@Override
	public void run() {
		logger.info("Checking the number of measurement filtered (current value ={})",
				counterFilterMsg);
		synchronized (_lock) {
			if (counterFilterMsg >= m_threshold) {
				logger.error("QoS Fault : too much messages filtered");
				/* try one corrective action */
				actionCorrective();
			}
			counterFilterMsg = 0;
		}
	}
	private void actionCorrective() {
		/* perform only modification */
		if (!done) {
			done = true;
			try {
				/* retreive the modele for the relevant node */
				MediatorComponent model = ciliaContext.getApplicationRuntime().getModel(
						nodeFilter);
				/* increase thresold level to 10 points */
				try {
					int i = Integer.parseInt((String) model
							.getProperty("filter.threshold")) + 10;
					model.setProperty("filter.threshold", Integer.toString(i));
					logger.info("Node [{}] thresold set to [{}%]",
							model.getId(), model.getProperty("filter.threshold"));
				} catch (NumberFormatException e) {
					logger.error("Node [{}] has no property [filter.threshold] ",
							nodeFilter.nodeId());
				}
			} catch (CiliaIllegalParameterException e) {
			} catch (CiliaIllegalStateException e) {
			}
		}
	}
	/* --- Callback level State variables --- */
	@Override
	public void onUpdate(Node node, String variable, Measure m) {
		synchronized (_lock) {
			/* A null value in a measure -> no Data set by the processor */
			if (m.hasNoValue())
				counterFilterMsg++;
		}
	}
	@Override
	public void onStateChange(Node node, String variable, boolean enable) {
	}
}
