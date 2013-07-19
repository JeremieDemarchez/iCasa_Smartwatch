/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
import fr.liglab.adele.icasa.application.impl.internal.DeploymentPackageRepresentation;

/**
 * Implementation of an application manager.
 * 
 * @author Thomas Leveque
 * 
 */
@Component(name = "Application-Manager-Impl")
@Instantiate(name = "Application-Manager-Impl-1")
@Provides(specifications = { ApplicationManager.class, EventHandler.class }, properties = { @StaticServiceProperty(name = EventConstants.EVENT_TOPIC, type = "java.lang.String[]", value = "{org/osgi/service/deployment/COMPLETE}") })
public class ApplicationManagerImpl implements ApplicationManager, EventHandler {

	@Requires
	DeploymentAdmin deploymentAdmin;

	@Requires(optional = true, nullable = true)
	private LogService _logger;

	/**
	 * Platform Categories list
	 */
	private List<ApplicationCategory> _categories = new ArrayList<ApplicationCategory>();

	/**
	 * List of application trackers
	 */
	private List<ApplicationTracker> _listeners = new ArrayList<ApplicationTracker>();

	/**
	 * Map keeping the record of applications
	 */
	private Map<String, ApplicationImpl> _appPerId = new HashMap<String, ApplicationImpl>();

	private ApplicationCategoryImpl _undefinedCateg;

	private BundleContext _context;

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	private Lock readLock = lock.readLock();

