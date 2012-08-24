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

import nextapp.echo.app.Label;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

public class DeviceEntry {
	public String serialNumber;
	public Label label;
	public FloatingButton widget;
	public Position position;
	public String logicPosition;
	public String state;
	public String fault;
	public FloatingButtonDragSource dragSource;
	public String description;
}
