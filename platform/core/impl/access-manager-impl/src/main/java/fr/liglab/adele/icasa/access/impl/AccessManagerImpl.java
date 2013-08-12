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

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.access.*;
import fr.liglab.adele.icasa.access.utils.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: garciai@imag.fr Date: 7/15/13 Time: 4:59 PM
 */
@Component(name = "iCasaAccessManager")
@Instantiate(name = "iCasaAccessManager-1")
@Provides
public class AccessManagerImpl implements AccessManager {

    @Property(value = "conf")
    private String location;

    private File storageFile;

    private long timestamp;

    private File backupFile;

    private Boolean disableAccessPolicy = false;

    private AtomicLong nextIdentifier = new AtomicLong(0);

    private Map<String, Map<String, AccessRightImpl>> rightAccess = new HashMap<String, Map<String, AccessRightImpl>>();

    protected List<AccessRightManagerListener> listeners = new ArrayList<AccessRightManagerListener>();

    private Map<Long, AccessRequestImpl> requestSet = new HashMap<Long, AccessRequestImpl>();


    public AccessManagerImpl(BundleContext context) {
        String disableStr = context.getProperty(Constants.DISABLE_ACCESS_POLICY_PROPERTY);

        if (disableStr != null) {
            disableAccessPolicy = Boolean.valueOf(disableStr);
        }
    }

    @Validate
    public void validate() {
        List<Map> rights = loadFile();
        if (rights != null) {
            for (Map rightInMap : rights) {
                AccessRightImpl right = createAccessRight(rightInMap);
                if (right != null) {
                    Map<String, AccessRightImpl> map = getAccessRightContainer(right.getApplicationId());
                    map.put(right.getDeviceId(), right);
                }
            }
        }
    }

    private Map<String, AccessRightImpl> getAccessRightContainer(String applicationId) {
        Map<String, AccessRightImpl> container = null;
        if (rightAccess.containsKey(applicationId)) {
            container = rightAccess.get(applicationId);
        } else {
            container = new HashMap<String, AccessRightImpl>();
            rightAccess.put(applicationId, container);
        }
        return container;
    }

    /**
     * Get the right access of an application to use a specified device. The {@code returned} object will be synchronized
     * by the Access Manager to maintain updated the using rights.
     *
     * @param applicationId The identifier of the application.
     * @param deviceId      The target device to use if it has the correct rights.
     * @return An {@link fr.liglab.adele.icasa.access.AccessRight} object which has the rights information of the usage
     *         of the device.
     */
    @Override
    public AccessRightImpl getAccessRight(String applicationId, String deviceId) {
        boolean isNewAccessRight = false;
        AccessRightImpl right = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        List<AccessRightManagerListener> accessRightManagerListeners = null;
        synchronized (this) {
            applicationAccess = getAccessRightContainer(applicationId);
            if (applicationAccess.containsKey(deviceId)) {
                right = applicationAccess.get(deviceId);
            } else {
                isNewAccessRight = true;
            }
            if (isNewAccessRight) {
                right = createAccessRight(applicationId, deviceId);
                applicationAccess.put(deviceId, right);
                accessRightManagerListeners = getListeners();// get listeners in the sync block
            }
        }
        if (isNewAccessRight) {// notify outside the sync block.
            notifyAddAccessRight(accessRightManagerListeners, right);
        }
        return right;
    }

    /**
     * Notify the new accessRight object.
     *
     * @param accessRightManagerListener
     * @param right
     */
    private void notifyAddAccessRight(List<AccessRightManagerListener> accessRightManagerListener, AccessRightImpl right) {
        for (AccessRightManagerListener listener : accessRightManagerListener) {
            listener.onAccessRightAdded(right);
        }
    }

    /**
     * Get the right access of an application. The returned object will be synchronized by the Access Manager to maintain
     * updated the access right.
     *
     * @param applicationId The identifier of the application.
     * @return An array of {@link fr.liglab.adele.icasa.access.AccessRight} objects which has the rights information for
     *         the application.
     */
    @Override
    public synchronized AccessRightImpl[] getAccessRight(String applicationId) {
        AccessRightImpl[] rights = null;
        Map<String, AccessRightImpl> applicationAccess = null;
        if (rightAccess.containsKey(applicationId)) {
            applicationAccess = rightAccess.get(applicationId);
            rights = applicationAccess.values().toArray(new AccessRightImpl[0]);
        } else {
            rights = new AccessRightImpl[0];
        }
        return rights;
    }

