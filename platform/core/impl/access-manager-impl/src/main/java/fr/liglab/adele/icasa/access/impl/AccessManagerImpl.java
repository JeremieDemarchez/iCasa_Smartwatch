/*
 * Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 * Group Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.icasa.access.impl;

import fr.liglab.adele.icasa.access.*;
import org.apache.felix.ipojo.annotations.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * User: garciai@imag.fr
 * Date: 7/15/13
 * Time: 4:59 PM
 */
@Component(name="iCasaAccessManager")
@Instantiate(name="iCasaAccessManager-1")
@Provides
public class AccessManagerImpl implements AccessManager{

    private Map<String, Map<String, AccessRightImpl>> rightAccess = new HashMap<String, Map<String, AccessRightImpl>>();

    protected List<AccessRightManagerListener> listeners = new ArrayList<AccessRightManagerListener>();

    private Set<AccessRequestImpl> requestSet = new HashSet<AccessRequestImpl>();

    /**
     * Get the right access of an application to use a specified device.
     * The {@code returned} object will be synchronized by the Access Manager to
     * maintain updated the using rights.
     *
     * @param applicationId The identifier of the application.
     * @param deviceId              The target device to use if it has the correct rights.
     * @return An {@link fr.liglab.adele.icasa.access.AccessRight} object which has the rights information
     *         of the usage of the device.
     *
     */
    @Override
    public AccessRightImpl getAccessRight(String applicationId, String deviceId) {
        boolean isNewAccessRight = false;
        AccessRightImpl right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        List<AccessRightManagerListener> accessRightManagerListeners = null;
        synchronized (this){
            if (rightAccess.containsKey(applicationId)){
                applicationAccess = rightAccess.get(applicationId);
                if (applicationAccess.containsKey(deviceId)){
                    right =  applicationAccess.get(deviceId);
                } else {
                    isNewAccessRight = true;
                }
            } else {
                applicationAccess = new HashMap<String, AccessRightImpl>();
                rightAccess.put(applicationId,applicationAccess);
                isNewAccessRight = true;
            }
            if(isNewAccessRight){
                right = createAccessRight(applicationId, deviceId);
                applicationAccess.put(deviceId, right);
                accessRightManagerListeners = getListeners();//get listeners in the sync block
            }
        }
        if(isNewAccessRight){//notify outside the sync block.
            notifyAddAccessRight(accessRightManagerListeners,right);
        }
        return right;
    }

    /**
     * Notify the new accessRight object.
     * @param accessRightManagerListener
     * @param right
     */
    private void notifyAddAccessRight(List<AccessRightManagerListener> accessRightManagerListener,AccessRightImpl right) {
        for(AccessRightManagerListener listener: accessRightManagerListener){
            listener.onAccessRightAdded(right);
        }
    }


    /**
     * Get the right access of an application.
     * The returned object will be synchronized by the Access Manager to
     * maintain updated the access right.
     *
     * @param applicationId The identifier of the application.
     * @return An array of {@link fr.liglab.adele.icasa.access.AccessRight} objects which has the rights information for the application.
     */
    @Override
    public synchronized AccessRightImpl[] getAccessRight(String applicationId) {
        AccessRightImpl[] rights = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationId)){
            applicationAccess = rightAccess.get(applicationId);
            rights = applicationAccess.values().toArray(new AccessRightImpl[0]);
        } else {
            rights = new AccessRightImpl[0];
        }
        return rights;
    }

    /**
     * Get the all access right defined
     * The returned object will be synchronized by the Access Manager to
     * maintain updated the access right.
     *
     * @return An array of {@link fr.liglab.adele.icasa.access.AccessRight} objects which has the rights information for the application.
     */
    @Override
    public synchronized AccessRightImpl[] getAllAccessRight() {
        ArrayList<AccessRightImpl> rights = null;
        Set<String> applications = rightAccess.keySet();
        for(String application: applications){
            AccessRightImpl[] rightAccess = getAccessRight(application);
            for (AccessRightImpl right: rightAccess){
                rights.add(right);
            }
        }
        return rights.toArray(new AccessRightImpl[0]);
    }

    /**
     * Set the right access for an application to use a given device.
     *
     * @param applicationId The application identifier.
     * @param deviceId      The device identifier.
     * @param methodName      The method name to set the right access.
     * @param accessRight       The right access.
     */
    @Override
    public AccessRightImpl setMethodAccess(String applicationId, String deviceId, String methodName, MemberAccessPolicy accessRight) {
        if (methodName == null){
            throw new NullPointerException("Method must not be null");
        }
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        rightAccess.updateMethodAccessRight(methodName, accessRight);
        return rightAccess;
    }

    /**
     * Set the right access for an application to use a given device.
     *
     * @param applicationId The application identifier.
     * @param deviceId      The device identifier.
     * @param method      The method name to set the right access.
     * @param accessRight       The right access.
     */
    @Override
    public AccessRightImpl setMethodAccess(String applicationId, String deviceId, Method method, MemberAccessPolicy accessRight) {
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        return setMethodAccess(applicationId, deviceId, method.getName(), accessRight);
    }

    /**
     * Set the right access for an application to use a device.
     *
     * @param applicationId The application wanting to use the device.
     * @param deviceId      The device identifier.
     * @param right       The right access.
     */
    @Override
    public AccessRightImpl setDeviceAccess(String applicationId, String deviceId, DeviceAccessPolicy right) {
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        rightAccess.updateAccessRight(right);
        return rightAccess;
    }

    private AccessRightImpl createAccessRight(String application, String device){
        AccessRightImpl right = new AccessRightImpl(application, device);
        AccessRequestImpl request = new AccessRequestImpl(right);
        List<AccessRightManagerListener> listenerList = getListeners();
        for(AccessRightListener listener: listenerList){
            right.addListener(listener);
        }
        requestSet.add(request);
        return right;
    }

    /**
     * Remove a listener.
     *
     * @param listener The listener to be called when an access right has changed.
     */
    @Override
    public synchronized void removeListener(AccessRightManagerListener listener) {
        listeners.remove(listener);
        AccessRight[] rights = getAllAccessRight();
    }

    private synchronized List<AccessRightManagerListener> getListeners(){
        return new ArrayList<AccessRightManagerListener>(listeners);
    }
    /**
     * Add a listener to be notified when the access right has been changed.
     *
     * @param listener The listener to be called when an access right has changed.
     */
    @Override
    public synchronized void addListener(AccessRightManagerListener listener) {
        AccessRightImpl[] rights = getAllAccessRight();
        for(AccessRight right: rights){
            right.addListener(listener);
        }
        listeners.add(listener);
    }
}
