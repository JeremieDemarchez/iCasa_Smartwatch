package fr.liglab.adele.icasa.context.model.example.user.location;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.example.transformation.PhysicalParameterImpl;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Instantiate
public class UserLocationManagerImpl {

    private static final Logger LOG = LoggerFactory.getLogger(UserLocationManagerImpl.class);

    @Requires(id = "user.location.factory",optional = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.user.location.UserLocationImpl)")
    Factory userLocationFactory;

    private final Map<String,ComponentInstance> m_userLocation = new HashMap<>();

    private final Object m_aggregationlock = new Object();


    @Validate
    public void start(){
        createUserLocationAggregation(UserContextEntityImpl.USER_ID);
    }

    @Invalidate
    public void stop(){
        deleteUserLocationAggregation(UserContextEntityImpl.USER_ID);
    }


    @Unbind(id = "user.location.factory")
    public void unbindPhysicalFactory(Factory factory){
        synchronized (m_aggregationlock){
            for (String key : m_userLocation.keySet()){
                try {
                    m_userLocation.get(key).dispose();
                }catch(IllegalStateException e){
                    LOG.error("failed unregistering Physical Parameter" + key, e);
                }
            }
        }
    }

    private void deleteUserLocationAggregation(String user){
        try {
            synchronized (m_aggregationlock) {
                m_userLocation.get(user + "_Location").dispose();
            }
        }catch(IllegalStateException e){
            LOG.error("failed unregistering Presence Physical Parameter in " + user, e);
        }
    }

    private void createUserLocationAggregation(String user){
        Hashtable properties = new Hashtable();
        ComponentInstance instance;

        String m_filter= "(" + PhysicalParameterImpl.PHYSICAL_PARAMETER_NAME + "=*Presence)";

        Hashtable m_requiresFilters = new Hashtable<>();
        m_requiresFilters.put("aggregation.sources",m_filter);

        properties.put("requires.filters", m_requiresFilters);
        properties.put("instance.name", "fr.liglab.adele.icasa.context.model.example.user.location."+user+"_Location");
        properties.put("aggregation.source.filter", m_filter);
        properties.put("user.id", user);
        //properties.put(UserLocationImpl.PARAMETER_NAME+".name",user + "_Location");
        properties.put("context.entity.id",user + "_Location");

        try {
            instance = userLocationFactory.createComponentInstance(properties);
            synchronized (m_aggregationlock){
                m_userLocation.put(user + "_Location",instance);
            }
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

}
