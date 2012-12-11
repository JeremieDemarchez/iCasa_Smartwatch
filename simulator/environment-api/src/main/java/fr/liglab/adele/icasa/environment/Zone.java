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
package fr.liglab.adele.icasa.environment;

import java.util.List;
import java.util.Set;

import fr.liglab.adele.icasa.environment.listener.ZoneListener;

public interface Zone {

	/**
	 * Returns id.
	 * 
	 * @return id.
	 */
	public String getId();

	/**
	 * Gets the (x,y) point in the left-top corner of the zone. This point is
	 * relative to its parent zone
	 * 
	 * @return
	 */
	public Position getLeftTopPosition();

	public void setLeftTopPosition(Position leftTopPosition) throws Exception;

	/**
	 * Gets the absolute (x,y) point in the left-top corner of the zone
	 * 
	 * @return
	 */
	public Position getAbsoluteLeftTopPosition();

	/**
	 * Gets the zone width
	 * 
	 * @return
	 */
	public int getWidth();

	public void setWidth(int width) throws Exception;

	/**
	 * Gets the zone height
	 * 
	 * @return
	 */
	public int getHeight();

	public void setHeight(int height) throws Exception;

	/**
	 * Returns true if a object its geographically contained into the zone.
	 * 
	 * @param object
	 *           a located object
	 * @return true if a object its geographically contained into the zone
	 */
	public boolean contains(LocatedObject object);

    public boolean contains(Position position);

	/**
	 * Returns relative position of specified object from top left corner of this
	 * zone.
	 * 
	 * @param object
	 *           a located object
	 * @return relative position of specified object from top left corner of this
	 *         zone.
	 */
	public Position getRelativePosition(LocatedObject object);

	/**
	 * Adds a child zone to this zone. The child zone must fit this zone.
	 * 
	 * @param child
	 * @return
	 */
	public boolean addZone(Zone child);

	/**
	 * Determines if a Zone fits (don't exceed limits) of this zone/
	 * 
	 * @param aZone
	 * @return
	 */
	public boolean fits(Zone aZone);

	/**
	 * Determines the deepth of this zone
	 * 
	 * @return
	 */
	public int getLayer();

	/**
	 * Gets the list of children zones
	 * 
	 * @return
	 */
	public List<Zone> getChildren();

	/**
	 * Sets the parent Zone
	 * 
	 * @param parent
	 */
	public void setParent(Zone parent);

	/**
	 * Gets the parent Zone
	 * 
	 * @return
	 */
	public Zone getParent();

	public void addListener(ZoneListener listener);

	public void removeListener(ZoneListener listener);

	public void setUseParentVariables(boolean useParentVariables);

	public boolean getUseParentVariables();

	public Object getVariableValue(String name);

	public void setVariableValue(String name, Object newValue);

	public void addVariable(String name);

	public void removeVariable(String name);

	public Set<String> getVariableNames();
	
	public void resize(int newWidth, int newHeight) throws Exception;
	
	public Position getRightBottomPosition();

}
