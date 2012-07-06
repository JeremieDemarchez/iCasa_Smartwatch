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
package org.medical.application.housesimulator.portlet;

import static fr.liglab.adele.icasa.device.GenericDevice.DEVICE_SERIAL_NUMBER;
import nextapp.echo.app.WindowPane;

import org.medical.application.housesimulator.impl.MedicalHouseSimulatorImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fr.liglab.adele.icasa.device.GenericDevice;


/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public abstract class DeviceStatusWindow extends WindowPane implements ServiceTrackerCustomizer {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 5806176061741930702L;

	protected final MedicalHouseSimulatorImpl m_parent;

	protected final String m_deviceSerialNumber;

	private final ServiceTracker m_tracker;

	private boolean m_disposalRequested = false;

	public DeviceStatusWindow(final MedicalHouseSimulatorImpl parent, final String deviceSerialNumber) {
		setId(deviceSerialNumber);
		m_parent = parent;
		m_deviceSerialNumber = deviceSerialNumber;

		Filter f;
		try {
			f = m_parent.getContext().createFilter('(' + DEVICE_SERIAL_NUMBER + '=' + deviceSerialNumber + ')');
		} catch (InvalidSyntaxException e) {
			// Oups!
			throw new RuntimeException(e);
		}
		m_tracker = new ServiceTracker(m_parent.getContext(), f, this);
		m_tracker.open();
	}

	@Override
	public Object addingService(final ServiceReference reference) {
		final Object service = m_parent.getContext().getService(reference);
		m_parent.enqueueTask(new Runnable() {
			@Override
         public void run() {
				updateInfo(reference, (GenericDevice) service);	         
         }			
		});		
		return service;
	}

	@Override
	public void modifiedService(final ServiceReference reference, final Object service) {
		m_parent.enqueueTask(new Runnable() {
			@Override
         public void run() {
				updateInfo(reference, (GenericDevice) service);	         
         }			
		});
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		m_parent.getContext().ungetService(reference);
		// Suicide!
		if (!m_disposalRequested) {
			m_disposalRequested = true;
			m_parent.enqueueTask(new Runnable() {
				@Override
				public void run() {
					m_parent.getStatusPane().remove(DeviceStatusWindow.this);
				}
			});
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (!m_disposalRequested) {
			m_disposalRequested = true;
			m_tracker.close();
		}
	}

	protected abstract void updateInfo(final ServiceReference reference, final GenericDevice device);
	

}
