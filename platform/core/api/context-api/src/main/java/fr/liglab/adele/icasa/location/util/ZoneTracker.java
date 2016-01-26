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
package fr.liglab.adele.icasa.location.util;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 */
public class ZoneTracker implements ZoneTrackerCustomizer {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);

    private ZoneTrackerCustomizer trackerCustomizer;

	private ZoneFilter zoneFilter;

	private ZoneFilter zoneCategoryFilter;

	private BundleContext context;
	/**
	 * The tracked zones: String zone identifier-> zone object.
	 */
	//private Tracked tracked;

	/**
	 * Tracker of technical service used to listen zone events.
	 */
	//protected Tracker contextMgrTracker;

	/**
	 * Creates a ZoneTracker object for the specified zone. Zone.
	 * 
	 * @param context
	 * @param customizer
	 * @param mandatoryVariables
	 */
	public ZoneTracker(BundleContext context, ZoneTrackerCustomizer customizer, final String... mandatoryVariables) {
		this.context = context;
		// use custom tracker, or this tracker customizer.
		this.trackerCustomizer = (customizer != null) ? customizer : this;
	/**	this.zoneCategoryFilter = new DefaultTrueFilter();
		// use always matcher filter or filer with mandatory variables.
		this.zoneFilter = (mandatoryVariables.length == 0) ? new DefaultTrueFilter() : new MandatoryVariableFilter(
		      mandatoryVariables);**/
	}

	/**
	 * Opens this ZoneTracker object and begin tracking services.
	 * <p>
	 * Services which match the search criteria specified when this ZoneTracker object was created are now tracked by
	 * this ZoneTracker object.
	 */
	public synchronized void open() {
        logger.debug("[ZoneTracker] open");

		/**	    if (tracked != null) {
			return;
		}

		tracked = new Tracked();
	contextMgrTracker = new Tracker(context, ContextManager.class.getName(), new TrackerCustomizer() {
			@Override
			public boolean addingService(ServiceReference serviceReference) {
				return true;
			}

			@Override
			public void addedService(ServiceReference serviceReference) {
                logger.debug("[ZoneTracker] iCasa Context Manager added");
				ContextManager contextMgr = (ContextManager) context.getService(serviceReference);
				if (tracked == null)
					return;

				synchronized (tracked) {
					tracked.startTracking(contextMgr);
				}
				tracked.trackInitialZones();
			}

			@Override
			public void modifiedService(ServiceReference serviceReference, Object o) {
				// Nothing to do.
			}

			@Override
			public void removedService(ServiceReference serviceReference, Object o) {
                logger.debug("[ZoneTracker] iCasa Context Manager removed");
				if (tracked == null)
					return;

				synchronized (tracked) {
					tracked.stopTracking();
				}
			}
		});
		contextMgrTracker.open();**/
	}

	/**
	 * Closes this ZoneTracker object.
	 * <p>
	 * This method should be called when this ZoneTracker object should end the tracking of services.
	 */
	public synchronized void close() {
        logger.debug("[ZoneTracker] close");
	/**	if (contextMgrTracker != null) {
			contextMgrTracker.close();
			contextMgrTracker = null;
		}**/

	/**	if (tracked == null) {
			return;
		}

		tracked.close();
		tracked = null;**/

	}

	/**
	 * A zone is being added to the Tracker object. This method is called before a zone which matched the search
	 * parameters of the Tracker object is added to it. This method must return true to be tracked for this zone object.
	 * 
	 * @param zone the zone being added to the Tracker object.
	 * @return true if the zone will be tracked, false if not.
	 */
	@Override
	public boolean addingZone(Zone zone) {
		return true;
	}

	/**
	 * A zone tracked by the Tracker object has been added in the list. This method is called when a zone has been added
	 * in the managed list (after addingZone) and if the zone has not disappeared before during the callback.
	 * 
	 * @param zone the added zone
	 */
	@Override
	public void addedZone(Zone zone) {
        // do nothing
	}

	/**
	 * Called when a zone tracked by the Tracker object has been modified. A tracked zone is considered modified
	 * according to tracker configuration.
	 * 
	 * @param zone the changed zone
	 * @param variableName name of the variable that has changed
	 * @param oldValue previous value of the property
	 * @param newValue new value of the property
	 */
	@Override
	public void modifiedZone(Zone zone, String variableName, Object oldValue, Object newValue) {
        // do nothing
	}

	/**
	 * @param zone
	 * @param oldPosition
	 * @param newPosition
	 */
	@Override
	public void movedZone(Zone zone, Position oldPosition, Position newPosition) {
        // do nothing
	}

    @Override
    public void resizedZone(Zone zone) {
        // do nothing
    }

    /**
	 * A zone tracked by the Tracker object has been removed. This method is called after a zone is no longer being
	 * tracked by the Tracker object.
	 * 
	 * @param zone the removed zone.
	 */
	@Override
	public void removedZone(Zone zone) {
        // do nothing
	}

	/**
	 * Gets the list of stored zones.
	 * 
	 * @return the list containing zp,es
	 */
	public List<Zone> getZones() {
	/**	Tracked tracked = this.tracked; // use local var since we are not synchronized
		if (tracked == null) { // if ZoneTracker is not open
			return null;
		}
		synchronized (tracked) {
			int length = tracked.size();
			if (length == 0) {
				return null;
			}
			List<Zone> references = new ArrayList<Zone>(length);
			references.addAll(tracked.values());
			// The resulting array is sorted by ranking.
			return references;
		}**/
		return null;
	}

	/**
	 * Returns the number of zones being tracked by this ZoneTracker object.
	 * 
	 * @return the Number of zones being tracked.
	 */
	public int size() {
	/**	Tracked tracked = this.tracked; // use local var since we are not synchronized
		if (tracked == null) { /* if ZoneTracker is not open */
	/**		return 0;
		}
		return tracked.size();**/
		return 1;
	}

}
