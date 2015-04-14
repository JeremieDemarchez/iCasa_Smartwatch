package fr.liglab.adele.icasa.home.live.configurator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightManagerListener;
import fr.liglab.adele.icasa.alarm.AlarmService;
import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationManager;
import fr.liglab.adele.icasa.application.ApplicationTracker;
import fr.liglab.adele.icasa.mode.ModeService;
import fr.liglab.adele.icasa.mode.ModeServiceImpl;
import fr.liglab.adele.icasa.notification.NotificationService;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.FormParameter;
import org.wisdom.api.annotations.Opened;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.websockets.Publisher;

import java.util.HashMap;
import java.util.Map;

@Component
@Instantiate
@Provides
public class HomeLiveConfigurator extends DefaultController implements ApplicationTracker,AccessRightManagerListener {

    public final static String HOMELIVE_WEB_SOCKET = "/homelive/ws";

    private  final Logger m_logger = LoggerFactory
            .getLogger(ModeServiceImpl.class);

    private final Map<String,HomeLiveApplicationConfiguration> homeLiveConfigurationAppMap = new HashMap<>();

    private final Object m_lock = new Object();

    @Requires
    Json json;

    @Requires
    Publisher publisher;

    @Requires
    ModeService modeService;

    @Requires
    NotificationService notificationService;

    @Requires
    AlarmService alarmService;

    @Requires
    AccessManager accessManager;

    @Requires
    ApplicationManager applicationManager;

    public HomeLiveConfigurator(){

    }

    @Validate
    public void start(){
        m_logger.info("HOME LIVE CONFIGURATOR Service STARTING");
        applicationManager.addApplicationListener(this);
        accessManager.addListener(this);
    }

    @Invalidate
    public void stop(){
        m_logger.info("HOME LIVE CONFIGURATOR Service STOPPING");
        accessManager.removeListener(this);
        applicationManager.removeApplicationListener(this);
    }

    @Opened("/homelive/ws")
    public void opened(@Parameter("client") String client) {
        synchronized (m_lock){
            for (String id : homeLiveConfigurationAppMap.keySet()){
                HomeLiveApplicationConfiguration appliConfig = homeLiveConfigurationAppMap.get(id);
                Map<String,String> devicePermissions = appliConfig.getDeviceWithPermissions();
                for (String deviceId : devicePermissions.keySet()){
                    publisher.send(HOMELIVE_WEB_SOCKET, client,(new SendPackage(id,deviceId,devicePermissions.get(deviceId),modeService.getCurrentMode())).toJson());
                }
            }
        }

    }

    @Route(method = HttpMethod.POST,uri = "/homelive/permission")
    public Result setPermission(@FormParameter("appliId") String appliId,@FormParameter("deviceId") String deviceId,@FormParameter("permission") String permission,@FormParameter("mode") String mode){
        if (homeLiveConfigurationAppMap.containsKey(appliId)){
            Map<String,String> returnMap =  homeLiveConfigurationAppMap.get(appliId).updatePermission(deviceId,permission,mode);
            return ok();
        }
        return badRequest();
    }

