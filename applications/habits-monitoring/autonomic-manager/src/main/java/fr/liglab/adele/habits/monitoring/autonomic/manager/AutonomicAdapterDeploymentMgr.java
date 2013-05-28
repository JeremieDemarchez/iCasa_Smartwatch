package fr.liglab.adele.habits.monitoring.autonomic.manager;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.habits.monitoring.autonomic.manager.dbadapter.IDBAdapter;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

/**
 * Created with IntelliJ IDEA. User: Kettani Mehdi Date: 26/04/13 Time: 11:33 To
 * change this template use File | Settings | File Templates.
 */
@Component(name = "AutonomicAdapterDeploymentMgr")
@Instantiate(name = "AutonomicAdapterDeploymentMgr-1")
public class AutonomicAdapterDeploymentMgr implements DeviceListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AutonomicAdapterDeploymentMgr.class);

	private Map<String, GenericDevice> devices;

	// DP installer
	@Requires
	private DeploymentAdmin dadmin;

	@Requires
	private PackageAdmin padmin;

	// DB Adapter Service
	@Requires
	private IDBAdapter dbAdapter;

	// bundle context
	private BundleContext context;

	/**
	 * Private constructor to get bundleContext.
	 * 
	 * @param context
	 */
	private AutonomicAdapterDeploymentMgr(BundleContext context) {
		this.context = context;
	}

	public void handleNewDevice(String[] classNames)
			throws ClassNotFoundException {

		for (String className : classNames) {
			if (!className.equals(GenericDevice.class.getName())) {
				// get dp id related to this interface
				Set<String> dpInfos = new HashSet<String>();
				try {
					Class<?> cl = padmin
							.getExportedPackage(
									className.substring(0,
											className.lastIndexOf(".")))
							.getExportingBundle().loadClass(className);

					if (cl != null && GenericDevice.class.isAssignableFrom(cl)) {
						logger.info(className + " extends GenericDevice");
						dpInfos.add(className);
						logger.info("getting dp id for class : " + className);
						String dpId = dbAdapter.getDeviceAdapterId(dpInfos);
						// check if this dp is already installed
						if (dpId != null) {
							logger.info("related dp id is : " + dpId);
							DeploymentPackage dp = dadmin
									.getDeploymentPackage(dpId);
							if (dp != null) {
								// dp is already installed : do nothing
								logger.info("dp " + dpId
										+ " is already installed.");
							} else {
								// dp is not installed, we should install it
								logger.info(dpId + " is not installed");
								// get url related to this id
								String dpUrl = (String) dbAdapter
										.getDeviceAdapterUrl(dpId);
								logger.info("location of this dp : " + dpUrl);
								URL url = new URL(dpUrl);
//								Proxy proxy = new Proxy(
//										Proxy.Type.HTTP,
//										new InetSocketAddress("p-goodway", 3128));
//								HttpURLConnection connection = (HttpURLConnection) new URL(
//										dpUrl).openConnection(proxy);
								dadmin.installDeploymentPackage(url.openStream());
							}
						} else {
							logger.warn("Could not find any dp to handle "
									+ cl.getName());
						}
					}

				} catch (Exception e) {
					logger.error("", e);
				}

			}
		}
	}

	@Override
	public void deviceAdded(GenericDevice genericDevice) {

		if (devices == null) {
			devices = new HashMap<String, GenericDevice>();
		}
		devices.put(genericDevice.getSerialNumber(), genericDevice);
		logger.info("device added");

		if (genericDevice instanceof PresenceSensor) {

		}
	}

	@Override
	public void deviceRemoved(GenericDevice genericDevice) {

		logger.info("device removed");

	}

	@Override
	public void devicePropertyModified(GenericDevice genericDevice, String s,
			Object oldValue, Object newValue) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void devicePropertyAdded(GenericDevice genericDevice, String s) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void devicePropertyRemoved(GenericDevice genericDevice, String s) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	/**
	 * It is called when a new service of type GenericDevice is registered into
	 * the gateway. (callback method, see metadata.xml).
	 * 
	 * @param detector
	 *            A new GenericDevice (proxy)
	 * @throws ClassNotFoundException
	 */
	@Bind(id = "GenericDeviceDep", specification = "fr.liglab.adele.icasa.device.GenericDevice", aggregate = true)
	public void bindDevice(ServiceReference detectorRef)
			throws ClassNotFoundException {

		String[] classNames = (String[]) detectorRef
				.getProperty(Constants.OBJECTCLASS);

		// if (classNames != null && classNames.length > 0) {
		// for (String className : classNames) {
		// System.out.println(className);
		// }
		// }

		GenericDevice detectorService = (GenericDevice) context
				.getService(detectorRef);
		logger.info("A new device has been found, id "
				+ detectorService.getSerialNumber());
		detectorService.addListener(this);
		this.handleNewDevice(classNames);
		// this.deviceAdded(detectorService);
	}

	@Unbind(id = "GenericDeviceDep")
	public void unbindDevice(ServiceReference detectorRef) {
		GenericDevice detectorService = (GenericDevice) context
				.getService(detectorRef);
		logger.info("A device is now outside from the zone, id "
				+ detectorService.getSerialNumber());
		detectorService.removeListener(this);
		this.deviceRemoved(detectorService);
	}
}
