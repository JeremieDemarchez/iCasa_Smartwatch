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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationCategory;
import fr.liglab.adele.icasa.application.ApplicationState;
import fr.liglab.adele.icasa.application.impl.internal.DeploymentPackageRepresentation;
import fr.liglab.adele.icasa.common.ProgressMonitor;
import fr.liglab.adele.icasa.common.StateVariable;
import fr.liglab.adele.icasa.common.VariableType;
import fr.liglab.adele.icasa.common.impl.EntityImpl;
import fr.liglab.adele.icasa.common.impl.StateVariableImpl;

public class ApplicationImpl extends EntityImpl implements Application, ServiceTrackerCustomizer {

    public static final String VENDOR_PROP_NAME = "Vendor";

    public static final String NAME_PROP_NAME = "Name";

    public static final String VERSION_PROP_NAME = "Version";

    public static final String ACTIVATION_STATE_PROP_NAME = "ActivationState";

    private ApplicationCategory _category;

    private ApplicationManagerImpl _appMgr;

    private ServiceTracker _appTracker;

    private BundleContext _context;

    private Application _app;

    private Map<String, DeploymentPackageRepresentation> deploymentPackageRepresentations;

    public ApplicationImpl(String id, String vendor, ApplicationCategory category, ApplicationManagerImpl appMgr,
            BundleContext context) {
        this(id, vendor, category, ApplicationState.STOPED, appMgr, context);
    }

    public ApplicationImpl(String id, String vendor, ApplicationCategory category, ApplicationState state,
            ApplicationManagerImpl appMgr, BundleContext context) {
        super(id);
        _context = context;
        StateVariable vendorVar = new StateVariableImpl(VENDOR_PROP_NAME, vendor, String.class,
                VariableType.HUMAN_READABLE_DESCRIPTION, "Entity identifier", false, true, this);
        addStateVariable(vendorVar);
        StateVariable nameVar = new StateVariableImpl(NAME_PROP_NAME, id, String.class,
                VariableType.HUMAN_READABLE_DESCRIPTION, "Name", false, true, this);
        addStateVariable(nameVar);
        StateVariable stateVar = new StateVariableImpl(ACTIVATION_STATE_PROP_NAME, state, ApplicationState.class,
                VariableType.STATE, "State", true, true, this);
        addStateVariable(stateVar);
        StateVariable versionVar = new StateVariableImpl(VERSION_PROP_NAME, state, ApplicationState.class,
                VariableType.STATE, "State", true, true, this);
        addStateVariable(versionVar);

        if (category == null)
            throw new IllegalArgumentException("category cannot be null.");

        _category = category;
        _appMgr = appMgr;
        _appTracker = new ServiceTracker(context, Application.class.getName(), this);

        deploymentPackageRepresentations = new HashMap<String, DeploymentPackageRepresentation>();
    }

    @Override
    public String getName() {
        return (String) getVariableValue(NAME_PROP_NAME);
    }

    @Override
    public String getVendor() {
        return (String) getVariableValue(VENDOR_PROP_NAME);
    }

    @Override
    public ApplicationCategory getCategory() {
        return _category;
    }

    @Override
    public synchronized void start(ProgressMonitor monitor) {
        // TODO start bundles
        _appTracker.open();
        if (_app != null) // TODO better manage synchronization and start process
            _app.start(monitor);
    }

    @Override
    public synchronized void stop(ProgressMonitor monitor) {
        if (_app != null)
            _app.stop(monitor);
        _appTracker.close();
        // TODO stop bundles
    }

    @Override
    public synchronized void resume(ProgressMonitor monitor) {
        if (_app != null)
            _app.resume(monitor);
    }

    @Override
    public synchronized void pause(ProgressMonitor monitor) {
        if (_app != null)
            _app.pause(monitor);
    }

    @Override
    public Set<Bundle> getBundles() {
        return _appMgr.getBundles(getId());
    }

    @Override
    public ApplicationState getState() {
        return (ApplicationState) getVariableValue(ACTIVATION_STATE_PROP_NAME);
    }

    public void setState(ApplicationState newState) {
        setVariableValue(ACTIVATION_STATE_PROP_NAME, newState);
        // manage state changed
    }

    @Override
    public String getVersion() {
        return (String) getVariableValue(VERSION_PROP_NAME);
    }

    public void setVersion(String version) {
        setVariableValue(VERSION_PROP_NAME, version);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Application app = (Application) _context.getService(reference);
        String appId = app.getId();
        synchronized (this) {
            if (appId.equals(getId())) {
                _app = app;
                return app;
            } else
                return null;
        }
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        // do nothing
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        Application app = (Application) service;
        String appId = app.getId();
        synchronized (this) {
            if (appId.equals(getId())) {
                _app = null;
                _context.ungetService(reference);
            }
        }
    }

    /**
     * Adds a DP representation on this application
     * 
     * @param dpr
     */
    public void addDeploymentPackageRepresentation(DeploymentPackageRepresentation dpr) {
        deploymentPackageRepresentations.put(dpr.getName(), dpr);
    }

    /**
     * Removes a DP representation on this application
     * 
     * @param name
     */
    public void removeDeploymentPackageRepresentation(String name) {
        deploymentPackageRepresentations.remove(name);
    }

    /**
     * Determines if this application contains a DP
     * 
     * @param name the DP name
     * @return true if app contains DP. False otherwise
     */
    public boolean containsDeploymentPackageRepresentation(String name) {
        return deploymentPackageRepresentations.keySet().contains(name);
    }

    public DeploymentPackageRepresentation getDeploymentPackageRepresentation(String name) {
        return deploymentPackageRepresentations.get(name);
    }

    /**
     * Determines if this application contains a Bundle
     * 
     * @param bundleSymbolicName bundle name
     * @return true if app contains the bundle. False otherwise
     */
    public boolean constainsBundle(String bundleSymbolicName) {
        for (DeploymentPackageRepresentation dpr : deploymentPackageRepresentations.values()) {
            if (dpr.constainsBundle(bundleSymbolicName))
                return true;
        }
        return false;
    }

    /**
     * Determines if this application has at least one Deployment package
     * 
     * @return
     */
    public boolean isEmptyApplication() {
        return (deploymentPackageRepresentations.size() == 0);
    }

    /**
     * Get a string set with bundles symbolic names in this App
     * 
     * @return
     */
    public Set<String> getBundlesIds() {
        Set<String> bundlesIds = new HashSet<String>();
        for (DeploymentPackageRepresentation dpr : deploymentPackageRepresentations.values()) {
            bundlesIds.addAll(dpr.getBundleIds());
        }
        return bundlesIds;
    }

    public List<DeploymentPackageRepresentation> getAllDeploymentPackageRepresentations() {
        List<DeploymentPackageRepresentation> list = new ArrayList<DeploymentPackageRepresentation>(
                deploymentPackageRepresentations.values());
        return list;
    }

}
