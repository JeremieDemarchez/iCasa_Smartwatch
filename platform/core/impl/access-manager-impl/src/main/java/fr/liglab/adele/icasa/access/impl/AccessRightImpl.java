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

import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightListener;
import fr.liglab.adele.icasa.application.Application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * User: garciai@imag.fr
 * Date: 7/16/13
 * Time: 10:12 AM
 */
public class AccessRightImpl implements AccessRight {



    /**
     * The right access to see the device.
     */
    protected volatile boolean rightToAccessDevice = false;

    protected List<AccessRightListener> listeners = new ArrayList<AccessRightListener>();

    private final String applicationId;

    private final String deviceId;

    /**
     * A map containing the right access to call methods.
     */
    protected Map<String, Boolean> rightMethodAccess = new Hashtable<String, Boolean>();

    public AccessRightImpl(String application, String device){
        applicationId = application;
        deviceId = device;
    }


    /**
     * See if the application has the right to access the device.
     * @return true when the application has the right to access the device. False if not.
     */
    @Override
    public synchronized boolean hasAccess(){
        return rightToAccessDevice;
    }

    /**
     * See if the application has the right access to call the given method.
     * @param methodName the method to see the right access.
     * @return true when the application can call the method, false if not.
     */
    @Override
    public boolean hasAccess(String methodName) throws NullPointerException{
        Boolean access = false;
        Boolean exists = false;
        if (methodName == null) {
            throw new NullPointerException("Method must not be null");
        }
        synchronized (this){
            if(hasAccess() && rightMethodAccess.containsKey(methodName)){
                access = rightMethodAccess.get(methodName); //get the right access.
                exists = true;
            }
        }
        if (!exists){
            updateMethodAccessRight(methodName, false); //Added if access right does not exist.
        }
        return access; // Return false only when there is any access right configured for this method.
    }

    /**
     * See if the application has the right access to call the given method.
     * @param method the method to see the right access.
     * @return true when the application can call the method, false if not.
     */
    @Override
    public boolean hasAccess(Method method) throws NullPointerException{
        if (method == null){
            throw new NullPointerException("Method must not be null");
        }
        return hasAccess(method.getName());
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

    private synchronized List<AccessRightListener> getListeners(){
        return new ArrayList<AccessRightListener>(listeners);
    }
    /**
     *
     * @param right
     */
    protected void updateAccessRight(boolean right) {
        List<AccessRightListener> listenerList = null;
        synchronized (this){
            rightToAccessDevice = right;
            listenerList = getListeners();
        }
        for(AccessRightListener listener: listenerList){
            listener.onAccessRightModified(this);
        }
    }

    protected void updateMethodAccessRight(String method, boolean right) throws NullPointerException{
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



}
