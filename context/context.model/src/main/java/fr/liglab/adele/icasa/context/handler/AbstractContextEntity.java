package fr.liglab.adele.icasa.context.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractContextEntity  implements fr.liglab.adele.icasa.context.model.ContextEntity{

    abstract public String getId() ;

    @Override
    abstract public List<Object> getStateValue(String property);

    @Override
    abstract public List<List<Object>> getState();

    @Override
    abstract public void setState(String state, Object value) ;

    @Override
    abstract public Map<String, Object> getStateAsMap() ;

    @Override
    public List<Object> getStateExtensionValue(String property) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return new HashMap<String,Object>();
    }
}
