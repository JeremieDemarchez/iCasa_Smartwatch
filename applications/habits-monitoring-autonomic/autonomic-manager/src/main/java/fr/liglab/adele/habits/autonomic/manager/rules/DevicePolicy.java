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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.Node;
import fr.liglab.adele.cilia.builder.Builder;
import fr.liglab.adele.cilia.exceptions.BuilderConfigurationException;
import fr.liglab.adele.cilia.exceptions.BuilderException;
import fr.liglab.adele.cilia.exceptions.BuilderPerformerException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalStateException;
import fr.liglab.adele.cilia.exceptions.CiliaInvalidSyntaxException;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.Position;

/**
 * Wait a device If the device has not adapter , , if adapter is
 * 
 * @author Denis Morand
 * 
 */
public class DevicePolicy implements LocatedDeviceListener,BundleListener {
	private static Logger logger = LoggerFactory.getLogger(DevicePolicy.class);
	private CiliaContext ciliaContext;
	private ContextManager iCasaContext;
	private BundleContext bundleContext;
	private String bundleName ;
	private String bundleSymbolicName ;

	private Map<String, String> m_type;
	private Properties config ; 
	private String m_urlProperties ;
	private String m_urlBundle  ;

	public DevicePolicy(BundleContext bc) {
		bundleContext = bc;
		m_type = new HashMap<String, String>();
		config= new Properties();
	}

	public void start() {
		/* Listens for iCasa devices simulated */
		iCasaContext.addListener(this);
		bundleContext.addBundleListener(this);
		if (m_urlProperties != null) {
			try {
				config.load(new URL(m_urlProperties).openStream());
				logger.info("Properties read {}", config.toString());
			} catch (MalformedURLException e) {
				logger.error("Invalid URL");
			} catch (IOException e) {
				logger.error("file {} {}", m_urlProperties, " is not existing");
			}
		}
		logger.info("Service {} started ",this.getClass().getSimpleName());
	}

	public void stop() {
		iCasaContext.removeListener(this);
		bundleContext.removeBundleListener(this);
		logger.info("Service {} stopped ",this.getClass().getSimpleName());
	}

	@Override
	public void deviceAdded(LocatedDevice device) {
		final String adapterType;
		final String deviceType=device.getType() ;
	//		bundleContext.installBundle(new URL(m_urlProperties).openStream());
	//	} catch (BundleException e) {
	//		logger.error("Error while installing bundle") ;
	//		e.printStackTrace();
	//	}
		logger.info("Device [{}] added ", deviceType);
		/* retreive the name (or null) of the Cilia adapter */
		
		adapterType = config.getProperty(deviceType);
		if (adapterType == null) {
			logger.info("Device is not [{}] allowed to be configured ", device.getType());
		} else {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					String instance = isAdapterAlreadyInstancied(adapterType);
					if (instance!= null ) {
						logger.info("The device [{}] could be managed by the adapter instance [{}]", deviceType,instance);
					} else {
						
					}
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
	public void devicePropertyModified(LocatedDevice device,
			String propertyName, Object oldValue) {
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

	private String isAdapterAlreadyInstancied( String typeAdapter) {
		Node[] node;
		try {
			/* retreives all adapter in or in-out */
			node = ciliaContext.getApplicationRuntime().endpointIn("(chain=*)");
			for (int i = 0; i < node.length; i++) {
				try {		
					String nodeType = ciliaContext.getApplicationRuntime()
							.getModel(node[i]).getType();
					logger.info("Version " +ciliaContext.getApplicationRuntime()
							.getModel(node[i]).getVersion());
					if (nodeType.compareToIgnoreCase(typeAdapter) == 0) {
						return ciliaContext.getApplicationRuntime()
								.getModel(node[i]).getType();
					}
				} catch (CiliaIllegalStateException e) {
					/* not an error , dynamic behavior ! */
					logger.info("The node has been removed [{}]",
							e.getMessage());
				}
			}
		} catch (CiliaIllegalParameterException e) {
			logger.error("unrecoverable error [{}]", e.getMessage());
		} catch (CiliaInvalidSyntaxException e) {
			logger.error("unrecoverable error [{}]", e.getMessage());
		}
		return null;
	}

	/* Cilia modification */
	private void createAdapter(String type) {
		Builder builder = ciliaContext.getBuilder();
		String chainId[] = ciliaContext.getApplicationRuntime().getChainId();
		try {
			builder.get(chainId[0]).create().adapter().type(type)
					.id(type + "-0");
			builder.done();
		} catch (BuilderException e) {
			logger.error("Builder error [{}]", e.getMessage());
		} catch (BuilderPerformerException e) {
			logger.error("Builder during adapter creation [{}]", e.getMessage());
		} catch (BuilderConfigurationException e) {
			logger.error("Builder configuration error [{}]", e.getMessage());
		}
	}
	

	@Override
	public void bundleChanged(BundleEvent event) {
		logger.info("Bundle event [{}] arrival ",event.getBundle().getSymbolicName()) ;
		
	}
}
