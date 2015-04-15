package fr.liglab.adele.icasa.home.live.configurator;

import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.mode.ModeUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by aygalinc on 13/04/15.
 */
public class HomeLiveApplicationConfiguration {

    private final String m_applicationId;

    private final AccessManager accessManager;

    private final Logger m_logger ;

    private  String m_currentMode ;

    private final Map<String,Map<String,String>> m_permissionByMode =new HashMap<>()  ;

    public HomeLiveApplicationConfiguration(String appliId, AccessManager accessManage,Logger logger,String currentMode){
        m_applicationId = appliId;
        this.accessManager = accessManage;
        m_logger =logger;
        m_currentMode = currentMode;

        Map<String,String> permission = new HashMap<String,String>();
        AccessRight[] appliRights = accessManager.getAccessRight(this.m_applicationId);
        for (AccessRight accessRight : appliRights){
            permission.put(accessRight.getDeviceId(), accessRight.getPolicy().toString());
        }
        m_permissionByMode.put(ModeUtils.AWAY,new HashMap<>(permission));

        m_permissionByMode.put(ModeUtils.HOLIDAYS,new HashMap<>(permission));

        m_permissionByMode.put(ModeUtils.HOME,new HashMap<>(permission));

        m_permissionByMode.put(ModeUtils.NIGHT,new HashMap<>(permission));
    }

    public synchronized void updateCurrentMode(){
        m_logger.info("updateCurrentMode  " );

        Map<String,String> deviceUsed = getDeviceWithPermissions();
        m_permissionByMode.put(m_currentMode,deviceUsed);
    }

    public synchronized void updateModeWithNewDevice(String deviceId,String permission,String mode) {
        m_logger.info("updateModeWithNewDevice  " + deviceId + permission + mode );
        if (m_currentMode.equals(mode)) {
            updateCurrentMode();
        } else {
            if (mode.equals(ModeUtils.AWAY) || mode.equals(ModeUtils.NIGHT) || mode.equals(ModeUtils.HOLIDAYS) || mode.equals(ModeUtils.HOME)) {
                if (permission.equals("total") ||permission.equals("hidden") ){
                    m_permissionByMode.get(mode).put(deviceId,permission);
                }else {
                    m_logger.error(" POLICY INVALID" + permission);
                }
            }else {
                m_logger.error(" MODE NAME INVALID" + mode);
            }
        }
    }

    public synchronized Map<String,String> getDeviceWithPermissions(){
        m_logger.info("getDeviceWithPermissions  " );
        Map<String,String> deviceUsed = new HashMap<>();
        AccessRight[] appliRights = accessManager.getAccessRight(this.m_applicationId);
        for (AccessRight accessRight : appliRights){
            deviceUsed.put(accessRight.getDeviceId(), accessRight.getPolicy().toString());
        }
        return deviceUsed;
    }

    public synchronized String getPermissionAssociatedToDevice(String deviceId, String mode) {
        m_logger.info("getPermissionAssociatedToDevice  " + deviceId + mode);
        String string = "NONE";
        if (mode.equals(ModeUtils.AWAY) || mode.equals(ModeUtils.NIGHT) ||mode.equals(ModeUtils.HOLIDAYS) ||mode.equals(ModeUtils.HOME) ) {
            string = m_permissionByMode.get(mode).get(deviceId);
        }else {
            m_logger.error(" MODE INVALID IN UPDATE PERMISSION" + mode);
        }
        return string;
    }

    private synchronized AccessRight updatePermission(String deviceId,String permission) {
        m_logger.info("Update Permission " + deviceId + permission);
        if (!(accessManager.getAccessRight(m_applicationId,deviceId).getPolicy().toString().equals(permission))) {
            AccessRight[] appliRights = accessManager.getAccessRight(this.m_applicationId);
            for (AccessRight appliRight : appliRights) {
                if (appliRight.getDeviceId().equals(deviceId)) {
                    try {
                        AccessRight right = null;
                        if (permission.equals("total") || permission.equals("hidden")) {
                            right = accessManager.setDeviceAccess(m_applicationId, deviceId, DeviceAccessPolicy.fromString(permission));
                        }
                        return right;
                    } catch (Exception e) {
                        m_logger.error("Update permission Fail ! ");
                    }
                }
            }
            return null;
        }else {
            m_logger.info("Policy not need To be update " + m_applicationId + " device " + deviceId);
            return accessManager.getAccessRight(m_applicationId,deviceId);
        }
    }

    public synchronized Map<String,String> updatePermission(String deviceId,String permission,String Mode) {
        m_logger.error("Update WITH MODE " + deviceId + permission + Mode);
        Map<String,String> returnMap = new HashMap<String,String>();
        if (m_currentMode.equals(Mode)) {
            AccessRight right = updatePermission(deviceId,permission);
            m_permissionByMode.get(m_currentMode).put(right.getDeviceId(),right.getPolicy().toString());
            returnMap.put(right.getDeviceId(),right.getPolicy().toString());

            return returnMap;
        }
        if (Mode.equals(ModeUtils.AWAY) || Mode.equals(ModeUtils.NIGHT) ||Mode.equals(ModeUtils.HOLIDAYS) ||Mode.equals(ModeUtils.HOME) ){

            if (accessManager.getAccessRight(m_applicationId,deviceId) !=null ){
                if(permission.equals("hidden") || permission.equals("total")){
                    m_permissionByMode.get(m_currentMode).put(deviceId,permission);
                    returnMap.put(deviceId,permission);
                    return returnMap;
                }else{
                    m_logger.error(" POLICY INVALID" + permission);
                }
            }else {
                m_logger.error(" DEVICE ID INVALID" +deviceId);
            }
        }
        m_logger.error(" MODE INVALID IN UPDATE PERMISSION" + Mode);
        return returnMap;
    }

    public synchronized void changeCurrentMode(String mode) {
        m_logger.info("Enter in new Mode : " + mode + " will change permission");
        if (mode.equals(ModeUtils.AWAY) || mode.equals(ModeUtils.NIGHT) ||mode.equals(ModeUtils.HOLIDAYS) ||mode.equals(ModeUtils.HOME) ){
            m_currentMode = mode;
            for (String deviceId : m_permissionByMode.get(mode).keySet()){
                m_logger.info("Permission will be update on device " + deviceId + " with policy " + m_permissionByMode.get(mode).get(deviceId));
                updatePermission(deviceId, m_permissionByMode.get(mode).get(deviceId));
            }
            return;
        }else {
            m_logger.error(" INVALID MODE NAME IN CHANGE MODE " + mode);
        }
    }
}
