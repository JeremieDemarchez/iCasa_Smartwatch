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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.extender.Extender;
import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.widget.DeclarativeDeviceWidgetFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Component(name = "DeviceWidgetFactoryListener")
@Instantiate(name = "DeviceWidgetFactoryListener-0")
@Extender(onArrival = "onBundleArrival", onDeparture = "onBundleDeparture", extension = "Common-Widget-Factory")
public class DeviceWidgetFactoryListenerImpl {

	@Requires(filter = ("(factory.name=DeviceWidgetFactory)"))
	private Factory deviceWidgetFactory;
	
	private Bundle _bundle;
	
	public DeviceWidgetFactoryListenerImpl(BundleContext context) {
		_bundle = context.getBundle();
	}

	@Validate
	private void start() {
	}

	@Invalidate
	private void stop() {
	}

	private void onBundleArrival(Bundle bundle, String header) {
		if (deviceWidgetFactory != null)
			parseFile(bundle);
	}

	private void onBundleDeparture(Bundle bundle) {
		//TODO: free used resources - how?
	}

	private void parseFile(Bundle bundle) {
		URL url = bundle.getResource("device-widgets.xml");
		if (url != null) {
			try {
				InputStream in = url.openStream();

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				saxParser.parse(in, new SimpleContentHandler(bundle, _bundle));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SimpleContentHandler extends DefaultHandler {

		private String windowClassName;
		private String deviceInterfaceName;
		private String deviceWidgetId;
		private String iconFileName;
		private Bundle bundle;
		private Bundle decoratorBundle;

		public SimpleContentHandler(Bundle bundle, Bundle decoratorBundle) {
			this.bundle = bundle;
			this.decoratorBundle = decoratorBundle;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("device-widget")) {

				deviceInterfaceName = attributes.getValue("type");
				deviceWidgetId = attributes.getValue("id");

				iconFileName = null;
				windowClassName = null;
			}
			if (qName.equals("icon")) {
				iconFileName = attributes.getValue("file");
			}
			if (qName.equals("window-class")) {
				windowClassName = attributes.getValue("name");
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("device-widget")) {

				ComponentInstance instance;
				try {
					instance = deviceWidgetFactory.createComponentInstance(new Hashtable());
					InstanceManager manager = (InstanceManager) instance;

					if (manager.getPojoObject() instanceof DeclarativeDeviceWidgetFactory) {
						DeclarativeDeviceWidgetFactory declarativeWidgetFactoryConf = (DeclarativeDeviceWidgetFactory) manager.getPojoObject();
						declarativeWidgetFactoryConf.setBundle(bundle);
						declarativeWidgetFactoryConf.setDeviceInterfaceName(deviceInterfaceName);
						declarativeWidgetFactoryConf.setWindowClassName(windowClassName);
						declarativeWidgetFactoryConf.setIconFileName(iconFileName);
						declarativeWidgetFactoryConf.setDeviceWidgetId(deviceWidgetId);
						declarativeWidgetFactoryConf.setDecoratorBundle(decoratorBundle);
					}
				} catch (UnacceptableConfiguration e) {
					e.printStackTrace();
				} catch (MissingHandlerException e) {
					e.printStackTrace();
				} catch (ConfigurationException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
