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

import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.device.GenericDevice;
import org.apache.felix.ipojo.annotations.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

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

    private Set<AccessRequestImpl> requestSet = new HashSet<AccessRequestImpl>();

    /**
     * Get the right access of an application to use a specified device.
     * The {@code returned} object will be synchronized by the Access Manager to
     * maintain updated the using rights.
     *
     * @param applicationIdentifier The identifier of the application.
     * @param deviceId              The target device to use if it has the correct rights.
     * @return An {@link fr.liglab.adele.icasa.access.AccessRight} object which has the rights information
     *         of the usage of the device.
     *
     */
    @Override
    public AccessRightImpl getRightAccess(String applicationIdentifier, String deviceId) {
        AccessRightImpl right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationIdentifier)){
            applicationAccess = rightAccess.get(applicationIdentifier);
            if (applicationAccess.containsKey(deviceId)){
                right =  applicationAccess.get(deviceId);
            } else {
                right = createAccessRight(applicationIdentifier, deviceId);
                applicationAccess.put(deviceId, right);
            }
        } else {
            applicationAccess = new Hashtable<String, AccessRightImpl>();
            right = createAccessRight(applicationIdentifier, deviceId);
            applicationAccess.put(deviceId, right);
            rightAccess.put(applicationIdentifier,applicationAccess);
        }
        return right;
    }


    /**
     * Get the right access of an application.
     * The returned object will be synchronized by the Access Manager to
     * maintain updated the access right.
     *
     * @param applicationIdentifier The identifier of the application.
     * @return An array of {@link fr.liglab.adele.icasa.access.AccessRight} objects which has the rights information for the application.
     */
    @Override
    public synchronized AccessRight[] getRightAccess(String applicationIdentifier) {
        AccessRightImpl[] right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationIdentifier)){
            applicationAccess = rightAccess.get(applicationIdentifier);
            right = applicationAccess.values().toArray(new AccessRightImpl[0]);
        } else {
            right = new AccessRightImpl[0];
        }
        return right;
    }

    /**
     * Set the right access for an application to use a given device.
     *
     * @param application The application identifier.
     * @param device      The device identifier.
     * @param methodName      The method name to set the right access.
     * @param right       The right access.
     */
    @Override
    public void updateAccess(String application, String device, String methodName, boolean right) {
        if (methodName == null){
            throw new NullPointerException("Method must not be null");
        }
        AccessRightImpl rightAccess = getRightAccess(application, device);
        rightAccess.updateMethodAccessRight(methodName, right);
    }

    /**
     * Set the right access for an application to use a given device.
     *
     * @param application The application identifier.
     * @param device      The device identifier.
     * @param method      The method name to set the right access.
     * @param right       The right access.
     */
    @Override
    public void updateAccess(String application, String device, Method method, boolean right) {
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        updateAccess(application, device, method.getName(), right);
    }

    /**
     * Set the right access for an application to use a device.
     *
     * @param application The application wanting to use the device.
     * @param device      The device identifier.
     * @param right       The right access.
     */
    @Override
    public void updateAccess(String application, String device, boolean right) {
        AccessRightImpl rightAccess = getRightAccess(application, device);
        rightAccess.updateAccessRight(right);
    }

    private AccessRightImpl createAccessRight(String application, String device){
        AccessRightImpl right = new AccessRightImpl(application, device);
        AccessRequestImpl request = new AccessRequestImpl(right);
        requestSet.add(request);
        return right;
    }
}
