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

import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightListener;
import fr.liglab.adele.icasa.access.MemberAccessPolicy;

import java.lang.reflect.Method;
import java.util.*;

/**
 * User: garciai@imag.fr
 * Date: 7/16/13
 * Time: 10:12 AM
 */
public class AccessRightImpl implements AccessRight {

    private volatile DeviceAccessPolicy policy = DeviceAccessPolicy.HIDDEN;

    protected List<AccessRightListener> listeners = new ArrayList<AccessRightListener>();

    private final String applicationId;

    private final String deviceId;

    /**
     * A map containing the right access to call methods.
     */
    protected Map<String, MemberAccessPolicy> rightMethodAccess = new HashMap<String, MemberAccessPolicy>();

    public AccessRightImpl(String application, String device){
        applicationId = application;
        deviceId = device;
    }


    /**
     * See if the application has the right to access the device.
     * @return true when the application has the right to access the device. False if not.
     */
    @Override
    public synchronized boolean isVisible(){
        return policy.compareTo(DeviceAccessPolicy.HIDDEN)>0;
    }

    /**
     * See if the application has the right access to call the given method.
     * @param methodName the method to see the right access.
     * @return true when the application can call the method, false if not.
     */
    @Override
    public boolean hasMethodAccess(String methodName) throws NullPointerException{
        if (methodName == null) {
            throw new NullPointerException("Method must not be null");
        }
        MemberAccessPolicy memberAccessPolicy = MemberAccessPolicy.READ_WRITE;
        Boolean exists = false;
        synchronized (this){
            if(policy.compareTo(DeviceAccessPolicy.TOTAL)==0){ //If has total access, return true immediately
                return true;
            } else if(policy.compareTo(DeviceAccessPolicy.VISIBLE)<=0){ //If is visible or hidden, return false.
                return false;
            }else if(rightMethodAccess.containsKey(methodName)){ //If is partial
                memberAccessPolicy = rightMethodAccess.get(methodName); //get the right access.
                exists = true;
            }
        }
        if (!exists){
            updateMethodAccessRight(methodName, MemberAccessPolicy.READ_WRITE); //Added if access right does not exist.
        }
        return memberAccessPolicy.compareTo(MemberAccessPolicy.HIDDEN)>0;
    }

    /**
     * Get the list of method whose access has been defined.
     * If an existent device method, does not appear in the list, the access to the method
     * is denied.
     *
     * @return an array of the existent method access.
     */
    @Override
    public synchronized String[] getMethodList() {
        return rightMethodAccess.keySet().toArray(new String[0]);
    }

    /**
     * See if the application has the right access to call the given method.
     * @param method the method to see the right access.
     * @return true when the application can call the method, false if not.
     */
    @Override
    public boolean hasMethodAccess(Method method) throws NullPointerException{
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        return hasMethodAccess(method.getName());
    }


    /**
     * Get the application wanting to access the device.
     *
     * @return the application identifier.
     */
    @Override
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Get the device identifier the application wants to access.
     *
     * @return the device identifier.
     */
    @Override
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Add a listener to be notified when the access right has been changed.
     *
     * @param listener The listener to be called when an access right has changed.
     */
    @Override
    public synchronized void addListener(AccessRightListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     *
     * @param listener The listener to be called when an access right has changed.
     */
    @Override
    public synchronized void removeListener(AccessRightListener listener) {
        listeners.remove(listener);
    }

    private synchronized List<AccessRightListener> getListeners(){
        return new ArrayList<AccessRightListener>(listeners);
    }
    /**
     *
     * @param right
     */
    protected void updateAccessRight(DeviceAccessPolicy right) {
        List<AccessRightListener> listenerList = null;
        synchronized (this){
            policy = right;
            listenerList = getListeners();
        }
        for(AccessRightListener listener: listenerList){
            listener.onAccessRightModified(this);
        }
    }

    protected void updateMethodAccessRight(String method, MemberAccessPolicy right) throws NullPointerException{
        List<AccessRightListener> listenerList = null;
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        synchronized (this){
            rightMethodAccess.put(method, right);
            listenerList = getListeners();
        }
        for(AccessRightListener listener: listenerList){
            listener.onMethodAccessRightModified(this, method);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessRightImpl that = (AccessRightImpl) o;

        if (!applicationId.equals(that.applicationId)) return false;
        if (!deviceId.equals(that.deviceId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationId.hashCode();
        result = 31 * result + deviceId.hashCode();
        return result;
    }

    public DeviceAccessPolicy getPolicy() {
        return policy;
    }

}
