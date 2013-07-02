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
package fr.liglab.adele.icasa.application.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationCategory;
import fr.liglab.adele.icasa.application.ApplicationManager;
import fr.liglab.adele.icasa.application.ApplicationState;
import fr.liglab.adele.icasa.application.ApplicationTracker;

/**
 * Implementation of an application manager.
 * 
 * @author Thomas Leveque
 * 
 */
@Component(name = "application-manager-impl")
@Instantiate(name = "application-manager-impl-1")
@Provides(specifications={ApplicationManager.class, EventHandler.class} , properties = { @StaticServiceProperty(name = EventConstants.EVENT_TOPIC, type = "java.lang.String[]", value = "{org/osgi/service/deployment/INSTALL, org/osgi/service/deployment/UNINSTALL, org/osgi/service/deployment/COMPLETE}") })
//@Extender(onArrival = "onAppBundleArrival", onDeparture = "onAppBundleDeparture", extension = Application.APP_ID_BUNDLE_HEADER)
public class ApplicationManagerImpl implements ApplicationManager, EventHandler {

	@Requires
	DeploymentAdmin deploymentAdmin;
	
	@Requires(optional = true, nullable = true)
	private LogService _logger;

	private List<ApplicationCategory> _categories = new ArrayList<ApplicationCategory>();

	/**
	 * Platform application list
	 */
	private List<Application> _apps = new ArrayList<Application>();

	/**
	 * List of application trackers
	 */
	private List<ApplicationTracker> _listeners = new ArrayList<ApplicationTracker>();

	
	private Map<String /* application id */, ApplicationImpl> _appPerId = new HashMap<String, ApplicationImpl>();

	private Map<Application, Set<String> /* bundle symbolic names */> _bundlesPerAppId = new HashMap<Application, Set<String>>();

	private Map<String /* Deployment package symbolic name */, ApplicationImpl> _appPerDeploymentPackage = new HashMap<String, ApplicationImpl>();

	private Map<String, DeploymentPackage> _uninstalledDeploymentPackages = new HashMap<String, DeploymentPackage>();

	private ApplicationCategoryImpl _undefinedCateg;

	private BundleContext _context;

	public ApplicationManagerImpl(BundleContext context) {
		_context = context;

		final ApplicationCategoryImpl humanSafeCateg = new ApplicationCategoryImpl("Human Safety");
		_categories.add(humanSafeCateg);
		_categories.add(new ApplicationCategoryImpl("Material Safety"));
		final ApplicationCategoryImpl humanConfortCateg = new ApplicationCategoryImpl("Human Confort");
		_categories.add(humanConfortCateg);
		_categories.add(new ApplicationCategoryImpl("Energy Efficiency"));
		_categories.add(new ApplicationCategoryImpl("Material Durability"));
		_undefinedCateg = new ApplicationCategoryImpl("Undefined");
		_categories.add(_undefinedCateg);
	}

	@Override
	public List<ApplicationCategory> getCategories() {
		return Collections.unmodifiableList(_categories);
	}

	@Override
	public List<Application> getApplications() {
		return Collections.unmodifiableList(_apps);
	}

	@Override
	public Application getApplication(String appId) {
		return _appPerId.get(appId);
	}

	@Override
	public void addApplicationListener(ApplicationTracker listener) {
		synchronized (_listeners) {
			_listeners.add(listener);
			notifyCurrentApplicationSet(listener);
		}
	}

	@Override
	public void removeApplicationListener(ApplicationTracker listener) {
		synchronized (_listeners) {
			_listeners.remove(listener);
			notifyStop(listener);
		}
	}

	@Validate
	public void start() {
		synchronized (_listeners) {
			for (ApplicationTracker listener : _listeners) {
				notifyCurrentApplicationSet(listener);
			}
		}
	}

	private void notifyCurrentApplicationSet(ApplicationTracker listener) {
		for (Application app : getApplications()) {
			listener.addApplication(app);
		}
	}

	@Invalidate
	public void stop() {
		synchronized (_listeners) {
			for (ApplicationTracker listener : _listeners) {
				notifyStop(listener);
			}
		}
		_appPerId.clear();
		//_isAppPerBundle.clear();
		_bundlesPerAppId.clear();
		_apps.clear();
		_appPerDeploymentPackage.clear();
	}

	private void notifyStop(ApplicationTracker listener) {
		for (Application app : getApplications()) {
			listener.removeApplication(app);
		}
	}

	@Override
	public Application getApplicationOfBundle(String bundleSymbolicName) {
		
		synchronized (_bundlesPerAppId) {
	      Set<Application> apps = _bundlesPerAppId.keySet();
	      for (Application application : apps) {
	         Set<String> bundles = _bundlesPerAppId.get(application);
	         if (bundles.contains(bundleSymbolicName))
	         	return application;
         }
	      return null;
      }
	}