    @Route(method = HttpMethod.GET,uri = "/homelive/mode")
    public Result getMode(){
        return ok(modeService.getCurrentMode()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/mode")
    public Result setMode(@FormParameter("mode") String modeName){
        modeService.setCurrentMode(modeName);
        return ok(modeService.getCurrentMode()).json();
    }

    @Route(method = HttpMethod.GET,uri = "/homelive/notification")
    public Result getUserAddress(){
        return ok(notificationService.getUserAddress()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/notification")
    public Result setUserAddress(@FormParameter("mode") String userAdress){
        notificationService.setUserAddress(userAdress);
        return ok(notificationService.getUserAddress()).json();
    }

    @Route(method = HttpMethod.GET,uri = "/homelive/alarm/camera")
    public Result getAlarmCamera(){
        return ok(alarmService.getAlarmCameraStatus()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/alarm/camera")
    public Result setAlarmCamera(@FormParameter("mode") Boolean cameraStatus){
        alarmService.setAlarmCameraStatus(cameraStatus);
        return ok(alarmService.getAlarmCameraStatus()).json();
    }

    @Route(method = HttpMethod.GET,uri = "/homelive/alarm/sound")
    public Result getAlarmSound(){
        return ok(alarmService.getAlarmSoundStatus()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/alarm/sound")
    public Result setAlarmSound(@FormParameter("mode") Boolean soundStatus){
        alarmService.setAlarmSoundStatus(soundStatus);
        return ok(alarmService.getAlarmSoundStatus()).json();
    }

    @Override
    public void addApplication(Application app) {

    }

    @Override
    public void removeApplication(Application app) {

    }

    @Override
    public void deploymentPackageAdded(Application app, String symbolicName) {


    }

    @Override
    public void deploymentPackageRemoved(Application app, String symbolicName) {

    }

    @Override
    public void bundleAdded(Application app, String symbolicName) {
        synchronized (m_lock){
            if (homeLiveConfigurationAppMap.containsKey(app.getId())){
                //APP DEJA ENREGISTRE
            }else {
                homeLiveConfigurationAppMap.put(app.getId(),new HomeLiveApplicationConfiguration(app.getId(),accessManager,m_logger,modeService.getCurrentMode()));
                HomeLiveApplicationConfiguration appliConfig = homeLiveConfigurationAppMap.get(app.getId());
                Map<String,String> devicePermissions = appliConfig.getDeviceWithPermissions();
                for (String deviceId : devicePermissions.keySet()){
                    publisher.publish(HOMELIVE_WEB_SOCKET, (new SendPackage(app.getId(), deviceId, devicePermissions.get(deviceId),modeService.getCurrentMode())).toJson());
                }
            }
        }
    }

    @Override
    public void bundleRemoved(Application app, String symbolicName) {
        synchronized (m_lock){
            if (homeLiveConfigurationAppMap.containsKey(app.getId())){
                //APP DEJA ENREGISTRE
                homeLiveConfigurationAppMap.remove(app.getId());
            }
        }
    }

    @Override
    public void onAccessRightAdded(AccessRight accessRight) {
        String appId = accessRight.getApplicationId();
        synchronized (m_lock){
            if (homeLiveConfigurationAppMap.containsKey(appId)){
                String policy = accessRight.getPolicy().toString();
                String deviceId = accessRight.getDeviceId();
                publisher.publish(HOMELIVE_WEB_SOCKET,(new SendPackage(appId,deviceId,policy,modeService.getCurrentMode())).toJson());
            }else {
                homeLiveConfigurationAppMap.put(appId, new HomeLiveApplicationConfiguration(appId, accessManager, m_logger,modeService.getCurrentMode()));
                HomeLiveApplicationConfiguration appliConfig = homeLiveConfigurationAppMap.get(appId);
                Map<String,String> devicePermissions = appliConfig.getDeviceWithPermissions();
                for (String deviceId : devicePermissions.keySet()){
                    publisher.publish(HOMELIVE_WEB_SOCKET, (new SendPackage(appId, deviceId, devicePermissions.get(deviceId),modeService.getCurrentMode())).toJson());
                }
            }
        }

    }

    @Override
    public void onAccessRightModified(AccessRight accessRight) {
        String appId = accessRight.getApplicationId();
        synchronized (m_lock){
            if (homeLiveConfigurationAppMap.containsKey(appId)){
                String policy = accessRight.getPolicy().toString();
                String deviceId = accessRight.getDeviceId();
                publisher.publish(HOMELIVE_WEB_SOCKET,(new SendPackage(appId,deviceId,policy,modeService.getCurrentMode())).toJson());
            }else {
                homeLiveConfigurationAppMap.put(appId, new HomeLiveApplicationConfiguration(appId, accessManager, m_logger,modeService.getCurrentMode()));
                HomeLiveApplicationConfiguration appliConfig = homeLiveConfigurationAppMap.get(appId);
                Map<String,String> devicePermissions = appliConfig.getDeviceWithPermissions();
                for (String deviceId : devicePermissions.keySet()){
                    publisher.publish(HOMELIVE_WEB_SOCKET, (new SendPackage(appId, deviceId, devicePermissions.get(deviceId),modeService.getCurrentMode())).toJson());
                }
            }
        }
    }

    @Override
    public void onMethodAccessRightModified(AccessRight accessRight, String methodName) {

    }


    private class SendPackage{

        private final String m_appId;

        private final String m_deviceId;

        private final String m_permission;

        private final String m_mode;

        private SendPackage(String appId,String deviceId,String permission,String mode){
            m_appId = appId;
            m_deviceId = deviceId;
            m_permission = permission;
            m_mode = mode;
        }

        private JsonNode toJson(){
            ObjectNode result = json.newObject();
            result.put("appId", m_appId);
            result.put("deviceId", m_deviceId);
            result.put("permission", m_permission);
            result.put("mode", m_mode);
            return result;
        }
    }
}
