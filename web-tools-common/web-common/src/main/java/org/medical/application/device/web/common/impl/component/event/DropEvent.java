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
package fr.liglab.adele.icasa.application.device.web.common.impl.component.event;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.extras.app.DragSource;

/**
 * An event that represents a draggable Component being dropped on a valid drop
 * target.
 */
public class DropEvent extends ActionEvent {

   private static final long serialVersionUID = 5554555074771882441L;
   
	private Component target;
	private Component specificTarget;
	private int targetX;
	private int targetY;

	/**
	 * Creates a DropEvent with the given Component as the draggable (source) and
	 * drop target Component (target)
	 * 
	 * @param source
	 *           the draggable Component
	 * @param target
	 *           the configured drop target Component
	 * @param specificTarget
	 *           the most specific component upon which the source was dropped
	 */
	public DropEvent(Object source, Component target, Component specificTarget, int targetX, int targetY) {
		super(source, DragSource.INPUT_DROP);
		this.target = target;
		this.specificTarget = specificTarget;
		this.targetX = targetX;
		this.targetY = targetY;
	}

	/**
	 * Creates a DropEvent with the given Component as the draggable (source) and
	 * drop target Component (target)
	 * 
	 * @param source
	 *           the draggable Component
	 * @param target
	 *           the configured drop target Component
	 * @param specificTarget
	 *           the most specific component upon which the source was dropped
	 */
	public DropEvent(Object source, Component target, Component specificTarget) {
		this(source, target, specificTarget, 0, 0);
	}

	/**
	 * Returns most specific component upon which the source was dropped
	 * 
	 * @return the most specific target component
	 */
	public Component getSpecificTarget() {
		return specificTarget;
	}

	/**
	 * Returns the drop target <code>Component</code>.
	 * 
	 * @return the drop target
	 */
	public Object getTarget() {
		return this.target;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}
}
