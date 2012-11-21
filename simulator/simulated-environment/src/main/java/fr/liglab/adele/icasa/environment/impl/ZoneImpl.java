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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.icasa.environment.LocatedObject;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.Zone;
import fr.liglab.adele.icasa.environment.ZoneListener;

public class ZoneImpl implements Zone {

	private int height;
	private int width;	
	private Zone parent;
	private Position leftTopposition;
	private List<Zone> children = new ArrayList<Zone>();
	private List<ZoneListener> listeners = new ArrayList<ZoneListener>();
	private Map<String, Double> variables = new HashMap<String, Double>();
	private boolean useParentVariable = false;

	public ZoneImpl(int x, int y, int width, int height) {
		this(new Position(x, y), width, height);
	}
	
	public ZoneImpl(Position leftTopPosition, int width, int height) {
		this.leftTopposition = leftTopPosition.clone();
		this.height = height;
		this.width = width;		
	}

	@Override
	public boolean fits(Zone aZone) {
		if (aZone.getLeftTopPosition().x + aZone.getWidth() > width)
			return false;
		if (aZone.getLeftTopPosition().y + aZone.getHeight() > height)
			return false;
		return true;
	}


	@Override
	public Zone getParent() {
		return parent;
	}

	@Override
   public List<Zone> getChildren() {
   	return children;
   }
	
	@Override
	public int getLayer() {
		if (parent==null)
			return 0;
		return parent.getLayer() + 1;
	}

	@Override
   public Position getLeftTopPosition() {
	   return leftTopposition.clone();	   
   }

	@Override
   public int getWidth() {
	   return width;
   }
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
   public boolean contains(LocatedObject object) {
		Position objectPosition = object.getPosition();
      if (objectPosition == null)
      return false;
      
     Position absolutePosition = getAbsoluteLeftTopPosition();
          
     return objectPosition.x >= absolutePosition.x && objectPosition.x <= absolutePosition.x + width
             && objectPosition.y >= absolutePosition.y && leftTopposition.y <= absolutePosition.y + height;
   }

	@Override
   public boolean addZone(Zone child) {
		if (fits(child)) {
			children.add(child);
			child.setParent(this);
			return true;
		}
		return false;
   }

	@Override
	public Position getAbsoluteLeftTopPosition() {
		Zone parentZone = getParent();
		int absoluteX = leftTopposition.x;
		int absoluteY = leftTopposition.y;
		while (parentZone!=null) {
			absoluteX += parentZone.getLeftTopPosition().x;
			absoluteY += parentZone.getLeftTopPosition().y;
			parentZone = parentZone.getParent();
		}
		return new Position(absoluteX, absoluteY);
	}
	
	@Override
   public void setParent(Zone parent) {
	   this.parent = parent;
	   
	   // Listeners notification 
	   for (ZoneListener listener : listeners) {
	      listener.parentModified(this);
      }
   }

	@Override
   public void addListener(ZoneListener listener) {
		listeners.add(listener);
   }

	@Override
   public void removeListener(ZoneListener listener) {
		listeners.remove(listener);	   
   }

	@Override
   public double getVariableValue(String name) {
		if (useParentVariable)
			if (parent!=null)
				return parent.getVariableValue(name);
			else 
				throw new NullPointerException("Variable " + name + " does not exist");
			
	   Double value = variables.get(name);
	   if (value==null)
	   	throw new NullPointerException("Variable " + name + " does not exist");
	   return value;
   }

	@Override
   public void setVariableValue(String name, double newValue) {
		if (useParentVariable)
			return;
		
	   if (!variables.containsKey(name))
	   	throw new NullPointerException("Variable " + name + " does not exist");
	   
	   double oldValue = variables.get(name); 
	   variables.put(name, newValue);
	   
	   // Listeners notification 
	   for (ZoneListener listener : listeners) {
	      listener.variableModified(this, name, oldValue, newValue);
      }
   }

	@Override
   public void addVariable(String name) {
		if (useParentVariable)
			return;
	   if (variables.containsKey(name))
	   	return;
	   variables.put(name, 0.0);
   }

	@Override
   public Set<String> getVariableList() {
		if (useParentVariable)
			if (parent!=null)
				return parent.getVariableList();
			else
				return null;
	   return variables.keySet();
   }

	@Override
   public void setUseParentVariables(boolean useParentVariables) {
		this.useParentVariable = useParentVariables;
	}

	@Override
   public boolean getUseParentVariables() {
	   return useParentVariable;
   }

	@Override
   public void setLeftTopPosition(Position leftTopPosition) {
		this.leftTopposition = leftTopPosition;
		// Listeners notification 
	   for (ZoneListener listener : listeners) {
	      listener.moved(this);
      }
   }

	@Override
   public void setWidth(int width) {
		this.width = width;
	   // Listeners notification 
	   for (ZoneListener listener : listeners) {
	      listener.resized(this);
      }
   }

	@Override
   public void setHeight(int height) {
		this.height = height;
		// Listeners notification 
	   for (ZoneListener listener : listeners) {
	      listener.resized(this);
      }
   }
	
	@Override
	public String toString() {
	   return "X: " + leftTopposition.x + " Y: " + leftTopposition.y + " Width: " + width + " Height: " + height;
	}
		
}
