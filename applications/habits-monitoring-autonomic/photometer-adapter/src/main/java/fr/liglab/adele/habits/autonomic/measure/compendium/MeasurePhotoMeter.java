package fr.liglab.adele.habits.autonomic.measure.compendium;

import fr.liglab.adele.habits.monitoring.measure.generator.Measure;

public class MeasurePhotoMeter extends Measure {
	private static final long serialVersionUID = -5369097163595442220L;
	private float sensedIlluminance;

	public void setIlluminance(float illuminance) {
		this.sensedIlluminance = illuminance;
	}

	public float getIlluminance() {
		return this.sensedIlluminance;
	}
}
