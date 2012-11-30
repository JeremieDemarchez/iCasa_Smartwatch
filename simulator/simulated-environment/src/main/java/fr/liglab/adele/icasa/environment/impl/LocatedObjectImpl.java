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
package fr.liglab.adele.icasa.environment.impl;

import fr.liglab.adele.icasa.environment.LocatedObject;
import fr.liglab.adele.icasa.environment.Position;

public class LocatedObjectImpl implements LocatedObject {

	private Position m_position;
	
	public LocatedObjectImpl(Position position) {
		m_position = position;
	}
	
	@Override
   public Position getAbsolutePosition() {
	   return m_position;
   }

	@Override
   public void setAbsolutePosition(Position position) {
		m_position = position;
   }

}
