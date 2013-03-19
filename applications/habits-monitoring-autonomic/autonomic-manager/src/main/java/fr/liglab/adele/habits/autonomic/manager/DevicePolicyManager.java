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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.ipojo.Factory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.Node;
import fr.liglab.adele.cilia.builder.Builder;
import fr.liglab.adele.cilia.exceptions.BuilderConfigurationException;
import fr.liglab.adele.cilia.exceptions.BuilderException;
import fr.liglab.adele.cilia.exceptions.BuilderPerformerException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.Position;

/**
 * Contexte adaptation
 * 
 * @author Denis Morand
 * 
 */
public class DevicePolicyManager implements LocatedDeviceListener, BundleListener {
	private static Logger logger = LoggerFactory.getLogger(DevicePolicyManager.class);
	private CiliaContext ciliaContext;
	private ContextManager iCasaContext;
	private BundleContext bundleContext;
	private Properties configDevices,configMediators;
	private String m_devicesProperties;
	private String m_mediatorsProperties;

	/* List of asynchronous job ID */
	private Map<String, String> phaseThreePending;
	private static final String MEDIATOR_RELIABILITY = "MeasureReliabilityMediator";
	private static final String MEDIATOR_RELIABILITY_V2 = "MeasureReliabilityMediator-V2";

	public DevicePolicyManager(BundleContext bc) {
		bundleContext = bc;
		configDevices = new Properties();
		configMediators = new Properties() ;
		phaseThreePending = new HashMap<String, String>();
	}

	public void start() {
		/* installing callback on simulated devices */
		iCasaContext.addListener(this);
		/* loading configuration file : devices allowed to be configured */
		if (m_devicesProperties != null) {
			try {
				configDevices.load(new URL(m_devicesProperties).openStream());
			} catch (MalformedURLException e) {
				logger.error("Invalid URL");
			} catch (IOException e) {
				logger.error("url {} is not existing", m_devicesProperties);
			}
		}
		logger.info("Service {} started ", this.getClass().getSimpleName());
	}

	public void stop() {
		iCasaContext.removeListener(this);
		bundleContext.removeBundleListener(this);
		logger.info("Service {} stopped ", this.getClass().getSimpleName());
	}

	@Override
	/**
	 * @param device created by the simulator
	 */
	public void deviceAdded(LocatedDevice device) {
		final String adapterType;
		final String deviceType = device.getType();
		logger.info ("*** Running phase 1 ***") ;
		logger.info("Device discovered type [{}], serial number [{}] ", deviceType,
				device.getSerialNumber());
		/* retreive the name (or null) of the Cilia adapter */
		adapterType = configDevices.getProperty(deviceType);

		if (adapterType == null) {
			logger.info("The type [{}] is not allowed to be configured ",
					device.getType());
			logger.info("*** End phase 1 ***") ;
		} else {
			/* Spwan asynchronous job : Phase One */
			Runnable task = new Runnable() {
				@Override
				public void run() {
					/* Check if the code is available in the gateway */
					phaseTwo(adapterType);
				}
			};
			new Thread(task).start();
		}
	}

	@Override
	public void deviceRemoved(LocatedDevice device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deviceMoved(LocatedDevice device, Position oldPosition) {
		// TODO Auto-generated method stub
	}

	@Override
	public void devicePropertyModified(LocatedDevice device, String propertyName,
			Object oldValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void devicePropertyAdded(LocatedDevice device, String propertyName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
		// TODO Auto-generated method stub
	}

	@Override
	/**
	 * OSGi callback : bundle events 
	 */
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.INSTALLED) {
			logger.info("Bundle {} installed successfully", event.getBundle()
					.getSymbolicName());
			try {
				event.getBundle().start();
			} catch (BundleException e) {
				logger.error("Error while starting the bundle {} : message :{}", event
						.getBundle().getSymbolicName(), e.getMessage());
				bundleContext.removeBundleListener(this);
				/* Remove the phase 2 pending */
				phaseThreePending.remove(event.getBundle().getLocation());
				e.printStackTrace();
			}
		}
		if (event.getType() == Bundle.ACTIVE) {
			logger.info("Bundle {} started successfully", event.getBundle()
					.getSymbolicName());
			/* remove the callback */
			bundleContext.removeBundleListener(this);
			/*
			 * spwan an Phase 2 asynchronous Job Cilia modification topology
			 */
			final String adapterType = phaseThreePending.get(event.getBundle()
					.getLocation());
			/* Spwan asynchronous job : Running Phase Two */
			Runnable task = new Runnable() {
				@Override
				public void run() {
					/* Check if the code is available in the gateway */
					phaseThree(adapterType);
				}
			};
			new Thread(task).start();
		}
	}

	/* ckeck if the factory for the Cilia component is existing */
	private boolean isBundleMissing(String type) throws InvalidSyntaxException {
		ServiceReference[] sr = null;
		return (bundleContext.getServiceReferences(Factory.class.getName(),
				("(factory.name=" + type + ")")) == null);
	}

