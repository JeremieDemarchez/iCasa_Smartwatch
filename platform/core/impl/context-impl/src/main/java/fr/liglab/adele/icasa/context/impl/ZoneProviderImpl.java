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
import fr.liglab.adele.icasa.ZoneProvider;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.impl.ZoneImpl;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component(immediate = true)
@Provides
@Instantiate(name = "ZoneProvider-0")
public class ZoneProviderImpl implements ZoneProvider {

	protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);

	@Creator.Field 	 Creator.Entity<ZoneImpl> creator;

	@Override
	public void createZone(String id, int leftX, int topY, int bottomZ, int width, int height, int depth) {
		Map propertiesInit = new HashMap<>();
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.NAME),id);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.X),leftX);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.Y),topY);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.Z),bottomZ);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.X_LENGHT),width);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.Y_LENGHT),height);
		propertiesInit.put(ContextEntity.State.ID(Zone.class,Zone.Z_LENGHT),depth);
		creator.createEntity(id,propertiesInit);
	}


	@Override
	public void removeZone(String id) {
		creator.deleteEntity(id);
	}

	@Override
	public void moveZone(String id, int leftX, int topY, int bottomZ) throws Exception {

	}

	@Override
	public void resizeZone(String id, int width, int height, int depth) throws Exception {

	}

	@Override
	public void removeAllZones() {
		creator.deleteAllEntities();
	}

	@Override
	public Set<String> getZoneIds() {
		return null;
	}


}
