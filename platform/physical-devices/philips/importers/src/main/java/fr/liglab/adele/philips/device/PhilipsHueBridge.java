package fr.liglab.adele.philips.device;

import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * Created by aygalinc on 03/02/16.
 */
public interface PhilipsHueBridge {

    void updateLightState(PHLight var1, PHLightState var2);
}
