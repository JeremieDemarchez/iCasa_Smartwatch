package fr.liglab.adele.zwave.device.proxies.zwave4j;

import java.util.concurrent.atomic.AtomicReference;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.ValueId;

public abstract class AbstractZwave4jDevice implements Zwave4jDevice {


	protected abstract void valueChanged(Manager manager, ValueId valueId);

	protected abstract void nodeStatusChanged(Manager manager, short status);

	@Override
	public void notification(Manager manager, Notification notification) {
		switch (notification.getType()) {
		case NOTIFICATION:
			nodeStatusChanged(manager, notification.getByte());
			break;
		case VALUE_CHANGED:
			valueChanged(manager, notification.getValueId());
			break;
		default:
			break;
		}

	}

	protected final Object getValue(Manager manager, ValueId valueId) {
		switch (valueId.getType()) {
		case BOOL:
			AtomicReference<Boolean> b = new AtomicReference<>();
			manager.getValueAsBool(valueId, b);
			return b.get();
		case BYTE:
			AtomicReference<Short> bb = new AtomicReference<>();
			manager.getValueAsByte(valueId, bb);
			return bb.get();
		case DECIMAL:
			AtomicReference<Float> f = new AtomicReference<>();
			manager.getValueAsFloat(valueId, f);
			return f.get();
		case INT:
			AtomicReference<Integer> i = new AtomicReference<>();
			manager.getValueAsInt(valueId, i);
			return i.get();
		case LIST:
			return null;
		case SCHEDULE:
			return null;
		case SHORT:
			AtomicReference<Short> s = new AtomicReference<>();
			manager.getValueAsShort(valueId, s);
			return s.get();
		case STRING:
			AtomicReference<String> ss = new AtomicReference<>();
			manager.getValueAsString(valueId, ss);
			return ss.get();
		case BUTTON:
			return null;
		case RAW:
			AtomicReference<short[]> sss = new AtomicReference<>();
			manager.getValueAsRaw(valueId, sss);
			return sss.get();
		default:
			return null;
		}
	}

}