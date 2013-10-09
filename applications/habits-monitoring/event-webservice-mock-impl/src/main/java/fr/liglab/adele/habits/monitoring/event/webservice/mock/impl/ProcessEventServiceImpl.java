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
/**
 * 
 */
package fr.liglab.adele.habits.monitoring.event.webservice.mock.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.liglab.adele.habits.monitoring.event.webservice.api.ProcessEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gabriel
 *
 */
public class ProcessEventServiceImpl implements ProcessEventService {

	
	private static final Logger logger = LoggerFactory.getLogger(ProcessEventServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see fr.liglab.adele.habits.monitoring.event.webservice.api.ProcessEventService#processEventData(java.lang.String, java.lang.String, java.lang.String, java.util.Date, float, java.lang.String)
	 */
	public boolean processEventData(String sensorId, String patientId,
			String eventType, Date dateTime, float reliability, String value) {
		
		
		
		Format formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		

		logger.info("Patient ID --->" + patientId);
		logger.info("Sensor ID --->" + sensorId);
		logger.info("Date  --->" + formatter.format(dateTime));
		return true;
	}

}
