package fr.liglab.adele.icasa.gas.alarm.sys;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.util.EmptyDeviceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GasAlarmSystemApplication extends EmptyDeviceListener {

	/**
	 * @author jeremy
	 * 
	 */
	private class AlarmThread extends Thread {

		private boolean stopped = false;

		public AlarmThread(String threadName) {
			super(threadName);
		}

		public void stopAlarm() {
			stopped = true;
		}

		@Override
		public void run() {

			while (!stopped) {

				if (co2LevelTooHigh == true) {

					List<BinaryLight> lightsToPilot = new ArrayList<BinaryLight>();
					synchronized (binaryLights) {
						for (BinaryLight binaryLight : binaryLights)
							lightsToPilot.add(binaryLight);
					}

					for (BinaryLight binaryLight : lightsToPilot) {
						binaryLight.setPowerStatus(false);
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                    for (BinaryLight binaryLight : lightsToPilot) {
                        binaryLight.setPowerStatus(true);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


				}

				else {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static double SEUIL_CO2_CRITIC = 3.8;

	/** Field for binaryLight dependency */
	private BinaryLight[] binaryLights;

	/** Field for carbonDioxydeSensor dependency */
	private CarbonDioxydeSensor[] carbonDioxydeSensors;

	public Boolean co2LevelTooHigh = false;

	private AlarmThread alarmThread = null;

	/** Bind Method for null dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
		//do nothing
	}

	/** Unbind Method for null dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
        //do nothing
	}

	/** Bind Method for null dependency */
	public void bindCarbonDioxydeSensor(
			CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
		carbonDioxydeSensor.addListener(this);
	}

	/** Unbind Method for null dependency */
	public void unbindCarbonDioxydeSensor(
			CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
		carbonDioxydeSensor.removeListener(this);
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// do nothing
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// do nothing
	}

	@Override
	public void devicePropertyModified(GenericDevice device,
			String propertyName, Object oldValue, Object newValue) {

		if (device instanceof CarbonDioxydeSensor) {

			CarbonDioxydeSensor activCO2Sensor = (CarbonDioxydeSensor) device;
			if (activCO2Sensor != null
					&& CarbonDioxydeSensor.CARBON_DIOXYDE_SENSOR_CURRENT_CONCENTRATION
					.equals(propertyName)) {

				Double co2Contration;

				if (activCO2Sensor
						.getPropertyValue(CarbonDioxydeSensor.CARBON_DIOXYDE_SENSOR_CURRENT_CONCENTRATION) != null) {
					// Get the current temp
					co2Contration = (Double) activCO2Sensor
							.getPropertyValue(CarbonDioxydeSensor.CARBON_DIOXYDE_SENSOR_CURRENT_CONCENTRATION);
				} else
					co2Contration = 0.0;

				// Test if the temp is too High
				if (co2Contration > SEUIL_CO2_CRITIC && co2LevelTooHigh != true) {

					co2LevelTooHigh = true;
				} else {
					if (co2LevelTooHigh != false) {
						co2LevelTooHigh = false;
					}
				}
			}
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// do nothing
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// do nothing
	}

	/** Component Lifecycle Method */
	public void stop() {
		alarmThread.stopAlarm();
		alarmThread = null;

        /*
           * It is extremely important to unregister the device listener.
           * Otherwise, iCASA will continue to send notifications to the
           * unpredictable and invalid component instance.
           * This will also causes problem when the bundle is stopped as iCASA
           * will still hold a reference on the device listener object.
           * Consequently, it (and its bundle) won't be garbage collected
           * causing a memory issue known as stale reference.
           */
        for (CarbonDioxydeSensor sensor : carbonDioxydeSensors) {
            sensor.removeListener(this);
        }
	}

	/** Component Lifecycle Method */
	public void start() {
		alarmThread = new AlarmThread("CO2GazAlarmThread"); 
		alarmThread.start();
	}
}
