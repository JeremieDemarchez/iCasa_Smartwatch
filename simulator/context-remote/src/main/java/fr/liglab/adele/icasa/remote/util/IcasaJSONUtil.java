package fr.liglab.adele.icasa.remote.util;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.util.DateTextUtil;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.remote.impl.DeviceJSON;
import fr.liglab.adele.icasa.remote.impl.ZoneJSON;

public class IcasaJSONUtil {

	public static JSONObject getDeviceJSON(LocatedDevice device, ContextManager _simulationMgr) {

		String deviceType = device.getType();
		if (deviceType == null)
			deviceType = "undefined";

		Position devicePosition = _simulationMgr.getDevicePosition(device.getSerialNumber());

		JSONObject deviceJSON = null;
		try {
			deviceJSON = new JSONObject();
			deviceJSON.putOnce(DeviceJSON.ID_PROP, device.getSerialNumber());
			deviceJSON.putOnce(DeviceJSON.NAME_PROP, device.getSerialNumber());
			deviceJSON.put(DeviceJSON.FAULT_PROP, device.getPropertyValue(GenericDevice.FAULT_PROPERTY_NAME));
			deviceJSON.put(DeviceJSON.LOCATION_PROP, device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME));
			deviceJSON.put(DeviceJSON.STATE_PROP, device.getPropertyValue(GenericDevice.STATE_PROPERTY_NAME));
			deviceJSON.put(DeviceJSON.TYPE_PROP, deviceType);
			if (devicePosition != null) {
				deviceJSON.put(DeviceJSON.POSITION_X_PROP, devicePosition.x);
				deviceJSON.put(DeviceJSON.POSITION_Y_PROP, devicePosition.y);
			}
			JSONObject propObject = new JSONObject();
			for (String property : device.getProperties()) {
				propObject.put(property, device.getPropertyValue(property));
			}
			deviceJSON.put(DeviceJSON.PROPERTIES_PROP, propObject);
		} catch (JSONException e) {
			e.printStackTrace();
			deviceJSON = null;
		}

		return deviceJSON;
	}

	public static JSONObject getDeviceTypeJSON(String deviceTypeStr) {
		JSONObject deviceTypeJSON = null;
		try {
			deviceTypeJSON = new JSONObject();
			deviceTypeJSON.putOnce("id", deviceTypeStr);
			deviceTypeJSON.putOnce("name", deviceTypeStr);
		} catch (JSONException e) {
			e.printStackTrace();
			deviceTypeJSON = null;
		}

		return deviceTypeJSON;
	}

	public static JSONObject getPersonTypeJSON(String personTypeStr) {
		JSONObject personTypeJSON = null;
		try {
			personTypeJSON = new JSONObject();
			personTypeJSON.putOnce("id", personTypeStr);
			personTypeJSON.putOnce("name", personTypeStr);
		} catch (JSONException e) {
			e.printStackTrace();
			personTypeJSON = null;
		}

		return personTypeJSON;
	}
	
	public static JSONObject getZoneJSON(Zone zone) {
		JSONObject zoneJSON = null;
		try {
			String zoneId = zone.getId();
			zoneJSON = new JSONObject();
			zoneJSON.putOnce(ZoneJSON.ID_PROP, zoneId);
			zoneJSON.putOnce(ZoneJSON.NAME_PROP, zoneId);
			zoneJSON.put(ZoneJSON.POSITION_LEFTX_PROP, zone.getLeftTopAbsolutePosition().x);
			zoneJSON.put(ZoneJSON.POSITION_TOPY_PROP, zone.getLeftTopAbsolutePosition().y);
			zoneJSON.put(ZoneJSON.POSITION_RIGHTX_PROP, zone.getRightBottomAbsolutePosition().x);
			zoneJSON.put(ZoneJSON.POSITION_BOTTOMY_PROP, zone.getRightBottomAbsolutePosition().y);
			zoneJSON.put(ZoneJSON.IS_ROOM_PROP, true); // TODO change it when Zone API will be improved

			JSONObject propObject = new JSONObject();
			for (String variable : zone.getVariableNames()) {
				propObject.put(variable, zone.getVariableValue(variable));
			}
			zoneJSON.put(ZoneJSON.VARIABLE_PROP, propObject);

		} catch (JSONException e) {
			e.printStackTrace();
			zoneJSON = null;
		}

		return zoneJSON;
	}
	
	
	public static JSONObject getClockJSON(Clock clock) {
		JSONObject clockJSON = null;
		try {
			clockJSON = new JSONObject();
			clockJSON.putOnce("startDateStr", DateTextUtil.getTextDate(clock.getStartDate()));
			clockJSON.putOnce("startDate", clock.getStartDate());
			clockJSON.putOnce("currentDateStr", DateTextUtil.getTextDate((clock.currentTimeMillis())));
			clockJSON.putOnce("currentTime", clock.currentTimeMillis());
			clockJSON.putOnce("factor", clock.getFactor());
			clockJSON.putOnce("pause", clock.isPaused());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clockJSON;
	}

	
	
	
	
	
}
