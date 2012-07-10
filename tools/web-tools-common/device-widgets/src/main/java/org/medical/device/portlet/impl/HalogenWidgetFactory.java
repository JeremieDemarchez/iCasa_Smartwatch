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
package org.medical.device.portlet.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.medical.application.device.dashboards.portlet.impl.DeviceWidgetFactoryImpl;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.light.DimmerLight;

@Component(name = "HalogenWidgetFactory")
//@Instantiate
@Provides
public class HalogenWidgetFactory extends DeviceWidgetFactoryImpl {

	public HalogenWidgetFactory(BundleContext context) {
		setBundle(context.getBundle());
		setIconFileName("/DimmerLamp.png");
		setDeviceInterfaceName(DimmerLight.class.getName());
		setWindowClassName(HalogenStatusWindow.class.getName());
		setDeviceWidgetId("HalogenWidgetFactory");
	}

}

