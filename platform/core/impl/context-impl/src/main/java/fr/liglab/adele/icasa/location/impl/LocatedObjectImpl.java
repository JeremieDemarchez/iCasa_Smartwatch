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
package fr.liglab.adele.icasa.location.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LocatedObjectImpl implements LocatedObject {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG_DEVICE);

	private Position m_position;

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	Lock readLock = lock.readLock();

	Lock writeLock = lock.writeLock();

	private List<LocatedObject> attachedObjects = new ArrayList<LocatedObject>();

	public LocatedObjectImpl(Position position) {
		m_position = position.clone();
	}

	@Override
	public Position getCenterAbsolutePosition() {
		readLock.lock();
		try {
			return m_position.clone();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void setCenterAbsolutePosition(Position position) {
        logger.debug("Setting center absolute position");
		Position absolutePosition = getCenterAbsolutePosition();
		int deltaX = position.x - absolutePosition.x;
		int deltaY = position.y - absolutePosition.y;
        int deltaZ = position.z - absolutePosition.z;
		writeLock.lock();
		m_position = position.clone();
		writeLock.unlock();
		moveAttachedObjects(deltaX, deltaY, deltaZ);
	}

	protected void moveAttachedObjects(int deltaX, int deltaY) {
		moveAttachedObjects(deltaX, deltaY, 0);
	}

    protected void moveAttachedObjects(int deltaX, int deltaY, int deltaZ) {
        logger.debug("Moving attached objects");
        List<LocatedObject> snapshotAttachedObjects = getAttachedObjects();
        for (LocatedObject object : snapshotAttachedObjects) {
            int newX = object.getCenterAbsolutePosition().x + deltaX;
            int newY = object.getCenterAbsolutePosition().y + deltaY;
            int newZ = object.getCenterAbsolutePosition().z + deltaZ;
            Position objectPosition = new Position(newX, newY, newZ);
            object.setCenterAbsolutePosition(objectPosition);
        }
    }
	@Override
	public void attachObject(LocatedObject object) {
		if (object == this)
			return;
		writeLock.lock();
		try {
			attachedObjects.add(object);
		} finally {
			writeLock.unlock();
		}
        logger.debug("Attach object");
		notifyAttachedObject(object);
	}

	@Override
	public void detachObject(LocatedObject object) {
		if (object == this)
			return;
		writeLock.lock();
		try {
			attachedObjects.remove(object);
		} finally {
			writeLock.unlock();
		}
        logger.debug("Detach object");
		notifyDetachedObject(object);
	}

	protected abstract void notifyAttachedObject(LocatedObject attachedObject);

	protected abstract void notifyDetachedObject(LocatedObject attachedObject);

	private List<LocatedObject> getAttachedObjects() {
		List<LocatedObject> snapshotList;
		readLock.lock();
		snapshotList = new ArrayList<LocatedObject>(attachedObjects);
		readLock.unlock();
		return snapshotList;
	}

}
