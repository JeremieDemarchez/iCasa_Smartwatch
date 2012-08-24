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

import nextapp.echo.app.Component;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;

import fr.liglab.adele.icasa.application.device.web.common.impl.BaseHouseApplication;
import fr.liglab.adele.icasa.application.device.web.common.util.BundleResourceImageReference;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public class HousePane extends ContentPaneWithFloatScroll {

	public static final String HOUSE_PANE_RENDER_ID = "housePaneId";

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = -3216850008204387596L;

	public static final int WIDTH = 736;

	public static final int HEIGHT = 674;

	private Row childContainer;


	/**
	 * TODO comments.
	 * 
	 * @param servlet
	 */
	public HousePane(final BaseHouseApplication application) {
		// m_application = application;
		setRenderId(HOUSE_PANE_RENDER_ID);
		setStyleName("housePane");
		setOverflow(OVERFLOW_AUTO);

		// Set the house image.
		String houseImage = application.getHouseImage();

		childContainer = new Row();
		add(childContainer);
		
		// add house image
		Label imageWidget = new Label(new BundleResourceImageReference(houseImage, BaseHouseApplication.getBundle()));
		childContainer.add(imageWidget);

	}
	
	public Component getChildContainer() {
		return childContainer;
	}

}
