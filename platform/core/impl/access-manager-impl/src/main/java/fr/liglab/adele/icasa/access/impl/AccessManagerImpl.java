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
        AccessRightImpl right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationId)){
            applicationAccess = rightAccess.get(applicationId);
            if (applicationAccess.containsKey(deviceId)){
                right =  applicationAccess.get(deviceId);
            } else {
                right = createAccessRight(applicationId, deviceId);
                applicationAccess.put(deviceId, right);
            }
        } else {
            applicationAccess = new Hashtable<String, AccessRightImpl>();
            right = createAccessRight(applicationId, deviceId);
            applicationAccess.put(deviceId, right);
            rightAccess.put(applicationId,applicationAccess);
        }
        return right;
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
    public synchronized AccessRight[] getAccessRight(String applicationId) {
        AccessRightImpl[] right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationId)){
            applicationAccess = rightAccess.get(applicationId);
            right = applicationAccess.values().toArray(new AccessRightImpl[0]);
        } else {
            right = new AccessRightImpl[0];
        }
        return right;
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
    public void setMethodAccess(String applicationId, String deviceId, String methodName, boolean accessRight) {
        if (methodName == null){
            throw new NullPointerException("Method must not be null");
        }
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        rightAccess.updateMethodAccessRight(methodName, accessRight);
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
    public void setMethodAccess(String applicationId, String deviceId, Method method, boolean accessRight) {
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        setMethodAccess(applicationId, deviceId, method.getName(), accessRight);
    }

    /**
     * Set the right access for an application to use a device.
     *
     * @param applicationId The application wanting to use the device.
     * @param deviceId      The device identifier.
     * @param right       The right access.
     */
    @Override
    public void setDeviceAccess(String applicationId, String deviceId, boolean right) {
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        rightAccess.updateAccessRight(right);
    }

    private AccessRightImpl createAccessRight(String application, String device){
        AccessRightImpl right = new AccessRightImpl(application, device);
        AccessRequestImpl request = new AccessRequestImpl(right);
        requestSet.add(request);
        return right;
    }
}
