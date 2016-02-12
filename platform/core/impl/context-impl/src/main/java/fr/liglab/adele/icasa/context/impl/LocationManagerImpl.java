/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.context.impl;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.impl.ZoneImpl;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component(immediate = true,publicFactory=false)
@Instantiate(name = "LocationManagerImpl-0")
public class LocationManagerImpl{

    protected static Logger LOG = LoggerFactory.getLogger(Constants.ICASA_LOG);

    @Requires(id = "zones",specification = Zone.class,optional = true)
    List<Zone> zones;

    @Requires(id = "locatedObjects",specification = LocatedObject.class,optional = true)
    List<LocatedObject> locatedObjects;

    @Creator.Field(ZoneImpl.RELATION_CONTAINS) 	Creator.Relation<ZoneImpl,LocatedObject> containsCreator;

    @Bind(id = "zones")
    public synchronized void bindZone(Zone zone){

    }

    @Modified(id = "zones")
    public synchronized void modifiedZone(Zone zone){

    }

    @Unbind(id = "zones")
    public synchronized void unbindZone(Zone zone){

    }


    @Bind(id = "locatedObjects")
    public synchronized void bindLocatedObject(LocatedObject object, Map<String,Object> properties){
        for (Zone zone : zones) {
            if (! zone.canContains(object.getPosition())) {
                continue;
            }
            LOG.info("create");
            containsCreator.create(zone.getZoneName(),(String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
        }
    }

    @Modified(id = "locatedObjects")
    public synchronized void modifiedLocatedObject(LocatedObject object,Map<String,Object> properties){
        for (Zone zone : zones) {
            if (zone.canContains(object.getPosition())) {
                try{
                    LOG.info("create source : " +  zone.getZoneName()  + " and target : " + (String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
                    containsCreator.create(zone.getZoneName(),(String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
                }catch (IllegalArgumentException e){

                }
            }else {
                LOG.info("delete  source : " + zone.getZoneName() + " and target : " +   (String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
                containsCreator.delete(zone.getZoneName(),(String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
            }
        }
    }

    @Unbind(id = "locatedObjects")
    public synchronized void unbindLocatedObject(LocatedObject object,Map<String,Object> properties){
        containsCreator.delete(object.getZone(),(String)properties.get(ContextEntity.CONTEXT_ENTITY_ID));
    }
}
