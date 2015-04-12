package fr.liglab.adele.icasa.alarm;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;


@Component
@Instantiate
@Provides
public class AlarmServiceImpl implements AlarmService {

    private boolean cameraStatus = false;

    private final Object m_lockCamera = new Object();

    private boolean soundStatus = false;

    private final Object m_lockSound = new Object();

    @Override
    public void setAlarmCameraStatus(boolean status) {
        synchronized (m_lockCamera){
            cameraStatus = status;
        }
    }

    @Override
    public void setAlarmSoundStatus(boolean status) {
        synchronized (m_lockSound){
            soundStatus = status;
        }
    }

    @Override
    public boolean getAlarmCameraStatus() {
        synchronized (m_lockCamera){
            return cameraStatus;
        }
    }

    @Override
    public boolean getAlarmSoundStatus() {
        synchronized (m_lockSound){
            return soundStatus;
        }
    }

    @Override
    public void fireAlarm() {

    }

    @Override
    public void stopAlarm() {

    }
}
