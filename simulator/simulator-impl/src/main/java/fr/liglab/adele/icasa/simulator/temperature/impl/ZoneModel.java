package fr.liglab.adele.icasa.simulator.temperature.impl;

import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Zone;

import java.util.*;

/**
 * Model of a zone used to compute thermal properties.
 *
 * @author Thomas Leveque
 */
public class ZoneModel {

    private double _thermalCapacity;
    private Map<String, Double> _wallSurfaceMap = new HashMap<String, Double>();
    private double _totalPower;
    private Zone _zone;
    private List<LocatedDevice> _devices = new ArrayList<LocatedDevice>();

    public ZoneModel(Zone zone) {
        this._zone = zone;
    }

    public Zone getZone() {
        return _zone;
    }

    public String getZoneId() {
        return _zone.getId();
    }

    public double getThermalCapacity() {
        return _thermalCapacity;
    }

    public void setThermalCapacity(double thermalCapacity) {
        this._thermalCapacity = thermalCapacity;
    }

    public double getTotalPower() {
        return _totalPower;
    }

    public void setTotalPower(int totalPower) {
        this._totalPower = totalPower;
    }

    public void addPower(double powerToAdd) {
        this._totalPower += powerToAdd;
    }

    public void reducePower(double powerToRemove) {
        this._totalPower -= powerToRemove;
    }

    public Set<Map.Entry<String, Double>> getWallSurfaces() {
        return _wallSurfaceMap.entrySet();
    }

    public double getWallSurface(String zoneId) {
        Double surface = _wallSurfaceMap.get(zoneId);
        if (surface == null)
            return 0.0d;
        else
            return surface;
    }

    public void setWallSurface(String zoneId, double wallSurface) {
        _wallSurfaceMap.put(zoneId, wallSurface);
    }

    public void removeWallSurface(String zoneId) {
        _wallSurfaceMap.remove(zoneId);
    }

    public void addDevice(LocatedDevice device) {
        _devices.add(device);
    }

    public void removeDevice(LocatedDevice device) {
        _devices.remove(device);
    }

    /**
     * Returns a mutable list of all devices into this zone.
     *
     * @return all devices into this zone.
     */
    public List<LocatedDevice> getDevices() {
        return new ArrayList(_devices);
    }
}