	private Lock writeLock = lock.writeLock();

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
		readLock.lock();
		Collection<ApplicationImpl> appsCollection = _appPerId.values();
		readLock.unlock();
		return Collections.unmodifiableList(new ArrayList<Application>(appsCollection));
	}

	@Override
	public Application getApplication(String appId) {
		readLock.lock();
		Application app = _appPerId.get(appId);
		readLock.unlock();
		return app;
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

	private void notifyCurrentApplicationSet(ApplicationTracker listener) {
		for (Application app : getApplications()) {
			listener.addApplication(app);
		}
	}

	@Validate
	public void start() {
		
		DeploymentPackage[] packages = deploymentAdmin.listDeploymentPackages();
      // Installing the deployment packages presents in the platform
		for (DeploymentPackage deploymentPackage : packages) {
	      onDeploymePackageArrival(deploymentPackage);
      }
		
		synchronized (_listeners) {
			for (ApplicationTracker listener : _listeners) {
				notifyCurrentApplicationSet(listener);
			}
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
	}

	private void notifyStop(ApplicationTracker listener) {
		for (Application app : getApplications()) {
			listener.removeApplication(app);
		}
	}

	@Override
	public Application getApplicationOfBundle(String bundleSymbolicName) {
		Application resultApp = null;
		readLock.lock();
		for (ApplicationImpl app : _appPerId.values()) {
			if (app.constainsBundle(bundleSymbolicName)) {
				resultApp = app;
				break;
			}
		}
		readLock.unlock();
		return resultApp;
	}

	private Application getApplicationOfDeploymentPackage(String dpSymbolicName) {
		Application resultApp = null;
		readLock.lock();
		for (ApplicationImpl app : _appPerId.values()) {
			if (app.containsDeploymentPackageRepresentation(dpSymbolicName)) {
				resultApp = app;
				break;
			}
		}
		readLock.unlock();
		return resultApp;
	}

	private void onDeploymePackageArrival(DeploymentPackage deploymentPackage) {
		String appId = (String) deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER);
		if (appId == null) // not an application deployment package
			return;

		// String appName = (String) deploymentPackage.getHeader(Application.APP_NAME_BUNDLE_HEADER);
		String appVersion = (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER);
		if (appVersion == null) { // version is mandatory
			// ignore if version is not provided
			_logger.log(LogService.LOG_ERROR, "Deployment Package " + deploymentPackage.getName()
			      + " must specify an application version.");
			return;
		}

		writeLock.lock();

		ApplicationImpl app = _appPerId.get(appId);
		boolean isNewApp = (app == null);

		if (isNewApp) {
			app = new ApplicationImpl(appId, null, _undefinedCateg, this, _context);
			app.setVersion(appVersion);
			app.setState(ApplicationState.STOPED);
		}

		app.addDeploymentPackageRepresentation(DeploymentPackageRepresentation
		      .builFromDeploymentPackage(deploymentPackage));

		_appPerId.put(app.getId(), app);

		writeLock.unlock();

		// Notifies listeners if is a new app
		if (isNewApp) {
			readLock.lock();
			for (ApplicationTracker listener : _listeners) {
				listener.addApplication(app);
			}
			readLock.unlock();
		}
	}

	private void onDeploymePackageDeparture(String dpSymbolicName) {
		ApplicationImpl tempApp = (ApplicationImpl) getApplicationOfDeploymentPackage(dpSymbolicName);

		if (tempApp == null) // The deployment package is not in the application registry
			return;

		writeLock.lock();
		tempApp.removeDeploymentPackageRepresentation(dpSymbolicName);
		if (tempApp.isEmptyApplication()) {
			_appPerId.remove(tempApp.getId());
			for (ApplicationTracker listener : _listeners) {
				listener.removeApplication(tempApp);
			}
		}
		writeLock.unlock();
	}

	/*
	 * private void onDeploymePackageArrival2(DeploymentPackage deploymentPackage) { String appId = (String)
	 * deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER); if (appId == null) // not an application deployment
	 * package return;
	 * 
	 * // String appName = (String) deploymentPackage.getHeader(Application.APP_NAME_BUNDLE_HEADER); String appVersion =
	 * (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER); if (appVersion == null) { // version
	 * is mandatory // ignore if version is not provided _logger.log(LogService.LOG_ERROR, "Deployment Package " +
	 * deploymentPackage.getName() + " must specify an application version."); return; }
	 * 
	 * writeLock.lock();
	 * 
	 * Application app = _appPerId.get(appId); boolean isNewApp = (app == null);
	 * 
	 * if (isNewApp) { ApplicationImpl tempApp = new ApplicationImpl(appId, null, _undefinedCateg, this, _context);
	 * tempApp.setVersion(appVersion); tempApp.setState(ApplicationState.STOPED); _appPerId.put(tempApp.getId(),
	 * tempApp);
	 * 
	 * app = tempApp; }
	 * 
	 * // cannot have multiple versions of the same app final String symbolicName = deploymentPackage.getName();
	 * _appPerDeploymentPackage.put(symbolicName, app);
	 * 
	 * // Adding the bundles contained in the deployment packaged to application Set<String> bundleIds =
	 * _bundlesPerAppId.get(app); if (bundleIds == null) { bundleIds = new HashSet<String>(); _bundlesPerAppId.put(app,
	 * bundleIds); } bundleIds.addAll(getBundlesNamesFromDeploymentPackage(deploymentPackage));
	 * 
	 * writeLock.unlock();
	 * 
	 * // Notifies listeners if is a new app if (isNewApp) { readLock.lock(); for (ApplicationTracker listener :
	 * _listeners) { listener.addApplication(app); } readLock.unlock(); } }
	 * 
	 * private void onDeploymePackageDeparture2(DeploymentPackage deploymentPackage) { synchronized
	 * (_appPerDeploymentPackage) { final String symbolicName = deploymentPackage.getName(); Application app =
	 * _appPerDeploymentPackage.remove(symbolicName);
	 * 
	 * if (app == null) // The deployment package is not in the app registry return;
	 * 
	 * Set<String> bundleIds = _bundlesPerAppId.get(app); if (bundleIds != null)
	 * bundleIds.removeAll(getBundlesNamesFromDeploymentPackage(deploymentPackage));
	 * 
	 * _appPerId.remove(app.getId());
	 * 
	 * // Listeners notification synchronized (_listeners) { for (ApplicationTracker listener : _listeners) {
	 * listener.removeApplication(app); } } } }
	 * 
	 * 
	 * private Set<String> getBundlesNamesFromDeploymentPackage(DeploymentPackage deploymentPackage) { Set<String>
	 * bundles = new HashSet<String>(); if (deploymentPackage != null) { BundleInfo[] bundleInfos =
	 * deploymentPackage.getBundleInfos(); for (BundleInfo bundleInfo : bundleInfos) {
	 * bundles.add(bundleInfo.getSymbolicName()); } } return bundles; }
	 */

	public Set<Bundle> getBundles(String appId) {
		Application app = getApplication(appId);
		if (app == null)
			return Collections.emptySet();

		ApplicationImpl tempApp = (ApplicationImpl) app;
		Set<String> bundleIds = tempApp.getBundlesIds();

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

		// Notification confirming the installation or uninstallation of a Deployment Package
		if (topic.equals("org/osgi/service/deployment/COMPLETE")) {
			boolean sucessfull = (Boolean) event.getProperty("successful");
			if (!sucessfull)
				return;
			String dpSymbolicName = (String) event.getProperty(DeploymentPackage.EVENT_DEPLOYMENTPACKAGE_NAME);
			DeploymentPackage deploymentPackage = deploymentAdmin.getDeploymentPackage(dpSymbolicName);
			if (deploymentPackage != null) { // Installation Confirmation
				String appId = (String) deploymentPackage.getHeader(Application.APP_ID_BUNDLE_HEADER);
				String appVersion = (String) deploymentPackage.getHeader(Application.APP_VERSION_BUNDLE_HEADER);
				if (appId != null && appVersion != null) // iCasa App Deployment Package
					onDeploymePackageArrival(deploymentPackage);
			} else { // unInstallation Confirmation
				onDeploymePackageDeparture(dpSymbolicName);
			}
		}
	}

}
