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

import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.icasa.environment.LocatedObject;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.Zone;

public class ZoneImpl implements Zone {


	private int height;
	private int width;
	
	private ZoneImpl parent;
	private Position position;

	private List<Zone> children = new ArrayList<Zone>();

	public ZoneImpl(int x, int y, int width, int height) {
		this(new Position(x, y), width, height);
	}
	
	public ZoneImpl(Position position, int width, int height) {
		this.position = position;
		this.height = height;
		this.width = width;
		this.parent = null;		
	}

	public boolean addZone(ZoneImpl childZone) {
		if (fits(childZone)) {
			children.add(childZone);
			childZone.parent = this;
			return true;
		}
		return false;
	}

	private boolean fits(ZoneImpl childZone) {
		if (childZone.getPosition().x + childZone.width > width)
			return false;
		if (childZone.getPosition().y + childZone.height > height)
			return false;
		return true;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * @return the parent
	 */
	public Zone getParent() {
		return parent;
	}

	/**
    * @return the children
    */
   public List<Zone> getChildren() {
   	return children;
   }
	

	
	public int getLayer() {
		if (parent==null)
			return 0;
		return parent.getLayer() + 1;
	}

	public Position getAbsolutePosition() {
		if (parent==null) 
			return position;
		return new Position(getAbsoluteX(), getAbsoluteY());
	}

	private int getAbsoluteX() {
		if (parent==null)
			return position.x;
		return parent.getAbsoluteX() + position.x;
	}
	
	private int getAbsoluteY() {
		if (parent==null)
			return position.y;
		return parent.getAbsoluteY() + position.y;
	}
	
	@Override
   public Position getPosition() {
	   return position.clone();	   
   }

	@Override
   public int getWidht() {
	   return width;
   }

	@Override
   public boolean contains(LocatedObject object) {
		Position objectPosition = object.getPosition();
      if (objectPosition == null)
      return false;
      
     Position absolutePosition = getAbsolutePosition();
          
     return objectPosition.x >= absolutePosition.x && objectPosition.x <= absolutePosition.x + width
             && objectPosition.y >= absolutePosition.y && position.y <= absolutePosition.y + height;
   }

}
