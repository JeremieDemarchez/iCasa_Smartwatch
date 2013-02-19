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
package fr.liglab.adele.icasa.device.power.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import fr.liglab.adele.icasa.device.power.Powermeter;
import fr.liglab.adele.icasa.device.util.AbstractDevice;

/**
 * Implementation of a simulated Power Switch + Meter
 * 
 * @author Gabriel Pedraza Ferreira
 * 
 */

@Component(name = "iCASA.RealFakePowerMeter")
@Provides
//@Instantiate(name = "Real-Powermeter")
public class TestPowerMeterImpl extends AbstractDevice implements Powermeter {

	@ServiceProperty(name = AbstractDevice.DEVICE_SERIAL_NUMBER, mandatory = true, value = "Titi")
	private String m_serialNumber;

	private double m_currentRating;


	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public double getCurrentPowerRating() {
		return m_currentRating;
	}

}
