/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.simulator.illuminance.impl;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.Variable;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.location.*;
import fr.liglab.adele.icasa.simulator.PhysicalModel;
import org.apache.felix.ipojo.annotations.*;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component(name = "illuminance-model")
@Instantiate(name = "illuminance-model-1")
@Provides(specifications = PhysicalModel.class)
public class IlluminancePMImpl implements PhysicalModel, ZoneListener, LocatedDeviceListener {

    /**
     * Rought Constant to establish the correspondance between power & illuminance
     */
    public static final double LUMENS_CONSTANT_VALUE = 680.0d;

    /**
     * 1px -> 0.014m //TODO
     */
    public static final double ZONE_SCALE_FACTOR = 0.014d;
    public static final String ILLUMINANCE_PROP_NAME = "Illuminance";

    private Set<Variable> _computedVariables;
    private Set<Variable> _requiredZoneVariables;

    private Object _deviceLock = new Object();

    /*
     * @gardedBy(_deviceLock)
     */
    private Set<LocatedDevice> _lights = new HashSet<LocatedDevice>();

    private Object _zoneLock = new Object();

    @Requires
    private ContextManager _contextMgr;

    public IlluminancePMImpl() {
        // workaround of ipojo bug of object member initialization
        _zoneLock = new Object();
        _deviceLock = new Object();
    }

    @Override
    public void deviceAdded(LocatedDevice locatedDevice) {
        GenericDevice device = locatedDevice.getDeviceObject();
        if (!((device != null) &&
                isLight(device)))
            return; // ignore it

        synchronized (_deviceLock) {
            _lights.add(locatedDevice);
        }
        updateZones(locatedDevice);
    }

    private boolean isLight(GenericDevice device) {
        return ((device instanceof BinaryLight) || (device instanceof DimmerLight));
    }

    @Override
    public void deviceRemoved(LocatedDevice locatedDevice) {
        GenericDevice device = locatedDevice.getDeviceObject();
        if (!((device != null) &&
                isLight(device)))
            return; // ignore it

        synchronized (_deviceLock) {
            _lights.remove(locatedDevice);
        }
        updateZones(locatedDevice);
    }

    @Override
    public void devicePropertyModified(LocatedDevice locatedDevice, String propName, Object value) {
        updateZonesIfPropChanged(locatedDevice, propName);
    }

    private void updateZonesIfPropChanged(LocatedDevice locatedDevice, String propName) {
        if (BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL.equals(propName) ||
                BinaryLight.BINARY_LIGHT_POWER_STATUS.equals(propName) ||
                DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL.equals(propName) ||
                DimmerLight.DIMMER_LIGHT_POWER_LEVEL.equals(propName)) {

            updateZones(locatedDevice);
        }
    }

    @Override
    public void devicePropertyAdded(LocatedDevice locatedDevice, String propName) {
        updateZonesIfPropChanged(locatedDevice, propName);
    }

    @Override
    public void devicePropertyRemoved(LocatedDevice locatedDevice, String propName) {
        //do nothing
    }

    @Override
    public void deviceMoved(LocatedDevice locatedDevice, Position oldPosition) {
        updateZones(oldPosition);
        updateZones(locatedDevice);
    }

    @Override
    public void deviceAttached(LocatedDevice locatedDevice, LocatedDevice locatedDevice1) {
        //do nothing
    }

    @Override
    public void deviceDetached(LocatedDevice locatedDevice, LocatedDevice locatedDevice1) {
        //do nothing
    }

    private void updateZones(LocatedDevice locatedDevice) {
        updateZones(locatedDevice.getCenterAbsolutePosition());
    }

    private void updateZones(Position devicePosition) {
        Set<Zone> zonesToUpdate = getZones(devicePosition);
        for (Zone zone : zonesToUpdate) {
            updateIlluminance(zone);
        }
    }

    private Set<Zone> getZones(Position devicePosition) {
        List<Zone> zones = _contextMgr.getZones();
        Set<Zone> zonesToUpdate = new HashSet<Zone>();
        for (Zone zone : zones) {
            if (zone.contains(devicePosition))
                zonesToUpdate.add(zone);
        }
        return zonesToUpdate;
    }

    private Set<Zone> getZones(LocatedDevice locatedDevice) {
        Position devicePosition = locatedDevice.getCenterAbsolutePosition();
        return getZones(devicePosition);
    }

