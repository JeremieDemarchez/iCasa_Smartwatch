/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package test.component.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

@Component(name = "ComponentUsingBindMethods")
public class ComponentUsingBindMethods {

    public List<BinaryLight> lights = new ArrayList<BinaryLight>();

    @RequiresDevice(id = "lights", type = "bind", aggregate = true)
    public void bindBinaryLight(BinaryLight light) {
        System.out.println("ComponentUsingBindMethods - Serial Number ---------------> " + light.getSerialNumber());
        synchronized (lights) {
            lights.add(light);
        }
    }

    @RequiresDevice(id = "lights", type = "unbind", aggregate = true)
    public void unbindBinaryLight(BinaryLight light) {
        System.out.println("ComponentUsingBindMethods - Serial Number ---------------> " + light.getSerialNumber());
        synchronized (lights) {
            lights.remove(light);
        }
        
    }

    @Validate
    private void start() {

    }
    
    private void printInfo(BinaryLight light) {
        try {
            System.out.println("ComponentUsingBindMethods - Serial Number ---------------> " + light.getSerialNumber());
            System.out.println("ComponentUsingBindMethods - Power Status ---------------> " + light.getPowerStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
