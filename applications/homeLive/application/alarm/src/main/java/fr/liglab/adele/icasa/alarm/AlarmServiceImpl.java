package fr.liglab.adele.icasa.alarm;


import fr.liglab.adele.icasa.device.security.Camera;
import fr.liglab.adele.icasa.device.security.Siren;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;


@Component
@Instantiate
@Provides
public class AlarmServiceImpl implements AlarmService {

    private boolean cameraStatus = false;

    private final Object m_lockCamera = new Object();

    private boolean soundStatus = false;

    private final Object m_lockSound = new Object();

    private boolean alarmRunning = false;

    private final Object m_lockAlarm = new Object();


    @Requires(optional = true)
    Siren[] sirens;

    @Requires(optional = true)
    Camera[] cameras;

    @Override
    public void setAlarmCameraStatus(boolean status) {
        synchronized (m_lockCamera){
            cameraStatus = status;
            synchronized (m_lockAlarm) {
                if (alarmRunning) {
                    if (cameraStatus) {
                        for (Camera camera : cameras) {
                            camera.startRecording();
                        }
                    }else {
                        for (Camera camera : cameras) {
                            camera.stopRecording();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setAlarmSoundStatus(boolean status) {
        synchronized (m_lockSound){
            soundStatus = status;
            synchronized (m_lockAlarm) {
                if (alarmRunning) {
                    if (soundStatus){
                        for (Siren siren : sirens) {
                            siren.turnOn();
                        }
                    }else {
                        for (Siren siren : sirens) {
                            siren.turnOff();
                        }
                    }
                }
            }
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
        synchronized (m_lockAlarm){
            alarmRunning=true;
        }
        synchronized (m_lockCamera) {
            if (cameraStatus) {
                for (Camera camera : cameras) {
                    camera.startRecording();
                }
            }
        }
        synchronized (m_lockSound){
            if (soundStatus){
                for (Siren siren : sirens){
                    siren.turnOn();
                }
            }
        }
    }

    @Override
    public void stopAlarm() {
        synchronized (m_lockAlarm){
            alarmRunning=false;
        }
        synchronized (m_lockCamera) {
            if (cameraStatus) {
                for (Camera camera : cameras) {
                    camera.stopRecording();
                }
            }
        }
        synchronized (m_lockSound){
            if (soundStatus){
                for (Siren siren : sirens){
                    siren.turnOff();
                }
            }
        }
    }
}
