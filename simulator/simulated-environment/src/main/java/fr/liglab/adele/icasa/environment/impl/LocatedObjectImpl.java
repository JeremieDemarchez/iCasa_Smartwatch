package fr.liglab.adele.icasa.environment.impl;

import fr.liglab.adele.icasa.environment.LocatedObject;
import fr.liglab.adele.icasa.environment.Position;

public class LocatedObjectImpl implements LocatedObject {

	private Position m_position;
	
	public LocatedObjectImpl(Position position) {
		m_position = position;
	}
	
	@Override
   public Position getPosition() {
	   return m_position;
   }

	@Override
   public void setPosition(Position position) {
		m_position = position;
   }

}
