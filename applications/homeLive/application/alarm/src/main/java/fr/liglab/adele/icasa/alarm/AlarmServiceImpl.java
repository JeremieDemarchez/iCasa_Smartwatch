package fr.liglab.adele.icasa.alarm;


import fr.liglab.adele.icasa.device.security.Camera;
import fr.liglab.adele.icasa.device.security.Siren;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


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

    private ServiceTracker m_cameraServiceTracker;

    private  final ServiceTrackerCustomizer m_cameraServiceTrackerCustomizer = new CameraServiceTrackerCustomizer();

    private ServiceTracker m_sirenServiceTracker;

    private final ServiceTrackerCustomizer m_sirenServiceTrackerCustomizer = new SirenServiceTrackerCustomizer();

    private final BundleContext m_context;

    private List<Siren> sirens = new ArrayList<Siren>();

    private final Object m_sirenLock = new Object();

    private List<Camera> cameras = new ArrayList<Camera>();

    private final Object m_camerasLock = new Object();

    public AlarmServiceImpl(BundleContext context){
        m_context = context;
    }
    @Validate
    public void start(){
        m_cameraServiceTracker = new ServiceTracker(m_context,Camera.class.getName(),m_cameraServiceTrackerCustomizer);
        m_sirenServiceTracker = new ServiceTracker(m_context,Siren.class.getName(),m_sirenServiceTrackerCustomizer);
        m_cameraServiceTracker.open();
        m_sirenServiceTracker.open();
    }

    @Invalidate
    public void stop(){
        m_cameraServiceTracker.close();
        m_sirenServiceTracker.close();
    }

    @Override
    public void setAlarmCameraStatus(boolean status) {

        synchronized (m_lockAlarm) {
            if (alarmRunning) {
                synchronized (m_lockCamera){
                    cameraStatus = status;
                    if (cameraStatus) {
                        synchronized (m_camerasLock){
                        for (Camera camera : cameras) {
                            camera.startRecording();
                        }
                        }
                    }else {
                        synchronized (m_camerasLock) {
                            for (Camera camera : cameras) {
                                camera.stopRecording();
                            }
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
                        synchronized (m_sirenLock) {
                            for (Siren siren : sirens) {
                                siren.turnOn();
                            }
                        }
                    }else {
                        synchronized (m_sirenLock){
                            for (Siren siren : sirens) {
                                siren.turnOff();
                            }
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
            m_logger.info("Nbr of Camera " + cameras.size());
            return cameraStatus;
        }
    }

    @Override
    public boolean getAlarmSoundStatus() {
        synchronized (m_lockSound){
            m_logger.info("Nbr of Siren " + sirens.size());
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

    private class CameraServiceTrackerCustomizer implements ServiceTrackerCustomizer{
        public CameraServiceTrackerCustomizer(){

        }
        @Override
        public Object addingService(ServiceReference serviceReference) {
            Camera camera = (Camera) m_context.getService(serviceReference);
            synchronized (m_camerasLock){
                cameras.add(camera);
            }
            return camera.getSerialNumber();
        }

        @Override
        public void modifiedService(ServiceReference serviceReference, Object o) {

        }

        @Override
        public void removedService(ServiceReference serviceReference, Object o) {
            Camera camera = (Camera) m_context.getService(serviceReference);
            synchronized (m_camerasLock){
                cameras.remove(camera);
            }
        }
    }

    private class SirenServiceTrackerCustomizer implements ServiceTrackerCustomizer{

        public SirenServiceTrackerCustomizer(){

        }

        @Override
        public Object addingService(ServiceReference serviceReference) {
            Siren siren = (Siren) m_context.getService(serviceReference);
            synchronized (m_sirenLock){
                sirens.add(siren);
            }
            return siren.getSerialNumber();
        }

        @Override
        public void modifiedService(ServiceReference serviceReference, Object o) {

        }

        @Override
        public void removedService(ServiceReference serviceReference, Object o) {
            Siren siren = (Siren) m_context.getService(serviceReference);
            synchronized (m_sirenLock){
                sirens.remove(siren);
            }
        }
    }


}
