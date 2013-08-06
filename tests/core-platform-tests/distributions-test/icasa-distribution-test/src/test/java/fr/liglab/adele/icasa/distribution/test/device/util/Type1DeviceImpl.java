package fr.liglab.adele.icasa.distribution.test.device.util;

import fr.liglab.adele.icasa.device.util.AbstractDevice;

public class Type1DeviceImpl extends AbstractDevice implements Type1Device {

	private String id;
	
	
	
	public Type1DeviceImpl(String id) {
	   this.id = id;
   }



	public String getSerialNumber() {
	   return id;
   }



}
