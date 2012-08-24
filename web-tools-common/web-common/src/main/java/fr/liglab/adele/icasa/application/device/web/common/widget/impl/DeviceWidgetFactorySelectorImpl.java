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
/**
 * 
 */
package fr.liglab.adele.icasa.application.device.web.common.widget.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import fr.liglab.adele.icasa.application.device.web.common.widget.DeviceWidgetFactorySelector;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * @author Gabriel Pedraza
 *
 */
@Component(name = "WidgetFactorySelector")
@Instantiate(name = "WidgetFactorySelector-0")
@Provides
public class DeviceWidgetFactorySelectorImpl implements DeviceWidgetFactorySelector {

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.device.portlet.selector.PortletFactorySelector#selectPortletFactory(fr.liglab.adele.icasa.device.GenericDevice, java.util.List)
	 */
	@Override
	public String selectPortletFactory(GenericDevice device, List<String> widgetFactories) {
		if (widgetFactories.isEmpty())
			return null;
					
		if (widgetFactories.size()>1) {
			if (widgetFactories.contains("X10WidgetFactory"))
				return "X10WidgetFactory";
			if (widgetFactories.contains("TikitagWidgetFactory"))
				return "TikitagWidgetFactory";
			if (widgetFactories.contains("SetTopBoxWidgetFactory"))
				return "SetTopBoxWidgetFactory";
			if (widgetFactories.contains("PlayerWidgetFactory"))
				return "PlayerWidgetFactory";
			if (widgetFactories.contains("DimmerLampWidgetFactory"))
				return "DimmerLampWidgetFactory";
			if (widgetFactories.contains("BinaryLightWidgetFactory"))
				return "BinaryLightWidgetFactory";
			if (widgetFactories.contains("HeaterWidgetFactory"))
				return "HeaterWidgetFactory";
			if (widgetFactories.contains("ThermometerWidgetFactory"))
				return "ThermometerWidgetFactory";
		}			
		
		return widgetFactories.get(0);
	}

}
