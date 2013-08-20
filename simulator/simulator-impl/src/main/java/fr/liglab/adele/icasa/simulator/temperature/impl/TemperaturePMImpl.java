/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.temperature.impl;


import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.Variable;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTracker;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTrackerCustomizer;
import fr.liglab.adele.icasa.service.scheduler.SpecificClockPeriodicRunnable;
import fr.liglab.adele.icasa.simulator.PhysicalModel;

import fr.liglab.adele.icasa.location.*;
import fr.liglab.adele.icasa.location.util.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.*;

@Component(name = "temperature-model")
@Instantiate(name = "temperature-model-1")
@Provides(specifications = PhysicalModel.class)
public class TemperaturePMImpl implements PhysicalModel, LocatedDeviceTrackerCustomizer, ZoneTrackerCustomizer {

    public static final String TEMPERATURE_PROP_NAME = "Temperature";
    public static final String VOLUME_PROP_NAME = "Volume";

    /**
     * Define constants to compute the value of the thermal capacity
     */
    public static final double AIR_MASS_CAPACITY = 1000; //mass capacity of the air in J/(Kg.K)
    public static final double AIR_MASS = 1.2; //mass of the air in Kg/m^3
    public static final double K = 0.724; // 0.661 < k < 0.787
    public static final double HIGHEST_TEMP = 303.16;
    public static final double LOWER_TEMP = 283.16;
    public static final double DEFAULT_TEMP_VALUE = 293.15; // 20 celsius degrees in kelvin
    public static final int MAX_WALL_DIST = 30; // maximum distance in centimeters between 2 walls to be considered as the same wall

    private volatile long m_lastUpdateTime;

    private Set<Variable> _computedVariables;
    private Set<Variable> _requiredZoneVariables;

    private Object _deviceLock = new Object();

    /*
     * @GardedBy(_deviceLock)
     */
    private Set<LocatedDevice> _temperatureDevices = new HashSet<LocatedDevice>();

    private Object _zoneModelLock = new Object();

    private Map<String /* zone id */, ZoneModel> _zoneModels = new HashMap<String, ZoneModel>();

    @Requires
    private ContextManager _contextMgr;

    @Requires
    private Clock _clock;

    private LocatedDeviceTracker _heaterTracker;
    private LocatedDeviceTracker _coolerTracker;
    private ZoneTracker _zoneTracker;

    private static final Clock _systemClock = SystemClockImpl.SINGLETON;

    private BundleContext _context;

    private ServiceRegistration _computeTempTaskSRef;
    private boolean _computationIsOn;

    public TemperaturePMImpl(BundleContext context) {
        _context = context;
        _computationIsOn = false;

        // workaround of ipojo bug of object member initialization
        _zoneModelLock = new Object();
        _deviceLock = new Object();

        _heaterTracker = new LocatedDeviceTracker(context, Heater.class, this);
        _coolerTracker = new LocatedDeviceTracker(context, Cooler.class, this);
        _zoneTracker = new ZoneTracker(context, this, VOLUME_PROP_NAME);

        _computedVariables = new HashSet<Variable>();
        _computedVariables.add(new Variable(TEMPERATURE_PROP_NAME, Double.class, "in Kelvin"));

        _requiredZoneVariables = new HashSet<Variable>();
        _requiredZoneVariables.add(new Variable(VOLUME_PROP_NAME, Double.class, "volume in cubic meters"));
    }

    @Validate
    private void start() {
        _computationIsOn = true;
        _heaterTracker.open();
        _coolerTracker.open();
        _zoneTracker.open();

        SpecificClockPeriodicRunnable computeTempTask = new SpecificClockPeriodicRunnable() {
            @Override
            public long getPeriod() {
                return 300;
            }

            @Override
            public Clock getClock() {
                return _systemClock;
            }

            @Override
            public String getGroup() {
                return "TempPM-group";
            }

            @Override
            public void run() {
                updateTemperatures();
            }
        };
        _computeTempTaskSRef = _context.registerService(SpecificClockPeriodicRunnable.class.getName(), computeTempTask, new Hashtable());
    }

