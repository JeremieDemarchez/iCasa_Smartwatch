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
package org.medical.application.device.web.common.widget.impl;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.util.DecoratedBundleResourceImageReference;
import org.medical.application.device.web.common.widget.DeclarativeDeviceWidgetFactory;
import org.osgi.framework.Bundle;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

@Component(name = "DeviceWidgetFactory")
@Provides
public class DeviceWidgetFactoryImpl implements DeclarativeDeviceWidgetFactory {

	private Bundle bundle;
	private String windowClassName;
	private String deviceInterfaceName;
	private String iconFileName;
	private String deviceWidgetId;
	private Bundle _decoratorBundle;

	public DeviceWidgetFactoryImpl() {
		_decoratorBundle = BaseHouseApplication.getBundle();
	}

	@Override
	public boolean typeIsSupported(GenericDevice device) {
		if (bundle != null && deviceInterfaceName != null) {
			Set<String> interfaces = null;
			interfaces = getInterfaceNames(device.getClass().getInterfaces());
			return interfaces.contains(deviceInterfaceName);
		}
		return false;
	}

	/**
	 * Return the list of interfaces names
	 * @param interfaces
	 * @return
	 */
	private Set<String> getInterfaceNames(Class[] interfaces) {
		HashSet<String> names = new HashSet<String>();
		for (Class interfaze : interfaces) {
			names.add(interfaze.getCanonicalName());
		}
		return names;
	}

	@Override
	public WindowPane createWidget(BaseHouseApplication parent, String deviceId) {
		if (windowClassName == null) {
			WindowPane windowPane = new GenericDeviceStatusWindow(parent, deviceId);
			return windowPane;
		}

		if (bundle != null) {
			try {
				Class windowClass = bundle.loadClass(windowClassName);
				Constructor constructor = windowClass.getConstructor(BaseHouseApplication.class, String.class);
				Object arglist[] = { parent, deviceId };
				WindowPane windowPane = (WindowPane) constructor.newInstance(arglist);
				return windowPane;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public ResourceImageReference getDeviceIcon(GenericDevice device) {
		if (bundle == null)
			return null;

		String stateDecoratorImg = null;
		String eventDecoratorImg = null;

		if (device.getState().equals("deactivated")) {
			stateDecoratorImg = "stop.png";
		} else if (device.getState().equals("activated")) {
			if (device.getFault().equals("yes"))
				stateDecoratorImg = "warning.png";
			else
				stateDecoratorImg = "play.png";

			if (device instanceof PresenceSensor) {
				if (((PresenceSensor) device).getSensedPresence())
					eventDecoratorImg = "event.png";
			}
		}

		return new DecoratedBundleResourceImageReference(iconFileName, stateDecoratorImg, eventDecoratorImg, bundle,
		      _decoratorBundle);
	}

	@Override
	public String getID() {
		return deviceWidgetId;
	}

	@Override
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public void setWindowClassName(String windowClassName) {
		this.windowClassName = windowClassName;
	}

	@Override
	public void setDeviceInterfaceName(String deviceInterfaceName) {
		this.deviceInterfaceName = deviceInterfaceName;
	}

	@Override
	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	@Override
	public void setDeviceWidgetId(String deviceWidgetId) {
		this.deviceWidgetId = deviceWidgetId;
	}

	@Override
	public void setDecoratorBundle(Bundle bundle) {
		_decoratorBundle = bundle;
	}

}
