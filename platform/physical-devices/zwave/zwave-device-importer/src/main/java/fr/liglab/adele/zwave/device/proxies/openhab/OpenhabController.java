package fr.liglab.adele.zwave.device.proxies.openhab;

import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;
import fr.liglab.adele.zwave.device.api.ZwaveController;


@ContextService
public interface OpenhabController extends ZwaveController {

	
    public void addEventListener(ZWaveEventListener eventListener);

    public void removeEventListener(ZWaveEventListener eventListener);

}
