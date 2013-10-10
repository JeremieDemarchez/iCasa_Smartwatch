/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.handler.test.mock.devices;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;

public class BinaryLightMockImpl extends AbstractDevice implements GenericDevice, BinaryLight {

    private String m_serialNumber;

    public BinaryLightMockImpl(String serialNumber) {
        m_serialNumber = serialNumber;
    }

    public boolean getPowerStatus() {
        return false;
    }

    public boolean setPowerStatus(boolean state) {
        return false;
    }

    public void turnOn() {
    }

    public void turnOff() {
    }

    public double getMaxPowerLevel() {
        return 0;
    }

    public String getSerialNumber() {
        return m_serialNumber;
    }

}