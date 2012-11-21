package fr.liglab.adele.icasa.environment.impl;

import fr.liglab.adele.icasa.environment.Device;
import fr.liglab.adele.icasa.environment.Position;

public class DeviceImpl extends LocatedObjectImpl implements Device {

	private String m_serialNumber;
	
	public DeviceImpl(String serialNumber, Position position) {
	   super(position);
	   m_serialNumber = serialNumber;
   }

	@Override
   public String getSerialNumber() {
	   return m_serialNumber;
   }
	
}
