/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.context.model.example;


import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;

import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

@ContextEntity(services = Zone.class)
public class ZoneImpl implements Zone {


	@State.Field(service=Zone.class,state=Zone.NAME)
	private String zoneName;
	
	@State.Field(service=Zone.class, state=Zone.X, directAccess = true)
	private  int x;

	@State.Field(service=Zone.class, state=Zone.Y, directAccess = true)
	private int y;

	@State.Field(service=Zone.class, state=Zone.Z, directAccess = true)
	private int z;

	@State.Field(service=Zone.class, state=Zone.X_LENGHT, directAccess = true)
	private int xLength;

	@State.Field(service=Zone.class, state=Zone.Y_LENGHT, directAccess = true)
	private int yLength;

	@State.Field(service=Zone.class, state=Zone.Z_LENGHT, directAccess = true)
	private int zLength;

	@Override
	public String getZoneName() {
		return zoneName;
	}

	@Override
	public void setLeftTopAbsolutePosition(Position position) {
		x = position.x;
		y = position.y;
	}

	@Override
	public Position getLeftTopAbsolutePosition() {
		return new Position(x,y,z);
	}

	@Override
	public int getXLength() {
			return xLength;
	}

	@Override
	public int getYLength() {
			return yLength;
	}

	@Override
	public void setXLength(int width) throws Exception {
		xLength = width;

	}

	@Override
	public void setYLength(int height) throws Exception {
			yLength=height;
	}

	/**
	 * Gets the zone Z length.
	 *
	 * @return the zone Y length.
	 */
	@Override
	public int getZLength() {
		return zLength;
	}

	/**
	 * Sets the zone Y length.
	 *
	 * @param length the new zone Z length.
	 * @throws Exception When the zone does not fit its parent zone.
	 */
	@Override
	public void setZLength(int length) throws Exception {
			zLength = length;
	}

	@Override
	public boolean canContains(Position position) {
		return false;
	}

	@Override
	public void resize(int newWidth, int newHeight, int newDepth) throws Exception {
			xLength = newWidth;
			yLength = newHeight;
			zLength = newDepth;
	}

	@Override
	public Position getRightBottomAbsolutePosition() {
			int newX = x + xLength;
			int newY = y + yLength;
			return new Position(newX, newY, z);
	}

	@Validate
	public void start(){

	}

	@Invalidate
	public void stop(){

	}
}