    private void updateTemperatures() {
        //called by only one thread

        if (!_computationIsOn)
            return;

        long previousUpdateTS = m_lastUpdateTime;
        long newUpdateTS = _clock.currentTimeMillis();
        long timeDiff = newUpdateTS - previousUpdateTS;
        if (timeDiff <= 0)
            return; // do not need to recompute

        Map<String, Double> newTemps = new HashMap<String, Double>();
        synchronized (_zoneModelLock) {
            for (ZoneModel zoneModel : _zoneModels.values()) {
                double newTemp = computeTemperature(zoneModel.getZone(), timeDiff);
                newTemps.put(zoneModel.getZoneId(), newTemp);
            }
        }
        for (String zoneId : newTemps.keySet()) {
            Zone zone = _contextMgr.getZone(zoneId);
            if (zone != null)
                zone.setVariableValue(TEMPERATURE_PROP_NAME, newTemps.get(zoneId));
        }

        m_lastUpdateTime = newUpdateTS;
    }

    @Invalidate
    private void stop() {
       _computationIsOn = false;
       if (_computeTempTaskSRef != null) {
           _computeTempTaskSRef.unregister();
           _computeTempTaskSRef = null;
       }

       _zoneTracker.close();
       _heaterTracker.close();
       _coolerTracker.close();
    }

    private boolean propChangeHasImpactOnPower(LocatedDevice locatedDevice, String propName) {
        return (Heater.HEATER_POWER_LEVEL.equals(propName) ||
                Heater.HEATER_MAX_POWER_LEVEL.equals(propName) ||
                Cooler.COOLER_POWER_LEVEL.equals(propName) ||
                Cooler.COOLER_MAX_POWER_LEVEL.equals(propName));
    }

    @Override
    public Set<Variable> getComputedZoneVariables() {
        return Collections.unmodifiableSet(_computedVariables);
    }

    @Override
    public Set<Variable> getRequiredZoneVariables() {
        return Collections.unmodifiableSet(_requiredZoneVariables);
    }

    @Override
    public Set<LocatedDevice> getUsedDevices() {
        synchronized (_deviceLock) {
            return Collections.unmodifiableSet(new HashSet<LocatedDevice>(_temperatureDevices));
        }
    }

    private Set<LocatedDevice> getTemperatureDevicesFromZone(Zone zone) {
        Set<LocatedDevice> devices = new HashSet<LocatedDevice>();
        //TODO implement it

        return devices;
    }