	/*
	public Application getApplicationOfBundle(String bundleSymbolicName) {		
		synchronized (_appPerBundle) {
			Boolean isApp = _isAppPerBundle.get(bundleSymbolicName);
			if (isApp == null) {
				Bundle bundle = getBundle(bundleSymbolicName);
				if (bundle != null)
					onBundleArrival(bundle);
			}

			Application app = _appPerBundle.get(bundleSymbolicName);
			if (app != null)
				return app;

			return app;
		}
	}
	*/

	private void onDeploymePackageArrival(DeploymentPackage deploymentPackage) {
		String appId = (String) deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER);
		if (appId==null) // not an application deployment package
			return;

		String appName = (String) deploymentPackage.getHeader(Application.APP_NAME_BUNDLE_HEADER);
		String appVersion = (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER);
		if (appVersion == null) { // version is mandatory
			// ignore if version is not provided
			_logger.log(LogService.LOG_ERROR, "Deployment Package " + deploymentPackage.getName()
			      + " must specify an application version.");
			return;
		}
		boolean isNewApp = addApplication(deploymentPackage, appId, appName, appVersion);
		if (isNewApp) {
			synchronized (_listeners) {
				ApplicationImpl app = _appPerId.get(appId);
				for (ApplicationTracker listener : _listeners) {
					listener.addApplication(app);
				}
			}
		}
		
