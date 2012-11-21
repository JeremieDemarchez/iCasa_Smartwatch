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
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.DevicePositionListener;
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
import org.osgi.framework.*;
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
        _simulMgr.addUserPositionListener(_iCasaListener);
        _simulMgr.addDevicePositionListener(_iCasaListener);

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

    private class ICasaEventListener implements DevicePositionListener, UserPositionListener {

        private UUID _lastEventId = UUID.randomUUID();

        private String generateUUID() {
            _lastEventId = UUID.randomUUID();
            return _lastEventId.toString();
        }

        @Override
        public void devicePositionChanged(String deviceSerialNumber, Position position) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-position-update");
                json.put("id", generateUUID());
                json.put("deviceId", deviceSerialNumber);
                json.put("time", new Date().getTime());
                _eventBroadcaster.broadcast(json.toString());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userPositionChanged(String userName, Position position) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-position-update");
                json.put("id", generateUUID());
                json.put("userId", userName);
                json.put("time", new Date().getTime());
                _eventBroadcaster.broadcast(json.toString());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userAdded(String userName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-added");
                json.put("id", generateUUID());
                json.put("userId", userName);
                json.put("time", new Date().getTime());
                _eventBroadcaster.broadcast(json.toString());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void userRemoved(String userName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "user-removed");
                json.put("id", generateUUID());
                json.put("userId", userName);
                json.put("time", new Date().getTime());
                _eventBroadcaster.broadcast(json.toString());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }


}