    public IlluminancePMImpl(Set<Variable> _computedVariables) {
        this._computedVariables = new HashSet<Variable>();
        _computedVariables.add(new Variable(ILLUMINANCE_PROP_NAME, Double.class, "in lux"));

        _requiredZoneVariables = new HashSet<Variable>();
        _requiredZoneVariables.add(new Variable("Surface", Double.class, "surface in square meters"));
    }

    @Override
    public Set<Variable> getComputedZoneVariables() {
        return _computedVariables;
    }

    @Override
    public Set<Variable> getRequiredZoneVariables() {
        return _requiredZoneVariables;
    }

    @Override
    public Set<LocatedDevice> getUsedDevices() {
        return Collections.unmodifiableSet(new HashSet<LocatedDevice>(_lights));
    }

    @Validate
    private void start() {
        _contextMgr.addListener(this);
    }

    @Invalidate
    private void stop() {
        _contextMgr.removeListener(this);
    }

    /**
     * Returns a new mutable set of all light devices for each call.
     *
     * @return a new mutable set of all light devices for each call.
     */
    private Set<GenericDevice> getLightDevices() {
        Set<GenericDevice> devices = new HashSet<GenericDevice>();
        synchronized (_deviceLock) {
            for (LocatedDevice locatedDevice : _lights) {
                GenericDevice deviceObject = locatedDevice.getDeviceObject();
                if (deviceObject != null)
                    devices.add(deviceObject);
            }
        }
        return devices;
    }

    private Set<GenericDevice> getLightDevicesFromZone(Zone zone) {
        Set<GenericDevice> devices = getLightDevices();
        Set<GenericDevice> filteredDevices = new HashSet<GenericDevice>();
        for (GenericDevice device : devices) {
            Position devicePosition = _contextMgr.getDevicePosition(device.getSerialNumber());
            if ((devicePosition != null) && (zone.contains(devicePosition))) {
                filteredDevices.add(device);
            }
        }

        return filteredDevices;
    }

    /**
     * Computes and updates the illuminance property value of specified zone .
     * The formula used to compute the illuminance is :
     * Illuminance [cd/m² or lux]=(power[W]*680.0[lumens])/surface[m²]
     *
     * @param zone a zone
     */
    private void updateIlluminance(Zone zone) {
        synchronized (_zoneLock) {
            double returnedIlluminance = 0.0; //TODO manage external illuminance
            int height = zone.getHeight();
            int width = zone.getWidth();
            double surface = ZONE_SCALE_FACTOR * height * ZONE_SCALE_FACTOR * width;
            double powerLevelTotal = 0.0d;

            Set<GenericDevice> devices = getLightDevicesFromZone(zone);
            for (GenericDevice device : devices) {
                if (device instanceof BinaryLight) {
                    BinaryLight binaryLight = (BinaryLight) device;
                    powerLevelTotal += binaryLight.getPowerStatus() ? binaryLight.getMaxPowerLevel() : 0.0d;
                } else if (device instanceof DimmerLight) {
                    DimmerLight dimmerLight = (DimmerLight) device;
                    powerLevelTotal += dimmerLight.getPowerLevel() * dimmerLight.getMaxPowerLevel();
                }
            }

            if (!devices.isEmpty())
                returnedIlluminance += ((powerLevelTotal / devices.size()) * LUMENS_CONSTANT_VALUE) / surface;

            zone.setVariableValue(ILLUMINANCE_PROP_NAME, returnedIlluminance);
        }
    }

    @Override
    public void zoneAdded(Zone zone) {
        updateIlluminance(zone);
    }

    @Override
    public void zoneRemoved(Zone zone) {
        //do nothing
    }

    @Override
    public void zoneMoved(Zone zone, Position position) {
        updateIlluminance(zone);
    }

    @Override
    public void zoneResized(Zone zone) {
        updateIlluminance(zone);
    }

    @Override
    public void zoneParentModified(Zone zone, Zone zone1) {
        //do nothing
    }

    @Override
    public void deviceAttached(Zone zone, LocatedDevice locatedDevice) {
        //do nothing
    }

    @Override
    public void deviceDetached(Zone zone, LocatedDevice locatedDevice) {
        //do nothing
    }

    @Override
    public void zoneVariableAdded(Zone zone, String s) {
        //do nothing  //TODO if manage external illuminance, may have impact
    }

    @Override
    public void zoneVariableRemoved(Zone zone, String s) {
        //do nothing //TODO if manage external illuminance, may have impact
    }

    @Override
    public void zoneVariableModified(Zone zone, String s, Object o) {
        //do nothing //TODO if manage external illuminance, may have impact
    }
}
