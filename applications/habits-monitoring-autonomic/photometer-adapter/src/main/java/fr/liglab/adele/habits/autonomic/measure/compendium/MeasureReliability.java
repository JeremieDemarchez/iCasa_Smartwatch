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
package fr.liglab.adele.habits.autonomic.measure.compendium;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.habits.monitoring.measure.generator.Measure;

/**
 * Computes a level of realibility decrese the reliability
 * 
 * @author Denis Morand
 * 
 */
public class MeasureReliability {

	private static final Logger logger = LoggerFactory
			.getLogger(MeasureReliability.class);
	private int detecteurs_on;
	private int thresold_illuminance;
	private Map<String, Float> m_map;

	public MeasureReliability() {
		detecteurs_on = 0;
		m_map = new HashMap<String, Float>();
	}

	public Data process(Data data) {
		String port;
		if (data != null) {
			Measure measure = (Measure) data.getContent();
			port = data.getLastReceivingPort();
			if ((port != null) && port.equalsIgnoreCase("in-photometer")) {
				fromPortPhotometer((MeasurePhotoMeter) measure);
			} else {
				fromPortPresence(measure);
			}
		}
		return data;
	}

	private void fromPortPresence(Measure measure) {
		float reliability;
		Float illuminance;
		String location;
		if (measure.getSensorValue()) {
			detecteurs_on++;
		} else {
			if (detecteurs_on > 0)
				detecteurs_on--;
		}
		reliability = (float) ((90 - (10 * detecteurs_on)));
		location = measure.getLocalisation();
		if ((location != null) && (location.length() > 0)) {
			illuminance = m_map.get(measure.getLocalisation());
			if ((illuminance != null) &&(illuminance >= thresold_illuminance) ){
				reliability -= illuminance;
			}
		}
		measure.setRealibility(reliability);
		logger.info("Measure with a reliability ={}%", reliability);
	}

	private void fromPortPhotometer(MeasurePhotoMeter measure) {
		String location = measure.getLocalisation();

		if ((location != null) && (location.length() > 0)) {
			m_map.put(location, new Float(((MeasurePhotoMeter) measure).getIlluminance()));
			if (logger.isInfoEnabled()) {
				logger.info("Illuminance level [{}] set for location [{}]",
						m_map.get(location), location);
			}
		}
	}

}
