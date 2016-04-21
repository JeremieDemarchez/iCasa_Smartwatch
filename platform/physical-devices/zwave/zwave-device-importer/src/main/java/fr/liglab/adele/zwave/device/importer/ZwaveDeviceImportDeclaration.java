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
package fr.liglab.adele.zwave.device.importer;

import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;

import java.util.Map;

/**
 * Created by aygalinc on 20/04/16.
 */
public class ZwaveDeviceImportDeclaration {

    public final static String DEVICE_MANUFACTURER = "zwave.device.manufacturer.id";

    public final static String DEVICE_ID = "zwave.device.id";

    public final static String DEVICE_TYPE = "zwave.device.type.id";

    public final static String ZWAVE_ID = "zwave.node.id";

    private final int zwaveNodeId;

    private final int zwaveDeviceId;

    private final int zwaveDeviceType;

    private final int zwaveManufacturerId;

    public ZwaveDeviceImportDeclaration(ImportDeclaration declaration){
        Map<String,Object> metadatas = declaration.getMetadata();
        zwaveNodeId = (int)metadatas.get(ZWAVE_ID);
        zwaveDeviceId = (int)metadatas.get(DEVICE_ID);
        zwaveDeviceType = (int)metadatas.get(DEVICE_TYPE);
        zwaveManufacturerId = (int)metadatas.get(DEVICE_MANUFACTURER);
    }

    public int getZwaveNodeId() {
        return zwaveNodeId;
    }

    public int getZwaveDeviceId() {
        return zwaveDeviceId;
    }

    public int getzwaveDeviceType() {
        return zwaveDeviceType;
    }

    public int getZwaveManufacturerId() {
        return zwaveManufacturerId;
    }

}
