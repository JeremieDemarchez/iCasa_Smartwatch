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
package fr.liglab.adele.habits.autonomic.measure.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.habits.autonomic.measure.Measure;

/**
 * Computes a level of realibility
 * 
 * @author Denis Morand
 * 
 */
public class MeasureReliability {

	private static final Logger logger = LoggerFactory
			.getLogger(MeasureReliability.class);
	private int detecteurs_on;

	public MeasureReliability() {
		detecteurs_on = 0;
	}

	public Data process(Data data) {
		if (data != null) {
			Measure measure = (Measure) data.getContent();
			if (measure.getSensorValue()) {
				detecteurs_on++;
				/* Decrease by 10% for each sensor on */
				measure.setRealibility((float) ((90 - (10 * detecteurs_on))));
			} else {
				measure.setRealibility((float) 90);
				if (detecteurs_on > 0)
					detecteurs_on--;
			}
			logger.info("Mediator reliability --> reliability set to {}%",
					measure.getRealibility());
		}
		return data;
	}

}