	/* Installing the missing code */
	private void phaseTwo(String adapterType) {
		try {
			logger.info("*** End phase 1 ***") ;
			logger.info("*** Running phase 2 *** ");
			if (!isBundleMissing(adapterType)) {
				logger.info("Code for Cilia component [{}] is already installed in the gateway",
						adapterType);
				/*
				 * running immediately the phase 2 : Cilia component
				 * installation
				 */
				phaseThree(adapterType);
			} else {
				/*
				 * getting the bundle name to install
				 */
				logger.info(
						"Code for Cilia component [{}] is not installed in the gateway",
						adapterType);
				String location = configDevices.getProperty(adapterType);
				if (location != null) {
					logger.info("Code found for the Cilia component [{}] in bundle [{}]",
							adapterType, location);
					/*
					 * Store relation bundle/adapter, for asynchronous run Phase
					 * three
					 */
					phaseThreePending.put(location, adapterType);
					/*
					 * install the callback : Phase three will run when bundle
					 * state is ACTIVE
					 */
					bundleContext.addBundleListener(this);
					/* install the bundle in the gateway */
					bundleContext.installBundle(location);

				} else {
					logger.info("No code found for the Cilia component [{}]", adapterType);
				}
			}
		} catch (BundleException e) {
			bundleContext.removeBundleListener(this);
			logger.error("Error while installing bundle " + e.getMessage());
		} catch (InvalidSyntaxException e) {
			logger.error("Syntax error " + e.getMessage());
		}
	}

	/*
	 * create cilia components ( instance ), adapter & mediator
	 */
	private void phaseThree(String adapterType) {
		logger.info("*** End phase 2 *** ");
		try {
			Node[] node = ciliaContext.getApplicationRuntime().nodeByType(adapterType);
			if (node.length == 0) {
				/* load the configuration file list of mediators to change , replace , update */
				if (m_mediatorsProperties != null) {
					try {
						configMediators.load(new URL(m_mediatorsProperties).openStream());
					} catch (MalformedURLException e) {
						logger.error("Invalid URL");
					} catch (IOException e) {
						logger.error("url {} is not existing", m_mediatorsProperties);
					}
				}
				/* no type instancied */
				try {
					logger.info("*** Running phase 3 *** ");
					String chainId[] = ciliaContext.getApplicationRuntime().getChainId();
					/* get the builder */
					Builder builder = ciliaContext.getBuilder();
					/* Instanciate an adapter type */
					logger.info("Create an instance of adapter type [{}]", adapterType);

					builder.get(chainId[0]).create().adapter().type(adapterType)
							.category("medical").id(buildInstanceName(adapterType));
					/* instanciate a mediator reliability V2 */
					logger.info("Create an instance of Mediator type [{}]",
							MEDIATOR_RELIABILITY_V2);
					builder.get(chainId[0]).create().mediator()
							.type(MEDIATOR_RELIABILITY_V2).category("medical")
							.id(buildInstanceName(MEDIATOR_RELIABILITY_V2));
					builder.done();
					/* retreiving the two nodes mediators reliability V1 and V2 */
					Node[] newNode = ciliaContext.getApplicationRuntime().nodeByType(
							MEDIATOR_RELIABILITY_V2);
					Node[] old = ciliaContext.getApplicationRuntime().nodeByType(
							MEDIATOR_RELIABILITY);
					/* Retreiving adapter instance previously created */
					Node[] adapterNode = ciliaContext.getApplicationRuntime().nodeByType(
							adapterType);
					builder = ciliaContext.getBuilder();
					/* replace on the fly the mediator V1 by V2 */
					logger.info("Replace on the fly mediator instance [{}] by [{}]",
							old[0].nodeId(), newNode[0].nodeId());
					builder.get(chainId[0]).replace().id(old[0].nodeId())
							.to(newNode[0].nodeId());
					builder.done();
					logger.info("Add binding from [{}] to [{}]", adapterNode[0].nodeId(),
							newNode[0].nodeId());
					/* binding from photometer to mediator */
					logger.info("Add binding from [{}] to [{}]", adapterNode[0].nodeId(),
							newNode[0].nodeId());
					builder = ciliaContext.getBuilder();
					builder.get(chainId[0]).bind().from(adapterNode[0].nodeId() + ":in")
							.to(newNode[0].nodeId() + ":in-photometer");
					/* remove unused mediator instance */
					logger.info("Remove mediator instance [{}]", old[0].nodeId(),
							newNode[0].nodeId());
					builder.get(chainId[0]).remove().mediator().id(old[0].nodeId());
					builder.done();
					logger.info("*** End phase 3 *** ");
				} catch (BuilderConfigurationException e) {
					logger.error("Builder configuration Exception {}", e.getMessage());
					e.printStackTrace();
				} catch (BuilderException e) {
					logger.error("Builder Exception {}", e.getMessage());
					e.printStackTrace();
				} catch (BuilderPerformerException e) {
					logger.error("BuilderPerformerException {}", e.getMessage());
					e.printStackTrace();
				}
			} else {
				logger.info("Component type [{}] already instancied [{}]", adapterType,
						node[0].nodeId());
			}
		} catch (CiliaIllegalParameterException e) {
		}
	}

	/* Generate an instance name unsing the type */
	private static final String buildInstanceName(String name) {
		int i = 0;
		return name.toLowerCase() + "-" + (i++);
	}
}
