package org.medical.device.manager.util;

import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;

/**
 * This class allows externals to force sending value change notifications.
 * 
 * @author Thomas Leveque
 *
 */
public class ManagedStateVariableImpl extends StateVariableImpl {

	public ManagedStateVariableImpl(String name, Object value, Class type,
			VariableType varType, String description, boolean canBeModified,
			boolean canSendNotif, Object owner) {
		super(name, value, type, varType, description, canBeModified, canSendNotif,
				owner);
	}

	public void sendValueChangeNotifs(Object oldValue) {
		notifyValueChange(oldValue);
	}

	
}