    /**
     * Computes the temperature property value of specified zone according to time difference from the last computation.
     *
     * @param zone a zone
     * @param timeDiff time difference in ms from the last computation
     * @return the temperature computed for time t + dt
     */
    private double computeTemperature(Zone zone, long timeDiff) {
        String zoneId = zone.getId();

        double newTemperature = DEFAULT_TEMP_VALUE; // 20 degrees by default
        synchronized (_zoneModelLock) {
            ZoneModel zoneModel = _zoneModels.get(zoneId);

            double currentTemperature = DEFAULT_TEMP_VALUE; // 20 degrees by default
            Object zoneTemperature = zone.getVariableValue(TEMPERATURE_PROP_NAME);
            if (zoneTemperature != null) {
                try {
                    currentTemperature = (Double) zoneTemperature; //TODO manage external temperature
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            computeTotalPower(zoneModel); //TODO should be computed on the fly
            double powerLevelTotal = zoneModel.getTotalPower();

            newTemperature = currentTemperature + ((powerLevelTotal * timeDiff) / zoneModel.getThermalCapacity());
            for (Map.Entry<String, Double> zoneWallSurfaceEntry : zoneModel.getWallSurfaces()) {
                String otherZoneId = zoneWallSurfaceEntry.getKey();
                double zoneWallSurface = (Double) zoneWallSurfaceEntry.getValue();
                Zone otherZone = _contextMgr.getZone(otherZoneId);
                if (otherZone != null) {
                    Object otherZoneTemperature = _contextMgr.getZone(otherZoneId).getVariableValue(TEMPERATURE_PROP_NAME);
                    if (otherZoneTemperature != null)
                        newTemperature += (K * zoneWallSurface * (((Double) otherZoneTemperature) - currentTemperature)
                                * timeDiff) / zoneModel.getThermalCapacity();
                }
            }

            /**
             * Clipping function to saturate the temperature at a certain level
             */
            if (newTemperature > HIGHEST_TEMP)
                newTemperature = HIGHEST_TEMP;
            else if (newTemperature > LOWER_TEMP)
                newTemperature = LOWER_TEMP;
        }

        return newTemperature;
    }

    private void computeTotalPower(ZoneModel zoneModel) {
        synchronized (_zoneModelLock) {
            zoneModel.setTotalPower(0);

            for (LocatedDevice device : zoneModel.getDevices()) {
                Object deviceObj = device.getDeviceObject();
                if (deviceObj instanceof Heater) {
                    Heater heater = (Heater) deviceObj;
                    zoneModel.addPower(heater.getPowerLevel() * heater.getMaxPowerLevel());
                } else if (deviceObj instanceof Cooler) {
                    Cooler cooler = (Cooler) deviceObj;
                    zoneModel.reducePower(cooler.getPowerLevel() * cooler.getMaxPowerLevel());
                }
            }
        }
    }

    private void setThermalCapacity(Zone zone, Double zoneVolume) {

        double newVolume = 1.0d; // use this value as default to avoid divide by zero
        if ((zoneVolume != null) && (((Double) zoneVolume) > 0.0d))
            newVolume = (Double) zoneVolume;

        String zoneId = zone.getId();
        double thermalCapacity = AIR_MASS * AIR_MASS_CAPACITY * newVolume;

        synchronized (_zoneModelLock) {
            ZoneModel zoneModel = _zoneModels.get(zoneId);
            if (zoneModel != null) {
                zoneModel.setThermalCapacity(newVolume);
            }
        }
    }


    /*
     * LocateDeviceTrackerCustomizer
     */

    @Override
    public boolean addingDevice(LocatedDevice locatedDevice) {
        return true;
    }

    @Override
    public void addedDevice(LocatedDevice locatedDevice) {
        synchronized (_deviceLock) {
            _temperatureDevices.add(locatedDevice);
        }

        synchronized(_zoneModelLock) {
            for (ZoneModel zoneModel : _zoneModels.values()) {
                if (zoneModel.getZone().contains(locatedDevice))
                    zoneModel.addDevice(locatedDevice);
            }
        }
    }

    @Override
    public void modifiedDevice(LocatedDevice locatedDevice, String propName, Object oldValue, Object newValue) {
        if (!propChangeHasImpactOnPower(locatedDevice, propName))
            return;

        //do not need to update total power of a zone, already done in temperature computation step
    }

    @Override
    public void movedDevice(LocatedDevice locatedDevice, Position oldPosition, Position newPosition) {
        //TODO implement it
    }

    @Override
    public void removedDevice(LocatedDevice locatedDevice) {
        synchronized (_deviceLock) {
            _temperatureDevices.remove(locatedDevice);
        }

        synchronized(_zoneModelLock) {
            for (ZoneModel zoneModel : _zoneModels.values()) {
                zoneModel.removeDevice(locatedDevice); // do not need to test if it is contained
            }
        }
    }

    /*
    * ZoneTrackerCustomizer
    */

    @Override
    public boolean addingZone(Zone zone) {
        return true;
    }

    @Override
    public void addedZone(Zone zone) {
        String zoneId = zone.getId();
        ZoneModel zoneModel = new ZoneModel(zone);

        synchronized (_zoneModelLock) {
            if (!_zoneModels.containsKey(zoneId))
                _zoneModels.put(zoneId, zoneModel);

            //TODO ? init zone model
        }
    }

    @Override
    public void modifiedZone(Zone zone, String variableName, Object oldValue, Object newValue) {
        if (!VOLUME_PROP_NAME.equals(variableName))
            return;

        setThermalCapacity(zone, (Double) newValue);
    }

    @Override
    public void movedZone(Zone zone, Position position, Position position1) {
        // do not need to update thermal capacity done when volume changes

        //TODO implement it
    }

    @Override
    public void resizedZone(Zone zone) {
        // do not need to update thermal capacity done when volume changes

        //TODO implement it
    }

    private static double getContactWallSurface(Zone zone1, Zone zone2) {
        double wallSurface = 0.0;

        int zone1XLength = zone1.getXLength();
        int zone1YLength = zone1.getYLength();
        int zone1ZLength = zone1.getZLength();
        int zone1X1 = zone1.getLeftTopAbsolutePosition().x;
        int zone1Y1 = zone1.getLeftTopAbsolutePosition().y;
        int zone1Z1 = zone1.getLeftTopAbsolutePosition().z;
        int zone1X2 = zone1.getRightBottomAbsolutePosition().x;
        int zone1Y2 = zone1.getRightBottomAbsolutePosition().y;
        int zone1Z2 = zone1.getRightBottomAbsolutePosition().z;

        int zone2XLength = zone2.getXLength();
        int zone2YLength = zone2.getYLength();
        int zone2ZLength = zone2.getZLength();
        int zone2X1 = zone2.getLeftTopAbsolutePosition().x;
        int zone2Y1 = zone2.getLeftTopAbsolutePosition().y;
        int zone2Z1 = zone2.getLeftTopAbsolutePosition().z;
        int zone2X2 = zone2.getRightBottomAbsolutePosition().x;
        int zone2Y2 = zone2.getRightBottomAbsolutePosition().y;
        int zone2Z2 = zone2.getRightBottomAbsolutePosition().z;

        int interX = getIntersectionLength(zone1X1, zone1X2, zone2X1, zone2X2);
        int interY = getIntersectionLength(zone1Y1, zone1Y2, zone2Y1, zone2Y2);
        int interZ = getIntersectionLength(zone1Z1, zone1Z2, zone2Z1, zone2Z2);
        int distX = getDistanceBetweenIntervals(zone1X1, zone1X2, zone2X1, zone2X2);
        int distY = getDistanceBetweenIntervals(zone1Y1, zone1Y2, zone2Y1, zone2Y2);
        int distZ = getDistanceBetweenIntervals(zone1Z1, zone1Z2, zone2Z1, zone2Z2);

        if ((interX > 0) && (interY > 0) && (interZ > 0)) {

            wallSurface = 0.0;
        } else if (zone1X1 >= zone2X1) {
            // case of Zone1 into Zone2
            wallSurface = 0.0;
        }

        return wallSurface;
    }

    private static int getMin(int n1, int n2, int n3) {
        return Math.min(n1, Math.min(n2, n3));
    }

    private static int getIntersectionLength(int coord1Min, int coord1Max, int coord2Min, int coord2Max) {
       if ((coord2Min > coord1Max) || (coord1Min > coord2Max))
           return 0;
       if (coord1Max >= coord2Min)
           return coord2Max - coord1Min;
       else if (coord2Max >= coord1Min)
           return coord1Max - coord2Min;

       return 0;
    }

    private static int getDistanceBetweenIntervals(int coord1Min, int coord1Max, int coord2Min, int coord2Max) {
        if (coord2Min > coord1Max)
            return coord2Min - coord1Max;
        if (coord1Min > coord2Max)
            return coord1Min - coord2Max;

        return 0;
    }

    @Override
    public void removedZone(Zone zone) {
        String zoneId = zone.getId();

        synchronized (_zoneModelLock) {
            _zoneModels.remove(zone.getId());
            for (ZoneModel zoneModel : _zoneModels.values()) {
               zoneModel.removeWallSurface(zoneId);
            }
        }
    }
}
