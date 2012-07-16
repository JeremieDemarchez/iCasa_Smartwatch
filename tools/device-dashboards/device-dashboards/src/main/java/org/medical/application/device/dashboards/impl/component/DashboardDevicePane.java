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
package org.medical.application.device.dashboards.impl.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nextapp.echo.app.Grid;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Table;
import nextapp.echo.app.table.DefaultTableModel;

import org.apache.felix.ipojo.Factory;
import org.medical.application.Application;
import org.medical.application.device.dashboards.impl.WebDashboardApplicationImpl;
import org.medical.application.device.web.common.impl.component.DeviceEntry;
import org.medical.application.device.web.common.impl.component.DevicePane;
import org.medical.device.manager.ApplicationDevice;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public class DashboardDevicePane extends DevicePane {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 3778184066500074285L;

	public static ResourceImageReference DEVICE_IMAGE = new ResourceImageReference("/Device.png");
	public static ResourceImageReference BIG_DEVICE_IMAGE = new ResourceImageReference("/BigDevice.png");

//	private final DashboardActionPane m_parent;

	private final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();

	protected DeviceTableModel tableModel;

	protected List<String> m_deviceSerialNumbers = new ArrayList<String>();

	private Map<Application, Set<String /* device id */>> m_devicesPerApplication = new HashMap<Application, Set<String /*
																																								 * device
																																								 * id
																																								 */>>();

	private static boolean[] BORDER_POSITIONS = new boolean[20];

	private Table m_deviceTable;

	protected Grid m_grid;

	//private TextField m_description;
	//private DropDownMenu m_factory;
	private final Map<String, Factory> m_deviceFactories = new HashMap<String, Factory>();
	//private final Random m_random = new Random();

	public DashboardDevicePane(DashboardActionPane parent) {
		super(parent);
	}






	public boolean isAvailableFor(String deviceSerialNumber, Application service) {
		if (service == null)
			return true;

		Set<String> deviceIds = m_devicesPerApplication.get(service);
		if (deviceIds == null) {
			return false;
		}

		return (deviceIds.contains(deviceSerialNumber));
	}

	public boolean isAvailableForSelectedApplication(String deviceSerialNumber) {
		Application service = ((WebDashboardApplicationImpl)getAppInstance()).getSelectedApplication();
		if (service == null)
			return true;

		return isAvailableFor(deviceSerialNumber, service);
	}

	private void setDeviceAvailabilityFor(String deviceSerialNumber, Application service, boolean available) {
		if (service == null)
			return;

		synchronized (m_devicesPerApplication) {
			Set<String> deviceIds = m_devicesPerApplication.get(service);
			if (deviceIds == null) {
				deviceIds = new HashSet<String>();
				m_devicesPerApplication.put(service, deviceIds);
			}

			if (available) {
				if (!m_devices.containsKey(deviceSerialNumber))
					return;

				deviceIds.add(deviceSerialNumber);
			} else {
				deviceIds.remove(deviceSerialNumber);
			}
		}
	}

	@Override
	public void notifySelectedAppChanged(Application oldSelectServ, Application newSelectedServ) {
		recreateDeviceTable(newSelectedServ);

		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				tableModel.addDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}

	public void refreshDeviceWidgets() {
		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}


}
