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
package fr.liglab.adele.icasa.environment;

import fr.liglab.adele.icasa.environment.listener.PersonListener;


/**
 *
 * @author Thomas Leveque
 *         Date: 10/11/12
 */
public interface Person extends LocatedObject {

    public String getName();

    public void setName(String name);

    public String getLocation();

    public void addListener(final PersonListener listener);

    public void removeListener(final PersonListener listener);
    
    
    /*
    public void attachDevice(LocatedDevice device);
    
    public void detachDevice(LocatedDevice device);    
    */

}
