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

	private String id;
	private int height;
	private int width;
	private Zone parent;
	private Position leftTopPosition;
	private List<Zone> children = new ArrayList<Zone>();
	private List<ZoneListener> listeners = new ArrayList<ZoneListener>();
	private Map<String, Object> variables = new HashMap<String, Object>();
	private boolean useParentVariable = false;

	public ZoneImpl(String id, int x, int y, int width, int height) {
		this(id, new Position(x, y), width, height);
	}

	public ZoneImpl(String id, Position leftTopPosition, int width, int height) {
		this.id = id;
		this.leftTopPosition = leftTopPosition.clone();
		this.height = height;
		this.width = width;
	}

	public String getId() {
		return id;
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
		if (parent == null)
			return 0;
		return parent.getLayer() + 1;
	}

	@Override
	public Position getLeftTopPosition() {
		return leftTopPosition.clone();
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
		Position objectPosition = object.getAbsolutePosition();
		if (objectPosition == null)
			return false;

		Position absolutePosition = getAbsoluteLeftTopPosition();

		return (objectPosition.x >= absolutePosition.x && objectPosition.x <= absolutePosition.x + width)
		      && (objectPosition.y >= absolutePosition.y && leftTopPosition.y <= absolutePosition.y + height);
	}

    @Override
    public boolean contains(Position position) {
        if (position == null)
            return false;

        Position absolutePosition = getAbsoluteLeftTopPosition();

        return (position.x >= absolutePosition.x && position.x <= absolutePosition.x + width)
                && (position.y >= absolutePosition.y && leftTopPosition.y <= absolutePosition.y + height);
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
		int absoluteX = leftTopPosition.x;
		int absoluteY = leftTopPosition.y;
		while (parentZone != null) {
			absoluteX += parentZone.getLeftTopPosition().x;
			absoluteY += parentZone.getLeftTopPosition().y;
			parentZone = parentZone.getParent();
		}
		return new Position(absoluteX, absoluteY);
	}

	@Override
	public void setParent(Zone parent) {
		Zone oldParentZone = this.parent;
		this.parent = parent;

		// Listeners notification
		for (ZoneListener listener : listeners) {
			listener.zoneParentModified(this, oldParentZone);
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
	public Object getVariableValue(String name) {
		if (useParentVariable)
			if (parent != null)
				return parent.getVariableValue(name);
			else
				throw new NullPointerException("Variable " + name + " does not exist");

		Object value = variables.get(name);
		if (value == null)
			throw new NullPointerException("Variable " + name + " does not exist");

		return value;
	}

	@Override
	public void setVariableValue(String name, Object newValue) {
		if (useParentVariable)
			return;

		if (!variables.containsKey(name))
			throw new NullPointerException("Variable " + name + " does not exist");

		Object oldValue = variables.get(name);
		variables.put(name, newValue);

		// Listeners notification
		for (ZoneListener listener : listeners) {
			listener.zoneVariableModified(this, name, oldValue);
		}
	}

	@Override
	public void addVariable(String name) {
		if (useParentVariable)
			return;
		if (variables.containsKey(name))
			return;
		variables.put(name, null);

		// Listeners notification
		for (ZoneListener listener : listeners) {
			listener.zoneVariableAdded(this, name);
		}
	}

	@Override
	public void removeVariable(String name) {
		if (useParentVariable)
			return;
		if (!variables.containsKey(name))
			return;
		variables.remove(name);

		// Listeners notification
		for (ZoneListener listener : listeners) {
			listener.zoneVariableRemoved(this, name);
		}
	}

	@Override
	public Set<String> getVariableNames() {
		if (useParentVariable)
			if (parent != null)
				return parent.getVariableNames();
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
		Position oldPosition = this.leftTopPosition.clone();
		this.leftTopPosition = leftTopPosition.clone();

		// Listeners notification
		for (ZoneListener listener : listeners)
			listener.zoneMoved(this, oldPosition);

	}

	@Override
	public void setWidth(int width) {
		this.width = width;

		// Listeners notification
		for (ZoneListener listener : listeners)
			listener.zoneResized(this);

	}
	
	@Override
   public void resize(int newHeight, int newWidth) {
		this.width = newWidth;
		this.height = newHeight;
		
		// Listeners notification
		for (ZoneListener listener : listeners)
			listener.zoneResized(this);		
   }

	@Override
	public void setHeight(int height) {
		this.height = height;

		// Listeners notification
		for (ZoneListener listener : listeners)
			listener.zoneResized(this);

	}

	@Override
	public String toString() {
		return "X: " + leftTopPosition.x + " Y: " + leftTopPosition.y + " Width: " + width + " Height: " + height;
	}

	@Override
	public Position getRelativePosition(LocatedObject object) {
		if (!(contains(object)))
			return null;
		int relX = object.getAbsolutePosition().x - getAbsoluteLeftTopPosition().x;
		int relY = object.getAbsolutePosition().y - getAbsoluteLeftTopPosition().y;
		return new Position(relX, relY);
	}



}