		tempPrintAppsInfo();
	}

	private boolean addApplication(DeploymentPackage deploymentPackage, String appId, String appName, String version) {
		
		synchronized (_appPerDeploymentPackage) {
			// cannot have multiple versions of the same app
			ApplicationImpl app = _appPerId.get(appId);
			boolean isNewApp = (app == null);

			if (isNewApp) {
				app = new ApplicationImpl(appId, null, _undefinedCateg, this, _context);
				app.setVersion(version);
				app.setState(ApplicationState.STOPED);

				_apps.add(app);
				_appPerId.put(app.getId(), app);
			}
			final String symbolicName = deploymentPackage.getName();
			_appPerDeploymentPackage.put(symbolicName, app);
			
			
			// Adding the bundles contained in the deployment packaged to application
			getBundleIds(app).addAll(getBundlesNamesFromDeploymentPackage(deploymentPackage));
			
			return isNewApp;		
		}
	}

	private void onDeploymePackageDeparture(DeploymentPackage deploymentPackage) {
		synchronized (_appPerDeploymentPackage) {
			final String symbolicName = deploymentPackage.getName();
			ApplicationImpl app = _appPerDeploymentPackage.get(symbolicName);
			if (app == null) // The deployment package is not in the app registry
				return;

			_appPerDeploymentPackage.remove(symbolicName);
			
			// Removing the bundles contained in the deployment packaged from application
			getBundleIds(app).removeAll(getBundlesNamesFromDeploymentPackage(deploymentPackage));
			
			// Listeners notification
			synchronized (_listeners) {				
				for (ApplicationTracker listener : _listeners) {
					listener.removeApplication(app);
				}
			}
		}
		
		tempPrintAppsInfo();
	}
		
	
	private Set<String> getBundlesNamesFromDeploymentPackage(DeploymentPackage deploymentPackage) {
		Set<String> bundles = new HashSet<String>();
		if (deploymentPackage!=null) {
			BundleInfo[] bundleInfos = deploymentPackage.getBundleInfos();
			for (BundleInfo bundleInfo : bundleInfos) {	         
				bundles.add(bundleInfo.getSymbolicName());
         }
		}
		return bundles;
	}
	
	private void onAppBundleArrival(Bundle bundle, String header) {
		// onBundleArrival(bundle);
	}

	/*
	private void onBundleArrival(Bundle bundle) {
		final Dictionary headers = bundle.getHeaders();
		String appId = (String) headers.get(Application.APP_ID_BUNDLE_HEADER);
		boolean isApp = (appId != null);
		_isAppPerBundle.put(bundle.getSymbolicName(), isApp);
		if (!isApp)
			return; // not an application bundle

		String appName = (String) headers.get(Application.APP_NAME_BUNDLE_HEADER);
		String appVersion = (String) headers.get(Application.APP_VERSION_BUNDLE_HEADER);
		if (appVersion == null) { // version is mandatory
			// ignore if version is not provided
			_logger.log(LogService.LOG_ERROR, "Bundle " + bundle.getBundleId() + " must specify an application version.");
			return;
		}

		boolean isNewApp = addApplication(bundle, appId, appName, appVersion);
		if (isNewApp) {
			synchronized (_listeners) {
				ApplicationImpl app = _appPerId.get(appId);
				for (ApplicationTracker listener : _listeners) {
					listener.addApplication(app);
				}
			}
		}
	}
	*/
	
	/*
	private boolean addApplication(Bundle bundle, String appId, String appName, String version) {

		synchronized (_appPerBundle) {
			// cannot have multiple versions of the same app
			ApplicationImpl app = _appPerId.get(appId);
			boolean isNewApp = (app == null);

			if (isNewApp) {
				app = new ApplicationImpl(appId, null, _undefinedCateg, this, _context);
				app.setVersion(version);
				app.setState(ApplicationState.STOPED);

				_apps.add(app);
				_appPerId.put(app.getId(), app);
			}
			final String symbolicName = bundle.getSymbolicName();
			_appPerBundle.put(symbolicName, app);
			Set<String> bundleIds = getBundleIds(app);
			bundleIds.add(symbolicName);

			return isNewApp;
		}
	}
	*/

	private Set<String> getBundleIds(Application app) {
		Set<String> bundleIds = _bundlesPerAppId.get(app);
		if (bundleIds == null) {
			bundleIds = new HashSet<String>();
			_bundlesPerAppId.put(app, bundleIds);
		}
		return bundleIds;
	}

	
	private void onAppBundleDeparture(Bundle bundle) {
		/*
		synchronized (_appPerBundle) {
			final String symbolicName = bundle.getSymbolicName();
			ApplicationImpl app = _appPerBundle.get(symbolicName);
			if (app == null)
				return;

			_appPerBundle.remove(symbolicName);
		}
		*/
	}
	
	
	public Set<Bundle> getBundles(String appId) {
		Application app = getApplication(appId);
		if (app == null)
			return new HashSet<Bundle>();

		Set<String> bundleIds = getBundleIds(app);
		Set<Bundle> bundles = new HashSet<Bundle>();
		for (String bundleId : bundleIds) {
			Bundle bundle = getBundle(bundleId);
			bundles.add(bundle);
		}

		return bundles;
	}

	private Bundle getBundle(String symbolicName) {
		Bundle result = null;
		for (Bundle candidate : _context.getBundles()) {
			if (symbolicName.equals(candidate.getSymbolicName())) {
				if (result == null || result.getVersion().compareTo(candidate.getVersion()) < 0) {
					result = candidate;
				}
			}
		}
		return result;
	}

	@Override
	public boolean isApplicationBundle(Bundle bundle) {
		return getApplicationOfBundle(bundle.getSymbolicName()) != null;
	}

	@Override
   public void handleEvent(Event event) {
		String topic = event.getTopic();
		String dpName = (String) event.getProperty(DeploymentPackage.EVENT_DEPLOYMENTPACKAGE_NAME);
		DeploymentPackage deploymentPackage = deploymentAdmin.getDeploymentPackage(dpName);
		
	   
		if (topic.equals("org/osgi/service/deployment/UNINSTALL")) {
			String appId = (String) deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER);
			String appVersion = (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER);
			if (deploymentPackage!=null && appId!=null && appVersion!=null) {
				synchronized (_uninstalledDeploymentPackages) {
					_uninstalledDeploymentPackages.put(dpName, deploymentPackage);
            }				
			}				
		}
		
		// Notification confirming the installation or uninstallation of a Deployment Package
		if (topic.equals("org/osgi/service/deployment/COMPLETE")) {
			boolean sucessfull = (Boolean) event.getProperty("successful");
			if (!sucessfull)
				return;
			
			if (deploymentPackage!=null) { // Installation Confirmation
				String appId = (String) deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER);
				String appVersion = (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER);
				if (appId!=null && appVersion!=null) // iCasa App Deployment Package
					onDeploymePackageArrival(deploymentPackage);				
			} else { //unInstallation Confirmation
				synchronized (_uninstalledDeploymentPackages) {
					System.out.println("Uninstall -------------------> " + dpName);
					DeploymentPackage uninstalledDeploymentPackage = _uninstalledDeploymentPackages.remove(dpName);
					if (uninstalledDeploymentPackage!=null) {
						onDeploymePackageDeparture(uninstalledDeploymentPackage);
					}
            }
			}			
		}		
   }
	
	
	private void tempPrintAppsInfo() {
		System.out.println(" ========================= Begin =========================");
		Set<String> dps = _appPerDeploymentPackage.keySet();		
		for (String dp : dps) {
			Application app = _appPerDeploymentPackage.get(dp);
	      System.out.println(" ========= DP -> " + dp + " --  Application ->  " + app.getName());
	      Set<String> bundles  = _bundlesPerAppId.get(app);
	      for (String bundle : bundles) {
	      	System.out.println("\t ======= Bundle -> " + bundle);
         }
      }
		System.out.println(" ========================= End =========================");
		System.out.println();
	}
	
}
