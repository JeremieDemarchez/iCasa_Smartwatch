package fr.liglab.adele.icasa.gas.alarm.sys;

/**
 * Created by aygalinc on 03/04/14.
 */
public interface GasAlarmService {

    public void addListener(GasAlarmListener listener);

    public void removeListener(GasAlarmListener listener);


}
