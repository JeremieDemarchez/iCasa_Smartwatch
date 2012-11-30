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
package fr.liglab.adele.icasa.remote.impl;

import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.Zone;
import fr.liglab.adele.icasa.environment.ZoneListener;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.DeviceListener;
import fr.liglab.adele.icasa.environment.SimulationManager.UserPositionListener;
import org.apache.felix.ipojo.annotations.*;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.handler.OnMessage;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.barjo.atmosgi.AtmosphereService;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component(name = "iCasa-event-broadcast")
@Instantiate(name = "iCasa-event-broadcast-1")
public class EventBroadcast extends OnMessage<String> {

    @Property(name = "mapping", value = "/event")
    private String mapping;

    private final List<AtmosphereInterceptor> _interceptors = new ArrayList<AtmosphereInterceptor>();

    @Requires
    private AtmosphereService _atmoService;

    @Requires
    private HttpService _httpService;

    @Requires
    private SimulationManager _simulMgr;

    private Broadcaster _eventBroadcaster;

    private ICasaEventListener _iCasaListener;

    private final BundleContext _context;

    public EventBroadcast(BundleContext context) {
        _context = context;
    }

    @Validate
    private void start() {
        _iCasaListener = new ICasaEventListener();
        _simulMgr.addZoneListener(_iCasaListener);
        _simulMgr.addUserPositionListener(_iCasaListener);
        _simulMgr.addDeviceListener(_iCasaListener);

        _eventBroadcaster = _atmoService.getBroadcasterFactory().get();

        //Register the server (itself)
        _interceptors.add(new AtmosphereResourceLifecycleInterceptor());
        _atmoService.addAtmosphereHandler(mapping, this, _eventBroadcaster, _interceptors);

        //Register the web client
        try {
            _httpService.registerResources("/event", "/web", null);
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    @Invalidate
    private void stop() {
        if (_iCasaListener != null) {
            _simulMgr.removeUserPositionListener(_iCasaListener);
            _simulMgr.removeDevicePositionListener(_iCasaListener);
            _iCasaListener = null;
        }

        _atmoService.removeAtmosphereHandler(mapping);
        _interceptors.clear();

        _httpService.unregister("/event");
    }

    @Override
    public void onMessage(AtmosphereResponse atmosphereResponse, String s) throws IOException {
        atmosphereResponse.getWriter().write(s);
    }

    private UUID _lastEventId = UUID.randomUUID();

    private String generateUUID() {
        _lastEventId = UUID.randomUUID();
        return _lastEventId.toString();
    }

    private void sendEvent(JSONObject event) {
        try {
            event.put("id", generateUUID());
            event.put("time", new Date().getTime());
            _eventBroadcaster.broadcast(event.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class ICasaEventListener implements DeviceListener, UserPositionListener, ZoneListener {

        @Override
        public void devicePositionChanged(String deviceSerialNumber, Position position) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-position-update");
                json.put("deviceId", deviceSerialNumber);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void deviceAdded(String deviceId) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-added");
                json.put("deviceId", deviceId);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void deviceRemoved(String deviceId) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-removed");
                json.put("deviceId", deviceId);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userPositionChanged(String userName, Position position) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-position-update");
                json.put("userId", userName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userAdded(String userName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-added");
                json.put("userId", userName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userRemoved(String userName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-removed");
                json.put("userId", userName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void zoneVariableAdded(Zone zone, String variableName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-variable-added");
                json.put("zoneId", zone.getId());
                json.put("variableName", variableName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void zoneVariableRemoved(Zone zone, String variableName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-variable-removed");
                json.put("zoneId", zone.getId());
                json.put("variableName", variableName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-variable-updated");
                json.put("zoneId", zone.getId());
                json.put("variableName", variableName);
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void zoneMoved(Zone zone) {
            //TODO
        }

        @Override
        public void zoneResized(Zone zone) {
            //TODO
        }

        @Override
        public void zoneParentModified(Zone zone, Zone oldParent) {
            //TODO
        }

        @Override
        public void zoneAdded(Zone zone) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-added");
                json.put("zoneId", zone.getId());
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void zoneRemoved(Zone zone) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-removed");
                json.put("zoneId", zone.getId());
                sendEvent(json);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }


}
