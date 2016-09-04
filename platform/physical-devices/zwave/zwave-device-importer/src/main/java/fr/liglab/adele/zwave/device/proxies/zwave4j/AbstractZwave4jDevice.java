package fr.liglab.adele.zwave.device.proxies.zwave4j;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.ValueId;

public abstract class AbstractZwave4jDevice implements Zwave4jDevice {

	
    protected abstract int getHomeId();

    protected abstract int getNodeId();

	protected abstract void valueChanged(Manager manager, ValueId valueId);

	protected abstract void nodeStatusChanged(Manager manager, short status);
    
	@Override
	public void notification(Manager manager, Notification notification) {
		switch (notification.getType()) {
		case NOTIFICATION:
			nodeStatusChanged(manager,notification.getByte());
			break;
		case VALUE_CHANGED:
			valueChanged(manager,notification.getValueId());
			break;
		default:
			break;
		}
		
	}

	protected boolean isActive(Manager manager) {
		
		boolean listening 	= manager.isNodeListeningDevice(getHomeId(),(short) getNodeId());
		boolean awake		= manager.isNodeAwake(getHomeId(),(short) getNodeId());
		boolean failed		= manager.isNodeFailed(getHomeId(),(short) getNodeId());
		
		return (listening && ! failed) || ( !listening && awake); 
	}

}