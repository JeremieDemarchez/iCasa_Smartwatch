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
package org.medical.web.common.portlet.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.medical.application.device.web.common.portlet.impl.DeviceWidgetFactoryImpl;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.temperature.Thermometer;

@Component(name = "ThermometerWidgetFactory")
//@Instantiate
@Provides
public class ThermometerWidgetFactory extends DeviceWidgetFactoryImpl {

	public ThermometerWidgetFactory(BundleContext context) {
		setBundle(context.getBundle());
		setIconFileName("/Thermometer.png");
		setDeviceInterfaceName(Thermometer.class.getName());
		setWindowClassName(ThermometerStatusWindow.class.getName());
		setDeviceWidgetId("ThermometerWidgetFactory");
	}

}
