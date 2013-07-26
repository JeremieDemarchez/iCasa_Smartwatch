/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.service.zone.dimension.calculator.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.liglab.adele.icasa.service.zone.dimension.calculator.ZoneDimension;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.TechnicalService;
import fr.liglab.adele.icasa.Variable;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;


/**
 * The Class ZoneDimensionImpl implements the Technical Service responsible for
 * providing each zone dimension.
 * 
 * @author : yoann.maurel@imag.fr
 */
@Component
@Instantiate
@Provides(specifications = TechnicalService.class)
class ZoneDimensionImpl implements TechnicalService, ZoneDimension {

	private final static Logger L = Logger.getLogger(ZoneDimension.class.getName());

	/**
	 * The array of zone variables computed by this technical service.
	 */
	private static final Variable[] COMPUTED_ZONE_VARIABLES = new Variable[] { ZONE_AREA_VAR,
	        ZONE_VOLUME_VAR };

	/**
	 * The Constant SCALE_FACTOR establishes the relation between px and meters.
	 */
	public static final double SCALE_FACTOR = 0.014d; // 1px -> 0.014m

	@Requires
	/**
	 * The context manager is provided by the simulator and allows to modify the different zones properties.
	 */
	private ContextManager m_contextManager;

	/**
	 * The zone listener is responsible for adding dimension properties.
	 * 
	 * An anonymous class is used to clearly separate the listener from the
	 * implementation of TechnicalService methods.
	 */
	private final ZoneListener m_zoneListener = new ZoneListener() {

		/**
		 * Update zone dimension properties (area, volume,...)
		 * 
		 * @param zone
		 *            : the zone to update.
		 */
		private void updateZoneDimensionProperties(Zone zone) {

			assert (zone != null);

			// Gets the dimensions (convert from pixels to meters).
			// FIXME : z should be provided by a technical service.
			final double x = pixelsToMeters(zone.getXLength());
			final double y = pixelsToMeters(zone.getYLength());
			final double z = pixelsToMeters(zone.getZLength());

			assert ((x > 0.0d) && (y > 0.0d) && (z > 0.0d)) : "negative dimensions !";


			// computes area and volume of the zone. We assume that the zone is
			// a rectangle (not a trapezoid).
			final double area = x * y;
			final double volume = area * z;


			if (L.isLoggable(Level.INFO)) {
				L.info(String.format("Update the zone %s area = %f m2 ; volume = %f m3",
				        zone.getId(), area, volume));
			}

			// add the zone variables (if first call - next calls have no effect
			// according to spec).
			m_contextManager.addZoneVariable(zone.getId(), ZONE_AREA);
			m_contextManager.addZoneVariable(zone.getId(), ZONE_VOLUME);

			// set the different properties (area, volume, ...).
			m_contextManager.setZoneVariable(zone.getId(), ZONE_AREA, area);
			m_contextManager.setZoneVariable(zone.getId(), ZONE_VOLUME, volume);
		}

		@Override
		public void zoneAdded(Zone zone) {
			// the zone is new => update the zone properties.
			updateZoneDimensionProperties(zone);
		}

		@Override
		public void zoneRemoved(Zone zone) {
			// i trust iCASA for removing all variables.
		}

		@Override
		public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {
			// a zone move may change some variables (in the future, if we add
			// some coordinates to the variables) so update.
			updateZoneDimensionProperties(zone);
		}

		@Override
		public void zoneResized(Zone zone) {
			// obviously, we need to update dimension when the zone is resized.
			updateZoneDimensionProperties(zone);
		}

		@Override
		public void zoneParentModified(Zone zone, Zone oldZone, Zone newZone) {}

		@Override
		public void deviceAttached(Zone zone, LocatedDevice locatedDevice) {}

		@Override
		public void deviceDetached(Zone zone, LocatedDevice locatedDevice) {}

		@Override
		public void zoneVariableAdded(Zone zone, String s) {}

		@Override
		public void zoneVariableRemoved(Zone zone, String s) {}

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue,
		        Object newValue) {}
	};

	/**
	 * Convert room dimension in pixels to meters.
	 * 
	 * @param pixels
	 *            the dimension in pixels
	 * @return the dimension in meters.
	 */
	private static double pixelsToMeters(double pixels) {
		return pixels * SCALE_FACTOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.TechnicalService#getComputedGlobalVariables()
	 */
	@Override
	public Set<Variable> getComputedGlobalVariables() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.TechnicalService#getComputedZoneVariables()
	 */
	@Override
	public Set<Variable> getComputedZoneVariables() {
		// convert the array of Computed variables to a set
		Set<Variable> computedVariables = new HashSet<Variable>();
		for (Variable computedVariable : COMPUTED_ZONE_VARIABLES) {
			computedVariables.add(computedVariable);
		}
		// return the converted result :
		return computedVariables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.TechnicalService#getRequiredGlobalVariables()
	 */
	@Override
	public Set<Variable> getRequiredGlobalVariables() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.TechnicalService#getRequiredZoneVariables()
	 */
	@Override
	public Set<Variable> getRequiredZoneVariables() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.TechnicalService#getUsedDevices()
	 */
	@Override
	public Set<LocatedDevice> getUsedDevices() {
		return Collections.emptySet();
	}

	/**
	 * Start the technical service.
	 * 
	 * @throws Throwable
	 */
	@Validate
	private void start() {
		L.info("The dimension service is starting");
		// add the listener responsible for updating dimension.

		m_contextManager.addListener(m_zoneListener);

	}

	/**
	 * Stop the technical service.
	 */
	@Invalidate
	private void stop() {
		L.info("The dimension service is stopping");

		// remove the listener responsible for updating dimension.
        if (m_contextManager != null)
		    m_contextManager.removeListener(m_zoneListener);
	}
}
