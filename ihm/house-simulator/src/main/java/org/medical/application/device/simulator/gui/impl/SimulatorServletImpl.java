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
package fr.liglab.adele.icasa.application.device.simulator.gui.impl;

import javax.servlet.Servlet;

import nextapp.echo.app.serial.property.BooleanPeer;
import nextapp.echo.extras.app.serial.property.AccordionPaneLayoutDataPeer;
import nextapp.echo.extras.webcontainer.sync.component.GroupPeer;
import nextapp.echo.webcontainer.sync.command.BrowserOpenWindowCommandPeer;
import nextapp.echo.webcontainer.sync.component.RadioButtonPeer;
import nextapp.echo.webcontainer.sync.property.ServedImageReferencePeer;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import fr.liglab.adele.icasa.application.device.web.common.impl.BaseHouseServletImpl;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza
 */
@Component(name = "SimulatorGUIServlet")
@Provides(specifications = Servlet.class)
public class SimulatorServletImpl extends BaseHouseServletImpl implements Servlet {

	/**
    * 
    */
   private static final long serialVersionUID = 1849521777163057387L;

	@ServiceProperty(name = "alias")
	private String alias;

	
	//TODO: Find other way to force BND of import the corrseponding packages
	Class[] peerClasses = { RadioButtonPeer.class, GroupPeer.class, BooleanPeer.class, ServedImageReferencePeer.class,
	      BrowserOpenWindowCommandPeer.class, AccordionPaneLayoutDataPeer.class };

	/**
	 * The classloader of this bundle.
	 */
	private static final ClassLoader CLASSLOADER = SimulatorServletImpl.class.getClassLoader();

		
	@Invalidate
	public void cleanUp() {
		super.cleanUp();
	}
	
	@Override
   public ClassLoader getBundleClassLoader() {
	   return CLASSLOADER;
   }
	
	@Override
	@Property(name="houseImage", mandatory=true)
	public void setHouseImage(String houseImage) {
	   super.setHouseImage(houseImage);
	}
	
	@Override
	@Property(name="userImage", mandatory=true)	
	public void setUserImage(String userImage) {
	   super.setUserImage(userImage);
	}

	@Override
	@Bind(id="appInstanceFactory", filter="(factory.name=WebHouseSimulator)")
	public void setApplicationInstanceFactory(Factory factory) {
	   super.setApplicationInstanceFactory(factory);
	}
			
}
