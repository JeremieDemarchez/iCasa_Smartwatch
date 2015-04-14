package fr.liglab.adele.icasa.home.live.configurator;

import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import org.slf4j.Logger;

/**
 * Created by aygalinc on 13/04/15.
 */
public class HomeLiveApplicationConfiguration {

    private final String m_applicationId;

    private final AccessManager accessManager;

    private final Logger m_logger   ;
    public HomeLiveApplicationConfiguration(String appliId, AccessManager accessManage,Logger logger){
        m_applicationId = appliId;
        this.accessManager = accessManage;
        m_logger =logger;
    }

    public void addDeviceWithPermission(String deviceId,boolean permission){
      /*  try {
            jsonAccessRight = AccessRightJSON.fromString(content);
            if (permission){
                right = manager.setDeviceAccess(m_applicationId,deviceId, DeviceAccessPolicy.fromString(jsonAccessRight.getString("policy")));
            }
        } catch (Exception e) {

        }*/
    }


    public void removeDevice(String deviceId){

    }

    public void updatePermission(){

    }
}
