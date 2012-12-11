/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.sound.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.device.sound.AudioSource;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.Zone;

/**
 * Implementation of a simulated audio source device.
 * 
 * @author bourretp
 */
@Component(name="iCASA.AudioSource")
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedAudioSourceImpl extends AbstractDevice implements AudioSource, SimulatedDevice {

    @ServiceProperty(name = AudioSource.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    @ServiceProperty(name = AudioSource.AUDIO_SOURCE_IS_PLAYING, value = "false")
    private boolean m_isPlaying;
    
    @ServiceProperty(name = "state", value = "deactivated")
    private String state;
    
    @ServiceProperty(name = "fault", value = "no")
    private String fault;

    // Unused
    private volatile SimulatedEnvironment m_env;

    @LogConfig
    private ComponentLogger m_logger;

    private AudioSourceStreamImpl m_stream = new AudioSourceStreamImpl();

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Override
    public InputStream getStream() {
        return m_stream;
    }

    @Override
    public synchronized void play() {
        Object oldValue = m_isPlaying;
        m_isPlaying = true;
        m_logger.info("Audio source playback has started");
        notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, AudioSource.AUDIO_SOURCE_IS_PLAYING, oldValue));
    }

    @Override
    public synchronized void pause() {
        Object oldValue = m_isPlaying;
        m_isPlaying = false;
        m_logger.info("Audio source playback has paused");
        notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, AudioSource.AUDIO_SOURCE_IS_PLAYING, oldValue));
    }
    
    @Override
    public synchronized boolean isPlaying() {
        return m_isPlaying;
    }
    

    private class AudioSourceStreamImpl extends InputStream {

        private final Random m_random = new SecureRandom();
        private final byte[] m_buffer = new byte[1];

        @Override
        public int read() throws IOException {
            synchronized (this) {
                if (m_isPlaying) {
                    m_random.nextBytes(m_buffer);
                    return m_buffer[0];
                } else {
                    return -1;
                }
            }
        }

    }
     
     /**
      * sets the state
      */
  	public void setState(String state) {
  		this.state = state;
     }


  	/**
      * @return the state
      */
     public String getState() {
     	return state;
     }


  	/**
      * @return the fault
      */
     public String getFault() {
     	return fault;
     }


  	/**
      * @param fault the fault to set
      */
     public void setFault(String fault) {
     	this.fault = fault;
     }

	@Override
   public void enterInZones(List<Zone> zones) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void leavingZones(List<Zone> zones) {
	   // TODO Auto-generated method stub
	   
   } 

}
