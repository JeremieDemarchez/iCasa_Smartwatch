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
package org.medical.application.device.web.common.impl.component;

import static fr.liglab.adele.icasa.device.GenericDevice.DEVICE_SERIAL_NUMBER;
import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.WindowPane;

import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fr.liglab.adele.icasa.device.GenericDevice;


/**
 * TODO comments.
 * 
 * @author gabriel
 */
public class DeviceStatusPane extends WindowPane implements ServiceTrackerCustomizer {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 5806176061741930702L;

	private final BaseHouseApplication m_parent;

	private final String m_deviceSerialNumber;

	private final ServiceTracker m_tracker;

	//private final Grid m_content;

	private boolean m_disposalRequested = false;

	public DeviceStatusPane(final BaseHouseApplication parent, final String deviceSerialNumber) {
		setId(deviceSerialNumber);
		m_parent = parent;
		m_deviceSerialNumber = deviceSerialNumber;
		//setTitle(m_deviceSerialNumber);
		//m_content = new Grid(2);
		//add(m_content);
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
	public Object addingService(ServiceReference reference) {
		Object service = m_parent.getContext().getService(reference);
		updateInfo(reference, (GenericDevice) service);
		return service;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		updateInfo(reference, (GenericDevice) service);
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
					m_parent.getStatusPane().remove(DeviceStatusPane.this);
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

	private synchronized void updateInfo(final ServiceReference reference, final GenericDevice device) {
		m_parent.enqueueTask(new Runnable() {
			@Override
			public void run() {
				
				for (Component c : getComponents()) {
					remove(c);
				}
				
				String title = (String)reference.getProperty("service.description");
				if (title==null)
					title = "Device: " + m_deviceSerialNumber;

				setTitle(title);
	         Grid layoutGrid = new Grid();
	         layoutGrid.setInsets(new Insets(3, 1));
	         
	         layoutGrid.add(new Label("Serial Number: "));
	         layoutGrid.add(new Label(m_deviceSerialNumber));
	         
//	         if (device instanceof PresenceSensor) {
//	         	PresenceSensor presenceDevice = (PresenceSensor) device;
//	            layoutGrid.add(new Label("Location"));
//	            Label locationLabel = new Label(presenceDevice.getLocation());
//	            
//	            
//	            locationLabel.setForeground(Color.BLUE);
//	            layoutGrid.add(locationLabel);
//	            
//	            boolean presenceSensed = presenceDevice.getSensedPresence();
//	            layoutGrid.add(new Label("Presence Sensed"));
//	            Label presenceLabel;
//	            if (presenceSensed) {
//	            	presenceLabel = new Label("Somebody in the room");
//	            	presenceLabel.setForeground(Color.RED);
//	            } else {
//	            	presenceLabel = new Label("Nobody in the room");
//	            	presenceLabel.setForeground(Color.BLACK);	            	
//	            }
//	            layoutGrid.add(presenceLabel);	            	           
//	         }
	         
	         //if (device instanceof MedicalGenericDevice) {
	         	layoutGrid.add(new Label("Fault"));
	         	
	         	String fault = (String)reference.getProperty("fault");
	         	Label faultLabel;
	         	if (fault.equals("yes")) {
	         		faultLabel = new Label("YES");
	         		faultLabel.setForeground(Color.RED);
	         	} else {
	         		faultLabel = new Label("NO");
	         		faultLabel.setForeground(Color.GREEN);
	         	}
	         	layoutGrid.add(faultLabel);
	         	
	         	
	         	layoutGrid.add(new Label("State"));
	         	
	         	String stateStr = (String)reference.getProperty("state");
	         	Label stateLabel;
	         	if (stateStr.equals("activated")) {
	         		stateLabel = new Label(stateStr);
	         		stateLabel.setForeground(Color.GREEN);
	         	} else {
	         		stateLabel = new Label(stateStr);
	         		stateLabel.setForeground(Color.RED);
	         	}
	         	layoutGrid.add(stateLabel);
	         //}
	         
	         
	         add(layoutGrid);	         
			}
		});
	}


}
