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
package fr.liglab.adele.icasa.service.zone.size.calculator.impl;

import fr.liglab.adele.icasa.service.preferences.Preferences;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;

@Component
@Instantiate
// FIXME : strategy should be "Singleton"
@Provides
public class ZoneSizeCalculatorImpl implements ZoneSizeCalculator {

	/** The Constant DEFAULT_SCALE_FACTOR. */
	public static final double DEFAULT_SCALE_FACTOR = 0.014d; // 1px -> 0.014m

    /** The _context manager provides access to internal representation of zones */
	@Requires
	private ContextManager _contextManager;

    @Requires(optional = true)
    private Preferences _preferences;

	@Override
	public double getXInMeter(String zoneId) {
		Zone zone = _contextManager.getZone(zoneId);
		if (zone!=null)
			return zone.getXLength() * getXScaleFactor();
		return 0;
	}

	/**
	 * Gets the y in meter.
	 * 
	 * @param zoneId
	 *            the zone id
	 * @return the y in meter
	 */
	@Override
	public double getYInMeter(String zoneId) {
		Zone zone = _contextManager.getZone(zoneId);
		if (zone!=null)
			return zone.getYLength() * getYScaleFactor();
		return 0;
	}

    public double getXScaleFactor() {
        double scaleFactor = DEFAULT_SCALE_FACTOR;
        if (_preferences != null) {
            try {
                Object scaleFactorObj = _preferences.getGlobalPropertyValue(X_SCALE_FACTOR_PROP_NAME);
                if (scaleFactorObj != null)
                    scaleFactor = (Double) scaleFactorObj;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        return scaleFactor;
    }

    public double getYScaleFactor() {
        double scaleFactor = DEFAULT_SCALE_FACTOR;
        if (_preferences != null) {
            try {
                Object scaleFactorObj = _preferences.getGlobalPropertyValue(Y_SCALE_FACTOR_PROP_NAME);
                if (scaleFactorObj != null)
                    scaleFactor = (Double) scaleFactorObj;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        return scaleFactor;
    }

    public double getZScaleFactor() {
        double scaleFactor = DEFAULT_SCALE_FACTOR;
        if (_preferences != null) {
            try {
                Object scaleFactorObj = _preferences.getGlobalPropertyValue(Z_SCALE_FACTOR_PROP_NAME);
                if (scaleFactorObj != null)
                    scaleFactor = (Double) scaleFactorObj;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        return scaleFactor;
    }

    /**
     * Gets the z in meter.
     *
     * @param zoneId
     *            the zone id
     * @return the z in meter
     */
    @Override
    public double getZInMeter(String zoneId) {
        Zone zone = _contextManager.getZone(zoneId);
        if (zone!=null)
            return zone.getZLength() * getZScaleFactor();
        return 0;
    }

	/**
	 * Gets the surface in meter square.
	 * 
	 * @param zoneId
	 *            the zone id
	 * @return the surface in meter square
	 */
	@Override
	public double getSurfaceInMeterSquare(String zoneId) {
		return getXInMeter(zoneId) * getYInMeter(zoneId);
	}


}