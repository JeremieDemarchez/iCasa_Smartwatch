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
package org.medical.application.device.simulator.gui.impl;

import javax.servlet.Servlet;

import nextapp.echo.app.serial.property.BooleanPeer;
import nextapp.echo.extras.app.serial.property.AccordionPaneLayoutDataPeer;
import nextapp.echo.extras.webcontainer.sync.component.GroupPeer;
import nextapp.echo.webcontainer.sync.command.BrowserOpenWindowCommandPeer;
import nextapp.echo.webcontainer.sync.component.RadioButtonPeer;
import nextapp.echo.webcontainer.sync.property.ServedImageReferencePeer;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Unbind;
import org.medical.application.device.web.common.impl.MedicalHouseSimulatorServletImpl;
import org.medical.application.device.web.common.impl.MedicalWebApplication;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
@Component(name = "SimulatorGUIServlet")
// @Provides(specifications = Servlet.class, properties = {
// @StaticServiceProperty(name = "alias", type = "java.lang.String", value =
// "/dashboards") })
@Provides(specifications = Servlet.class)
public class HouseSimulatorGUIServletImpl extends MedicalHouseSimulatorServletImpl implements Servlet {

	/**
    * 
    */
   private static final long serialVersionUID = 1849521777163057387L;

	@ServiceProperty(name = "alias")
	private String alias;

	Class[] peerClasses = { RadioButtonPeer.class, GroupPeer.class, BooleanPeer.class, ServedImageReferencePeer.class,
	      BrowserOpenWindowCommandPeer.class, AccordionPaneLayoutDataPeer.class };

	/**
	 * The classloader of this bundle.
	 */
	private static final ClassLoader CLASSLOADER = HouseSimulatorGUIServletImpl.class.getClassLoader();

		
	@Invalidate
	public void cleanUp() {
		super.cleanUp();
	}

	
	@Override
   public ClassLoader getBundleClassLoader() {
	   return CLASSLOADER;
   }

	@Override
	@Bind(id="webApplication", optional=false)
	public void bindWebApplication(MedicalWebApplication webApplication) {
	   super.bindWebApplication(webApplication);
	}
	
	@Override
	@Unbind(id="webApplication")
	public void unbindWebApplication(MedicalWebApplication webApplication) {
	   super.unbindWebApplication(webApplication);
	}
	
}
