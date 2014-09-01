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
package fr.liglab.adele.icasa.electricity.manager;


import org.apache.felix.ipojo.annotations.*;


/**
 * Created by horakm on 4/3/14.
 */
@Component(name = "consumption-manager")
@Instantiate(name = "consumption-manager-1")
@Provides
public class ElectricityManagerImpl implements ElectricityManager {



    public ElectricityManagerImpl() {

    }

    /** Component Lifecycle Method */
    @Validate
    private void start() {
        System.out.println("Component consumption manager is starting...");
    }

    /** Component Lifecycle Method */
    @Invalidate
    private void stop() {

        System.out.println("Component consumption manager is stopping...");
    }



    @Override
    public int filterSample() {
        return 14;
    }
}
