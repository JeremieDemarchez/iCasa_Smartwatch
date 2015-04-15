package fr.liglab.adele.icasa.alarm;


import fr.liglab.adele.icasa.device.security.Camera;
import fr.liglab.adele.icasa.device.security.Siren;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@Instantiate
@Provides
public class AlarmServiceImpl implements AlarmService {

    private  final Logger m_logger = LoggerFactory
            .getLogger(AlarmServiceImpl.class);

    private boolean cameraStatus = false;

    private final Object m_lockCamera = new Object();

    private boolean soundStatus = false;

    private final Object m_lockSound = new Object();

    private boolean alarmRunning = false;

    private final Object m_lockAlarm = new Object();


    @Requires(optional = true)
    private Siren[] sirens;

    @Requires(optional = true)
    private Camera[] cameras;

    @Override
    public void setAlarmCameraStatus(boolean status) {

        synchronized (m_lockAlarm) {
            if (alarmRunning) {
                synchronized (m_lockCamera){
                    cameraStatus = status;
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
            }else {
                synchronized (m_lockCamera){
                    cameraStatus = status;
                }
            }
        }
    }

    @Override
    public void setAlarmSoundStatus(boolean status) {

        synchronized (m_lockAlarm) {
            if (alarmRunning) {
                synchronized (m_lockSound){
                    soundStatus = status;
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
            }else {
                synchronized (m_lockSound) {
                    soundStatus = status;
                }
            }
        }
    }

    @Override
    public boolean getAlarmCameraStatus() {
        synchronized (m_lockCamera){
            m_logger.info("Nbr of Camera " + cameras.length);
            return cameraStatus;
        }
    }

    @Override
    public boolean getAlarmSoundStatus() {
        synchronized (m_lockSound){
            m_logger.info("Nbr of Siren " + sirens.length);
            return soundStatus;
        }
    }

    @Override
    public void fireAlarm() {
        m_logger.info("Fire Alarm");
        synchronized (m_lockAlarm){
            alarmRunning=true;

            synchronized (m_lockCamera) {
                if (cameraStatus) {
                    for (Camera camera : cameras) {
                        m_logger.info("Start recording Camera" + camera.getSerialNumber());
                        camera.startRecording();
                    }
                }
            }
            synchronized (m_lockSound){
                if (soundStatus){
                    for (Siren siren : sirens){
                        m_logger.info("Start siren" + siren.getSerialNumber());
                        siren.turnOn();
                    }
                }
            }
        }
    }

    @Override
    public void stopAlarm() {
        m_logger.info("Stop Alarm");
        synchronized (m_lockAlarm){
            alarmRunning=false;
            synchronized (m_lockCamera) {
                if (cameraStatus) {
                    for (Camera camera : cameras) {
                        m_logger.info("Stop recording Camera" + camera.getSerialNumber());
                        camera.stopRecording();
                    }
                }
            }
            synchronized (m_lockSound){
                if (soundStatus){
                    for (Siren siren : sirens){
                        m_logger.info("Stop siren " + siren.getSerialNumber());
                        siren.turnOff();
                    }
                }
            }
        }
    }
}
