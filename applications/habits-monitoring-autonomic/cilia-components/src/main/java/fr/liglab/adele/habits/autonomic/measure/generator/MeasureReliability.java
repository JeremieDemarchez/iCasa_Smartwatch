package fr.liglab.adele.habits.autonomic.measure.generator;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.Data;

public class MeasureReliability {

	private static final Logger logger = LoggerFactory
			.getLogger(MeasureReliability.class);
	private Map m_map;

	public MeasureReliability() {
		m_map = new HashMap();
	}

	public Data process(Data data) {
		String localisation;

		Measure measure = (Measure) data.getContent();
		localisation = measure.getLocalisation();
		return data;
	}

}
