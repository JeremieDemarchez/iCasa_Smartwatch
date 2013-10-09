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

/**
 * User: garciai@imag.fr
 * Date: 7/18/13
 * Time: 4:21 PM
 */
public interface AccessRightManagerListener extends AccessRightListener {
    /**
     * Callback called when a new AccessRight has been added to the AccessManager.
     * @param accessRight
     */
    void onAccessRightAdded(AccessRight accessRight);
}
