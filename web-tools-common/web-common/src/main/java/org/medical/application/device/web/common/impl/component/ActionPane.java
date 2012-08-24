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
package fr.liglab.adele.icasa.application.device.web.common.impl.component;

import nextapp.echo.app.Extent;
import nextapp.echo.extras.app.AccordionPane;

import fr.liglab.adele.icasa.application.device.web.common.impl.BaseHouseApplication;

/**
 * This Pane contains actions that can be realized by user 
 * 
 * @author bourretp
 */
public abstract class ActionPane extends AccordionPane {

	/**
    * 
    */
	private static final long serialVersionUID = 4290465922034559972L;

	public static Extent ICON_SIZE = new Extent(25);

	private final BaseHouseApplication m_appInstance;

	protected DevicePane m_devicePane;

	

	public ActionPane(final BaseHouseApplication appInstance) {
		m_appInstance = appInstance;
			
	}
	
	protected abstract void initContent();
	
	protected String getDeviceTabName(String serviceName) {
		return "Devices for " + serviceName;
	}

	public BaseHouseApplication getApplicationInstance() {
		return m_appInstance;
	}

	public DevicePane getDevicePane() {
		return m_devicePane;
	}
	
}
