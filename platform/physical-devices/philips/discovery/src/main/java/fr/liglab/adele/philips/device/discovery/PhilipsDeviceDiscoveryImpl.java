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
/**
 * 
 */
package fr.liglab.adele.philips.device.discovery;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.felix.ipojo.annotations.*;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.connection.impl.PHBridgeInternal;
import com.philips.lighting.hue.sdk.exception.PHHueException;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.rose.RoseMachine;
import com.philips.lighting.hue.sdk.PHSDKListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Discovery for PhilipsHue
 */
@Component(name = "philips.rose.discovery", immediate = true)
public class PhilipsDeviceDiscoveryImpl implements PHSDKListener, Runnable  {

	@Requires(id="rose.machine")
	private RoseMachine roseMachine;

    @Property(name = "philips.hue.discovery.pooling", value = "5000",mandatory = true)
    private Long pollingTime;

    private PHAccessPoint ap;

    private boolean isRunning = false;

    private static final Logger LOG = LoggerFactory.getLogger(PhilipsDeviceDiscoveryImpl.class);

    private Preferences preferences = Preferences.userRoot().node(this.getClass().getName());

    public void onCacheUpdated(int i, PHBridge phBridge) {
    }

    @Validate
    public void init(){

        LOG.debug("Philips discovery initialized");

        PHHueSDK.getInstance().getNotificationManager().registerSDKListener(this);

        isRunning = true;

        Thread t1 = new Thread(this);
        t1.setDaemon(true);
        t1.start();

    }

    @Invalidate
    public void uninit(){

        PHHueSDK.getInstance().getNotificationManager().unregisterSDKListener(this);

        isRunning = false;

    }

    public void onBridgeConnected(PHBridge phBridge) {
        PHHueSDK.getInstance().setSelectedBridge(phBridge);
        PHHueSDK.getInstance().enableHeartbeat(phBridge, pollingTime);
    }

    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        LOG.warn("authentication required, you have 30 seconds to push the button on the bridge");

        PHHueSDK.getInstance().startPushlinkAuthentication(phAccessPoint);
    }

    public void onAccessPointsFound(List<PHAccessPoint> phAccessPoints) {
        ap = phAccessPoints.get(phAccessPoints.size() - 1);
    }

    public void onError(int i, String s) {
        //everytime we dispatch a search and it doesnt find a bridge, is considered as an error
    }

    public void onConnectionResumed(PHBridge phBridge) {
    }

    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        LOG.info("connection lost");
    }

    public void run() {
        PHBridgeSearchManager sm = (PHBridgeSearchManager) PHHueSDK.getInstance().getSDKService(PHHueSDK.SEARCH_BRIDGE);

        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        while (isRunning) {
            doSearch(sm);

            try {
                Thread.sleep(pollingTime);
            } catch (InterruptedException e) {
                LOG.error("failed to put in wait state", e);
            }
        }
    }

    private void doSearch(PHBridgeSearchManager sm) {
        sm.search(true, false);

        if (ap == null) {
            return;
        }

        final String bridgeUsernameKey = "username";

        String username = preferences.get(bridgeUsernameKey, null);

        if (username == null) {
            username = PHBridgeInternal.generateUniqueKey();
            preferences.put(bridgeUsernameKey, username);
            try {
                preferences.flush();
            } catch (BackingStoreException e) {
                LOG.error("failed to store username in java preferences, this will force you to push the bridge button everytime to authenticate", e);
            }
        }

        ap.setUsername(username);

        try {
            PHHueSDK.getInstance().connect(ap);
        } catch (PHHueException e) {
            //LOG.debug("Failed to connect to the Philips Hue AP with the message {}", e.getMessage(), e);
        }

        PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();

        if (PHHueSDK.getInstance().getSelectedBridge() != null) {
            PHBridgeResourcesCache cache = bridge.getResourceCache();

            for (PHLight light : cache.getAllLights()) {
                //generateImportDeclaration(light, bridge);

                if(!light.isReachable()) continue;

                Map props = new Properties();

                String serialNumber = computeSerialNumber(light.getIdentifier());
                props.put(RemoteConstants.ENDPOINT_ID, serialNumber);
                props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "philips");
                props.put("objectClass", new String[] { light.getClass().getName() });
                props.put("id", serialNumber);
                props.put("philips.device.type.code", light.getLightType());
                props.put("philips.device.light", light);
                props.put("philips.device.bridge", bridge);
                props.put("service.imported.configs", "philips");

                EndpointDescription epd = new EndpointDescription(props);

                Boolean found=false;
                for(EndpointDescription ed:roseMachine.getDiscoveredEndpoints()){
                    if(ed.getProperties().get(RemoteConstants.ENDPOINT_ID).equals(serialNumber)){
                        found=true;
                        break;
                    }
                }

                if(!found){
                    roseMachine.putRemote(serialNumber, epd);
                }

            }
        }
    }

	private String computeSerialNumber(String moduleAddress){
		return "philips#"+moduleAddress;
	}

}
