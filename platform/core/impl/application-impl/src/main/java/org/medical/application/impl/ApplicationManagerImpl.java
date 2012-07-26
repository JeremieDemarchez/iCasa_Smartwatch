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
package org.medical.application.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.extender.Extender;
import org.medical.application.Application;
import org.medical.application.ApplicationCategory;
import org.medical.application.ApplicationManager;
import org.medical.application.ApplicationState;
import org.medical.application.ApplicationTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;


/**
 * Implementation of an application manager.
 * 
 * @author Thomas Leveque
 *
 */
@Component(name="application-manager-impl")
@Instantiate(name="application-manager-impl-1")
@Provides
@Extender(
		onArrival="onAppBundleArrival",
		onDeparture="onAppBundleDeparture",
		extension=Application.APP_ID_BUNDLE_HEADER)
public class ApplicationManagerImpl implements ApplicationManager {
	
	@Requires(optional=true, nullable=true)
	private LogService _logger;

	private List<ApplicationCategory> _categories = new ArrayList<ApplicationCategory>();
	
	private List<Application> _apps = new ArrayList<Application>();
	
	private List<ApplicationTracker> _listeners = new ArrayList<ApplicationTracker>();
	
	private Map<String /* application id */, ApplicationImpl> _appPerId = new HashMap<String, ApplicationImpl>();
	
	private Map<Application, Set<String> /* bundle symbolic names */> _bundlesPerAppId = new HashMap<Application, Set<String>>();
	
	private Map<String /* bundle symbolic name */, ApplicationImpl> _appPerBundle = new HashMap<String, ApplicationImpl>();
	
	private Map<String /* bundle symbolic name */, Boolean> _isAppPerBundle = new ConcurrentHashMap<String, Boolean>();

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
		synchronized(_listeners) {
			_listeners.add(listener);
			notifyCurrentApplicationSet(listener);
		}
	}

	@Override
	public void removeApplicationListener(ApplicationTracker listener) {
		synchronized(_listeners) {
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
		_isAppPerBundle.clear();
		_bundlesPerAppId.clear();
		_apps.clear();
	}

	private void notifyStop(ApplicationTracker listener) {
		for (Application app : getApplications()) {
			listener.removeApplication(app);
		}
	}

	@Override
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
	
	private void onAppBundleArrival(Bundle bundle, String header) {
		onBundleArrival(bundle);
	}

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
			//ignore if version is not provided
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

	private Set<String> getBundleIds(Application app) {
		Set<String> bundleIds = _bundlesPerAppId.get(app);
		if (bundleIds == null) {
			bundleIds = new HashSet<String>();
			_bundlesPerAppId.put(app, bundleIds);
		}
		
		return bundleIds;
	}

	private void onAppBundleDeparture(Bundle bundle) {
		synchronized (_appPerBundle) {
			final String symbolicName = bundle.getSymbolicName();
			ApplicationImpl app = _appPerBundle.get(symbolicName);
			if (app == null)
				return;
		
        	_appPerBundle.remove(symbolicName);
        }
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
}
