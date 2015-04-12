package fr.liglab.adele.icasa.alarm;

/**
 * Created by aygalinc on 12/04/15.
 */
public interface AlarmService {

    public void setAlarmCameraStatus(boolean status);

    public void setAlarmSoundStatus(boolean status);

    public boolean getAlarmCameraStatus();

    public boolean getAlarmSoundStatus();

    public void fireAlarm();

    public void stopAlarm();

}
