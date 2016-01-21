package fr.liglab.adele.icasa.context.model.example.zone;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.context.handler.creator.entity.EntityCreator;
import fr.liglab.adele.icasa.context.handler.creator.entity._EntityCreator;
import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreator;
import fr.liglab.adele.icasa.context.handler.creator.relation._RelationCreator;
import fr.liglab.adele.icasa.context.model.RelationImpl;
import fr.liglab.adele.icasa.context.model.RelationType;
import fr.liglab.adele.icasa.context.model.example.Relation_Contained;
import fr.liglab.adele.icasa.context.model.example.Relation_IsContained;
import fr.liglab.adele.icasa.context.model.example.device.DeviceDiscovery;
import fr.liglab.adele.icasa.location.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Instantiate
public class ZoneDiscovery implements ZoneListener,LocatedDeviceListener {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscovery.class);

    private final Map<String,ServiceRegistration> zoneEntities = new HashMap<>();

    private final RelationType relation_isContained = new Relation_IsContained();
    private final RelationType relation_contained = new Relation_Contained();

    @EntityCreator(entity=ZoneContextEntityImpl.class)
    private _EntityCreator m_entityCreator;

    @RelationCreator(relation=RelationImpl.class)
    private _RelationCreator m_relationCreator;

    @Requires(id = "context.manager")
    private ContextManager m_manager;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Bind(id = "context.manager", optional = true, aggregate = false)
    public void bindContextManager(ContextManager contextManager){
        m_manager.addListener(this);
        /*TODO zone added before*/
    }

    @Unbind(id = "context.manager")
    public void unbindContextManager(ContextManager contextManager){
        m_manager.removeListener(this);
    }

    @Override
    public void zoneAdded(Zone zone) {
        m_entityCreator.createEntity(zone.getId());
    }

    @Override
    public void zoneRemoved(Zone zone) {
        m_entityCreator.deleteEntity(zone.getId());
    }

    @Override
    public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {

    }

    @Override
    public void zoneResized(Zone zone) {

    }

    @Override
    public void zoneParentModified(Zone zone, Zone oldParentZone, Zone newParentZone) {

    }

    @Override
    public void deviceAttached(Zone container, LocatedDevice child) {

    }

    @Override
    public void deviceDetached(Zone container, LocatedDevice child) {

    }

    @Override
    public void zoneVariableAdded(Zone zone, String variableName) {

    }

    @Override
    public void zoneVariableRemoved(Zone zone, String variableName) {

    }

    @Override
    public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {

    }

    @Override
    public void deviceAdded(LocatedDevice device) {

    }

    @Override
    public void deviceRemoved(LocatedDevice device) {

    }

    @Override
    public void deviceMoved(LocatedDevice device, Position oldPosition, Position newPosition) {
        String deviceID = device.getSerialNumber();
        String oldZoneID;
        String newZoneID;
        UUID uuid;

        if (getZones(oldPosition).isEmpty()){
            for (Zone zone : getZones(newPosition)){
                LOG.info(" Discovery create relation");

                newZoneID = zone.getId();
                m_relationCreator.createRelation(relation_isContained, deviceID, newZoneID);
                m_relationCreator.createRelation(relation_contained, newZoneID, deviceID);
            }
        } else {
//            boolean delete = getZones(newPosition).isEmpty();
//            for (Zone oldZone : getZones(oldPosition)){
//                oldZoneID = oldZone.getId();
//                if (delete){
//                    LOG.info(" Discovery delete relation");
//                    uuid = m_relationCreator.findId(relation_isContained.getName(), deviceID, oldZoneID);
//                    if (uuid != null) {
//                        m_relationCreator.deleteRelation(uuid);
//                    }
//                    uuid = m_relationCreator.findId(relation_contained.getName(), oldZoneID, deviceID);
//                    if (uuid != null) {
//                        m_relationCreator.deleteRelation(uuid);
//                    }
//                }else {
//                    for (Zone newZone : getZones(newPosition)) {
//                        LOG.info(" Discovery update relation");
//                        newZoneID = newZone.getId();
//                        uuid = m_relationCreator.findId(relation_isContained.getName(), deviceID, oldZoneID);
//                        if (uuid != null) {
//                            m_relationCreator.updateRelation(uuid, deviceID, newZoneID);
//                        }
//                        if (uuid != null) {
//                            uuid = m_relationCreator.findId(relation_contained.getName(), oldZoneID, deviceID);
//                        }
//                        m_relationCreator.updateRelation(uuid, newZoneID, deviceID);
//                    }
//                }
//            }
        }
    }

    private Set<Zone> getZones(Position devicePosition) {
        List<Zone> zones = m_manager.getZones();
        Set<Zone> zonesToUpdate = new HashSet<Zone>();
        for (Zone zone : zones) {
            if (zone.contains(devicePosition))
                zonesToUpdate.add(zone);
        }
        return zonesToUpdate;
    }

    @Override
    public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue, Object newValue) {

    }

    @Override
    public void devicePropertyAdded(LocatedDevice device, String propertyName) {

    }

    @Override
    public void devicePropertyRemoved(LocatedDevice device, String propertyName) {

    }

    @Override
    public void deviceAttached(LocatedDevice container, LocatedDevice child) {

    }

    @Override
    public void deviceDetached(LocatedDevice container, LocatedDevice child) {

    }

    @Override
    public void deviceEvent(LocatedDevice device, Object data) {

    }
}
