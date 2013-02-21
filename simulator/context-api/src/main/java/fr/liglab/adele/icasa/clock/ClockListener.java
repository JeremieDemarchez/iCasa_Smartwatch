package fr.liglab.adele.icasa.clock;

public interface ClockListener {
	
	void factorModified(int oldFactor);
	void startDateModified(long oldStartDate);
	void clockPaused();
	void clockResumed();
	void clockReset();

}
