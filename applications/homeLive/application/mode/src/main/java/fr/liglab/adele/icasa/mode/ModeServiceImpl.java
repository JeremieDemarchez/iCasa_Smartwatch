package fr.liglab.adele.icasa.mode;

import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.FormParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

@Component
@Instantiate
@Provides
public class ModeServiceImpl extends DefaultController implements ModeService {

    private  final Logger m_logger = LoggerFactory
            .getLogger(ModeServiceImpl.class);

    private  String currentMode = ModeUtils.HOME;

    @Requires(optional = true)
    ModeListener[] modeListeners;

    private final Object m_lock = new Object();

    public ModeServiceImpl(){

    }

    @Validate
    public void start(){
        m_logger.info("Technical Mode Service STARTING");
    }

    @Invalidate
    public void stop(){
        m_logger.info("Technical Mode Service STOPPING");
    }

    @Override
    public String getCurrentMode() {
        synchronized (m_lock){
            return new String(currentMode);
        }
    }

    @Override
    public void setCurrentMode(String modeName) {
        synchronized (m_lock) {
            String oldMode = getCurrentMode() ;
            if (modeName.equalsIgnoreCase(ModeUtils.HOME)) {
                currentMode = ModeUtils.HOME;
            } else if (modeName.equalsIgnoreCase(ModeUtils.AWAY)) {
                currentMode = ModeUtils.AWAY;
            } else if (modeName.equalsIgnoreCase(ModeUtils.HOLIDAYS)) {
                currentMode = ModeUtils.HOLIDAYS;
            } else if (modeName.equalsIgnoreCase(ModeUtils.NIGHT)) {
                currentMode = ModeUtils.NIGHT;
            } else {
                m_logger.error("Invalid ModeName");
                return;
            }
            notifyListener(oldMode,getCurrentMode());
        }
    }

    private void notifyListener(String newModeName,String oldModeName){
        for (ModeListener listener : modeListeners){
            listener.modeChange(newModeName,oldModeName);
        }
    }

    @Route(method = HttpMethod.POST,uri = "/homelive/mode")
    public Result setMode(@FormParameter("mode") String modeName){
        setCurrentMode(modeName);
        return ok(getCurrentMode()).json();
    }

}
