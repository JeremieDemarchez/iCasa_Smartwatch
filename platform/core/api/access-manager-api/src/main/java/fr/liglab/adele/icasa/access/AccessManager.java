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

package fr.liglab.adele.icasa.access;

import java.lang.reflect.Method;

/**
 * This interface allows to inspect the rights access of applications to use iCasa devices.
 */
public interface AccessManager {

    /**
     * Get the right access of an application to use a specified device.
     * The {@code returned} object will be synchronized by the Access Manager to
     * maintain updated the using rights.
     * @param applicationIdentifier The identifier of the application.
     * @param deviceId The target device to use if it has the correct rights.
     * @return An {@link AccessRight} object which has the rights information
     * of the usage of the device.
     */
    AccessRight getRightAccess(String applicationIdentifier, String deviceId);

    /**
     * Get the right access of an application.
     * The returned object will be synchronized by the Access Manager to
     * maintain updated the access right.
     * @param applicationIdentifier The identifier of the application.
     * @return An array of {@link AccessRight} objects which has the rights information for the application.
     */
    AccessRight[] getRightAccess(String applicationIdentifier);

    /**
     * Set the right access for an application to use a given device.
     * @param application The application identifier.
     * @param device The device identifier.
     * @param method The method name to set the right access.
     * @param right The right access.
     */
    void updateAccess(String application, String device, String method, boolean right);

    /**
     * Set the right access for an application to use a given device.
     * @param application The application identifier.
     * @param device The device identifier.
     * @param method The method name to set the right access.
     * @param right The right access.
     */
    void updateAccess(String application, String device, Method method, boolean right);

    /**
     * Set the right access for an application to use a device.
     * @param application The application wanting to use the device.
     * @param device The device identifier.
     * @param right The right access.
     */
    void updateAccess(String application, String device, boolean right);

}
