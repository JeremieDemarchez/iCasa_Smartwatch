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

import java.util.EventListener;
import java.util.List;
import java.util.Set;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * TODO Comments.
 * 
 * @author bourretp
 */
public interface SimulationManager {


	void createEnvironment(String id, String description, int leftX, int topY, int rightX, int bottomY);
	
    /**
     * Return the identifiers of all the simulated environments.
     * 
     * @return the identifiers of all the simulated environments.
     * @see SimulatedEnvironment#ENVIRONMENT_ID
     */
    Set<String> getEnvironments();

    /**
     * Return the serial numbers of all the present devices.
     * 
     * @return the serial numbers of all the present devices.
     * @see GenericDevice#DEVICE_SERIAL_NUMBER
     */
    Set<String> getDevices();

    /**
     * Return the spatial zone occupied by the environment with the given
     * identifier.
     * 
     * @param environmentId
     *            the identifier of the environment.
     * @return the spatial zone occupied by the environment with the given
     *         identifier.
     */
    Zone getEnvironmentZone(String environmentId);

    /**
     * Return the environment that contains the given position, or {@code null}
     * is the given position is not contained in an environment.
     * 
     * @param position
     *            the position to resolve.
     * @return the environment that contains the given position, or {@code null}
     *         is the given position is not contained in an environment.
     */
    String getEnvironmentFromPosition(Position position);

    /**
     * Return the current position of the device with the given serial number,
     * or {@code null} if the position of the device is unknown, or if the
     * device doesn't exist.
     * 
     * @return the current position of the device with the given serial number,
     *         or {@code null} if the position of the device is unknown, or if
     *         the device doesn't exist.
     */
    Position getDevicePosition(String deviceSerialNumber);

    /**
     * Change the position of the device with the given serial number. If
     * {@code position} is {@code null}, the position information of the device
     * is erased.
     * 
     * @param deviceSerialNumber
     *            the serial number of the device.
     * @param position
     *            the new position of the device, or {@code null} to erase the
     *            position information of the device.
     */
    void setDevicePosition(String deviceSerialNumber, Position position);

    /**
     * Change the location of the device with the given serial number. If
     * {@code environmentId}, is {@code null}, the position information of the
     * device is erased. The device will be randomly placed in the given
     * environment.
     * 
     * @param deviceSerialNumber
     *            the serial number of the device.
     * @param environmentId
     *            the identifier of the environment in which the device is
     *            located, or {@code null} to erase the position information of
     *            the device.
     */
    void setDeviceLocation(String deviceSerialNumber, String environmentId);

    /**
     * Return the current position of the user with the given name, or
     * {@code null} if the user position is unknown.
     * 
     * @param userName
     *            the name of the user to locate.
     * @return the current position of the user with the given name, or
     *         {@code null} if the user position is unknown.
     */
    Position getUserPosition(String userName);

    /**
     * Change the position of the user with the given name. If {@code position}
     * is {@code null}, the user is erased.
     * 
     * @param userName
     *            the name of the user.
     * @param position
     *            the new position of the user, or {@code null} to erase the
     *            user.
     */
    void setUserPosition(String userName, Position position);

    /**
     * Change the location of the user with the given name. If
     * {@code environmentId} is {@code null}, the user is erased. The user will
     * be randomly placed in the given environment.
     * 
     * @param userName
     *            the name of the user.
     * @param environmentId
     *            the identifier of the environment in which the user is
     *            located, or {@code null} to erase the user.
     */
    void setUserLocation(String userName, String environmentId);

    /**
     * "Kill all humans"
     * 
     * <p>
     * Bender Bending Rodriguez<br>
     * Bending Unit 22<br>
     * <small>hecho en Tijuana, Mexico.</small>
     * </p>
     * 
     */
    void removeAllUsers();
    
    void addUser(String userName);
    
    void removeUser(String userName);
    

    Set<String> getEnvironmentVariables(String environmentId);
    
    Double getVariableValue(String environmentId, String variable);
    
    /**
     * Sets an environment variable
     * @param environmentId the environment id
     * @param variable the variable name
     * @param value the new variable value
     */
    void setEnvironmentVariable(String environmentId, String variable, Double value);
       

    /**
     * @param listener
     */
    void addDevicePositionListener(DevicePositionListener listener);

    /**
     * @param listener
     */
    void removeDevicePositionListener(DevicePositionListener listener);

    /**
     * @param listener
     */
    void addUserPositionListener(UserPositionListener listener);

    /**
     * @param listener
     */
    void removeUserPositionListener(UserPositionListener listener);
    
    
    void setDeviceFault(String deviceId, boolean value);
    
    void setDeviceState(String deviceId, boolean value);
    
    //List<String> getDeviceFactories();
    
    void createDevice(String factoryName, String deviceId, String description);
    
    void removeDevice(String deviceId);
    
    Set<String> getDeviceFactories();
    
    
    // --- Device and User events listeners methods and classes --- //
    
    
    /**
     * 
     * @author Gabriel Pedraza Ferreira
     *
     */
    public static interface DevicePositionListener extends EventListener {

        /**
         * @param deviceSerialNumber
         * @param position
         */
        void devicePositionChanged(String deviceSerialNumber, Position position);

    }

    /**
     * 
     * @author Gabriel Pedraza Ferreira
     *
     */
    public static interface UserPositionListener extends EventListener {

        void userPositionChanged(String userName, Position position);
        
        void userAdded(String userName);
        
        void userRemoved(String userName);

    }
    
    public final class Position {
       public final int x;
       public final int y;

       public Position(final int x, final int y) {
           this.x = x;
           this.y = y;
       }

       @Override
       public String toString() {
           return x + ":" + y;
       }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

   }

   public final class Zone {
       public final int leftX;
       public final int rightX;
       public final int topY;
       public final int bottomY;

       public Zone(final int leftX, final int topY, final int rightX,
               final int bottomY) {
           if (leftX >= rightX) {
               throw new IllegalArgumentException("leftX >= rightX");
           } else if (topY >= bottomY) {
               throw new IllegalArgumentException("topY >= bottomY");
           }
           this.leftX = leftX;
           this.topY = topY;
           this.rightX = rightX;
           this.bottomY = bottomY;
       }

       public Zone(final Position topLeftCorner,
               final Position bottomRightCorner) {
           if (topLeftCorner == null) {
               throw new NullPointerException("topLeftCorner");
           } else if (bottomRightCorner == null) {
               throw new NullPointerException("bottomRightCorner");
           } else if (topLeftCorner.x >= bottomRightCorner.x) {
               throw new IllegalArgumentException(
                       "topLeftCorner.x >= bottomRightCorner.x");
           } else if (topLeftCorner.y >= bottomRightCorner.y) {
               throw new IllegalArgumentException(
                       "topLeftCorner.y >= bottomRightCorner.y");
           }
           this.leftX = topLeftCorner.x;
           this.topY = topLeftCorner.y;
           this.rightX = bottomRightCorner.x;
           this.bottomY = bottomRightCorner.y;
       }

       public boolean contains(final Position position) {
           if (position == null) {
               throw new NullPointerException("position");
           }
           return position.x >= leftX && position.x <= rightX
                   && position.y >= topY && position.y <= bottomY;
       }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + bottomY;
			result = prime * result + leftX;
			result = prime * result + rightX;
			result = prime * result + topY;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Zone other = (Zone) obj;
			if (bottomY != other.bottomY)
				return false;
			if (leftX != other.leftX)
				return false;
			if (rightX != other.rightX)
				return false;
			if (topY != other.topY)
				return false;
			return true;
		}
       
       
   }



}