    /**
     * Get an access right. The returned object will be synchronized by the Access Manager to maintain updated the access
     * right.
     *
     * @param policyId The identifier of the application.
     * @return the access right object
     */
    @Override
    public AccessRight getAccessRightFromId(Long policyId) {
        AccessRequestImpl request = requestSet.get(policyId);
        AccessRightImpl right = null;
        if (request != null) {
            right = request.getAccessRight();
        }
        return right;
    }

    /**
     * Get the all access right defined The returned object will be synchronized by the Access Manager to maintain
     * updated the access right.
     *
     * @return An array of {@link fr.liglab.adele.icasa.access.AccessRight} objects which has the rights information for
     *         the application.
     */
    @Override
    public synchronized AccessRightImpl[] getAllAccessRight() {
        ArrayList<AccessRightImpl> rights = new ArrayList();
        Set<String> applications = rightAccess.keySet();
        for (String application : applications) {
            AccessRightImpl[] rightAccess = getAccessRight(application);
            for (AccessRightImpl right : rightAccess) {
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
     * @param methodName    The method name to set the right access.
     * @param accessRight   The right access.
     */
    @Override
    public AccessRightImpl setMethodAccess(String applicationId, String deviceId, String methodName,
                                           MemberAccessPolicy accessRight) {
        if (methodName == null || accessRight == null) {
            throw new NullPointerException("Method and accessRight must not be null");
        }
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        if (!disableAccessPolicy) { //it update only if is enabled.
            rightAccess.updateMethodAccessRight(methodName, accessRight);
        }
        writeFile();
        return rightAccess;
    }

    /**
     * Set the right access for an application to use a given device.
     *
     * @param applicationId The application identifier.
     * @param deviceId      The device identifier.
     * @param method        The method name to set the right access.
     * @param accessRight   The right access.
     */
    @Override
    public AccessRightImpl setMethodAccess(String applicationId, String deviceId, Method method,
                                           MemberAccessPolicy accessRight) {
        if (method == null || accessRight == null) {
            throw new NullPointerException("Method and accessRight must not be null");
        }
        return setMethodAccess(applicationId, deviceId, method.getName(), accessRight);
    }

    /**
     * Set the right access for an application to use a device.
     *
     * @param applicationId The application wanting to use the device.
     * @param deviceId      The device identifier.
     * @param right         The right access.
     */
    @Override
    public AccessRightImpl setDeviceAccess(String applicationId, String deviceId, DeviceAccessPolicy right) {
        if (right == null) {
            throw new NullPointerException("Policy must not be null");
        }
        if (applicationId == null || deviceId == null) {
            throw new NullPointerException("Application and device must not be null");
        }
        AccessRightImpl rightAccess = getAccessRight(applicationId, deviceId);
        if (!disableAccessPolicy) { //update only if is enabled.
            rightAccess.updateAccessRight(right);
        }

        writeFile();
        return rightAccess;
    }

    private AccessRightImpl createAccessRight(String application, String device) {
        Long identifier = getNextIdentifier();
        AccessRightImpl right = null;

        // if access policy is disable, the policy total is used for all rights
        if (disableAccessPolicy) {
            right = new AccessRightImpl(identifier, application, device, DeviceAccessPolicy.TOTAL);
        } else {
            right = new AccessRightImpl(identifier, application, device);
        }

        addListeners(right);
        AccessRequestImpl request = new AccessRequestImpl(right);
        requestSet.put(identifier, request);
        writeFile();
        return right;
    }

    /**
     * Used when loading access right from file.
     *
     * @param rightInfo The right info contained in a map.
     * @return the new access right.
     */
    private AccessRightImpl createAccessRight(Map rightInfo) {
        if (!(rightInfo.containsKey("applicationId") && rightInfo.containsKey("deviceId"))
                && rightInfo.containsKey("policy")) {
            System.err.println("Access right from map is invalid, applicationId, deviceId and policy are mandatory");
            return null;
        }
        DeviceAccessPolicy policy = DeviceAccessPolicy.fromString((String) rightInfo.get("policy"));
        if (policy == null) {
            System.err.println("Access right from map is invalid, policy is not well formed: " + rightInfo.get("policy"));
            return null;
        }
        Long identifier = getNextIdentifier();
        AccessRightImpl right = null;
        try {
            right = new AccessRightImpl(identifier, rightInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        addListeners(right);
        AccessRequestImpl request = new AccessRequestImpl(right);
        requestSet.put(identifier, request);
        return right;
    }

    private void addListeners(AccessRightImpl accessRight) {
        List<AccessRightManagerListener> listenerList = getListeners();
        for (AccessRightListener listener : listenerList) {
            accessRight.addListener(listener);
        }
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

    private synchronized List<AccessRightManagerListener> getListeners() {
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
        for (AccessRight right : rights) {
            right.addListener(listener);
        }
        listeners.add(listener);
    }

    private Long getNextIdentifier() {
        return nextIdentifier.getAndIncrement();
    }

    private Map getAccessRightsMaps() {
        AccessRightImpl[] rights = getAllAccessRight();
        Map accessRight = new HashMap();
        List<Map> rightsInMap = new ArrayList();
        for (AccessRightImpl right : rights) {
            rightsInMap.add(right.toMap());
        }
        accessRight.put("access", rightsInMap);
        return accessRight;
    }

    private synchronized boolean writeFile() {

        // if access policy is disable, policy is not saved to storage
        if (disableAccessPolicy) {
            return true;
        }

        // Rename the current file so it may be used as a backup during the next read
        if (storageFile.exists()) {
            if (!backupFile.exists()) {
                if (!storageFile.renameTo(backupFile)) {
                    System.err.println("Couldn't rename file " + storageFile + " to backup file " + backupFile);
                    return false;
                }
            } else {
                storageFile.delete();
            }
        }

        // Attempt to write the file, delete the backup and return true as
        // atomically as
        // possible. If any exception occurs, delete the new file; next time we
        // will restore
        // from the backup.
        try {
            FileOutputStream str = createFileOutputStream(storageFile);
            if (str == null) {
                return false;
            }
            XMLUtils.writeMapXml(getAccessRightsMaps(), str);
            str.close();
            timestamp = storageFile.lastModified();

            // Writing was successful, delete the backup file if there is one.
            backupFile.delete();
            return true;
        } catch (XmlPullParserException e) {
            System.err.println("writeFileLocked: Got exception:");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("writeFileLocked: Got exception:");
            e.printStackTrace();
        }
        // Clean up an unsuccessfully written file
        if (storageFile.exists()) {
            if (!storageFile.delete()) {
                System.err.println("Couldn't clean up partially-written file " + storageFile);
            }
        }
        return false;
    }

    private FileOutputStream createFileOutputStream(File file) {
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdir()) {
                System.err.println("Couldn't create directory for " + "Access Right file " + file);
                return null;
            }

            try {
                str = new FileOutputStream(file);
            } catch (FileNotFoundException e2) {
                System.err.println("Couldn't create Access Right file " + file);
                e2.printStackTrace();
            }
        }
        return str;
    }

    /**
     * Load the iCasa Access Right file and build a List of Maps. Each map contains the access right information.
     *
     * @return
     */
    private List<Map> loadFile() {

        // if access policy is disable, policy is not charge from storage
        if (disableAccessPolicy) {
            return null;
        }

        storageFile = new File(location, "iCasa_access_right.xml");
        backupFile = new File(location, "iCasa_access_right.bak");
        timestamp = storageFile.lastModified();
        Map accessRightInFile = null;
        List returningList = null;
        if (storageFile.exists() && !storageFile.canRead()) {
            System.err.println("Unable to read Acces Right file: " + storageFile.getAbsolutePath());
            return null;
        }
        if (!(storageFile.exists() && storageFile.canRead())) {
            return null;
        }
        FileInputStream str = null;
        try {
            str = new FileInputStream(storageFile);
            accessRightInFile = XMLUtils.readMapXml(str);
            str.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (accessRightInFile != null && accessRightInFile.containsKey("access")) {
            try {
                returningList = (List) accessRightInFile.get("access");
            } catch (Exception ex) {
                System.err.println("Unable to Retrieve Access Right info: " + storageFile.getAbsolutePath());
                return null;
            }
        }
        return returningList;
    }


}
