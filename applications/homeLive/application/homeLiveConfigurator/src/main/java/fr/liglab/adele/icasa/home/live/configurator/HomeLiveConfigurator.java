package fr.liglab.adele.icasa.home.live.configurator;

import fr.liglab.adele.icasa.alarm.AlarmService;
import fr.liglab.adele.icasa.mode.ModeService;
import fr.liglab.adele.icasa.mode.ModeServiceImpl;
import fr.liglab.adele.icasa.notification.NotificationService;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.FormParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

@Component
@Instantiate
@Provides
public class HomeLiveConfigurator extends DefaultController {

    private  final Logger m_logger = LoggerFactory
            .getLogger(ModeServiceImpl.class);

    @Requires
    ModeService modeService;

    @Requires
    NotificationService notificationService;

    @Requires
    AlarmService alarmService;

    public HomeLiveConfigurator(){

    }

    @Validate
    public void start(){
        m_logger.info("HOME LIVE CONFIGURATOR Service STARTING");
    }

    @Invalidate
    public void stop(){
        m_logger.info("HOME LIVE CONFIGURATOR Service STOPPING");
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/mode")
    public Result setMode(@FormParameter("mode") String modeName){
        modeService.setCurrentMode(modeName);
        return ok(modeService.getCurrentMode()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/notification")
    public Result setUserAddress(@FormParameter("mode") String userAdress){
        notificationService.setUserAddress(userAdress);
        return ok(notificationService.getUserAddress()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/alarm/camera")
    public Result setAlarmCamera(@FormParameter("mode") Boolean cameraStatus){
        alarmService.setAlarmCameraStatus(cameraStatus);
        return ok(alarmService.getAlarmCameraStatus()).json();
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/alarm/sound")
    public Result setAlarmSound(@FormParameter("mode") Boolean soundStatus){
        alarmService.setAlarmSoundStatus(soundStatus);
        return ok(alarmService.getAlarmSoundStatus()).json();
    }

}
