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
package fr.liglab.adele.zwave.device.proxyes;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.zwave.device.api.ZwaveControllerICasa;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.api.ZwaveRepeater;
import fr.liglab.adele.zwave.device.importer.ZwaveDeviceImportDeclaration;
import org.apache.felix.ipojo.annotations.*;
import org.openhab.binding.zwave.internal.protocol.*;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveWakeUpCommandClass;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveCommandClassValueEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveNodeInfoEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveTransactionCompletedEvent;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryIntrospection;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclarationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;

@ContextEntity(services = {ZwaveControllerICasa.class,ZwaveDevice.class, ZwaveRepeater.class})
@Provides(specifications = {DiscoveryService.class, DiscoveryIntrospection.class})
public class ZwaveControllerICasaImpl extends AbstractDiscoveryComponent implements ZwaveRepeater,ZwaveDevice,ZwaveControllerICasa{

    private static final Logger LOG = LoggerFactory.getLogger(ZwaveControllerICasaImpl.class);

    private ControllerManager manager;

    @Creator.Field("isZwaveNeighbor") 	Creator.Relation<ZwaveDevice,ZwaveDevice> neighborsRelationCreator;

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.SERIAL_PORT,directAccess = true)
    private String serialPort;

    @ContextEntity.State.Field(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.MASTER)
    private boolean master;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_ID)
    private int zwaveId;

    /**
     * Constructor
     */
    protected ZwaveControllerICasaImpl(BundleContext bundleContext) {
        super(bundleContext);
    }

    /**
     *
     * SERVICES
     */
    @Override
    public boolean isMaster() {
        return master;
    }

    @Override
    public synchronized void addEventListener(ZWaveEventListener eventListener) {
        manager.getController().addEventListener(eventListener);
    }

    @Override
    public synchronized void removeEventListener(ZWaveEventListener eventListener) {
        manager.getController().removeEventListener(eventListener);
    }

    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getZwaveId() {
        return zwaveId;
    }

    /**
     * LifeCycle
     */
    @Validate
    protected synchronized void start() {
        super.start();
        try {
            manager	= new ControllerManager(serialPort,this);
            manager.open();
        } catch (SerialInterfaceException e) {
            LOG.error(e.toString());
        }
    }

    @Invalidate
    protected synchronized void stop() {
        super.stop();
        manager.close();
    }

    /**
     * SYNCHRO
     */

    @ContextEntity.State.Pull(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.MASTER)
    Supplier<Boolean> pullMaster=()-> manager.controller.isMasterController();

    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_ID)
    Supplier<Integer> pullId=()-> manager.getManagerZwaveId();

    /**
     * Neighbors Synchro
     */
    @ContextEntity.Relation.Field(value = "isZwaveNeighbor",owner = ZwaveDevice.class)
    @Requires(id="zwavesNeighbors",specification=ZwaveDevice.class,optional=true)
    private List<ZwaveDevice> zwaveDevices;

    @Bind(id = "zwavesNeighbors")
    public void bindZDevice(ZwaveDevice device){
        pushNeighbors();
    }

    @Unbind(id= "zwavesNeighbors")
    public void unbindZDevice(ZwaveDevice device){
        pushNeighbors();
    }

    @ContextEntity.State.Push(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    public List<Integer> pushNeighbors() {
        List<Integer> neighbors = new ArrayList<>();
        for (ZwaveDevice device : zwaveDevices){
            neighbors.add(device.getZwaveId());
        }
        return neighbors;
    }

    @Override
    public String getName() {
        return "Zwave Device Discovery";
    }

    /**
     * This class handles the import for a given controller
     */
    private class ControllerManager implements ZWaveEventListener  {

        private final String			serialPort;

        private final ZWaveController 	controller;

        private final ZwaveControllerICasaImpl 	parent;

        private Map<EndPointIdentifier,String> proxiesWaiting = new ConcurrentHashMap<>();

        private Map<EndPointIdentifier,String> proxies = new ConcurrentHashMap<>();

        private Set<String> relationProxies = new ConcurrentSkipListSet<>();

        public ControllerManager(String serialPort,ZwaveControllerICasaImpl parent) throws SerialInterfaceException {

            this.serialPort		= serialPort;
            this.controller		= new ZWaveController(true,false,serialPort,10000,false);
            this.parent = parent;

            /**
             * Put the controller in the zwaveDevice Managed list
             */
            proxies.put(new EndPointIdentifier(serialPort,getManagerZwaveId()),"");
        }

        public void open() {
            controller.initialize();
            controller.addEventListener(this);
        }

        public void close() {
            controller.close();
            controller.removeEventListener(this);
        }

        public Integer getManagerZwaveId(){
            if (controller.getOwnNodeId() == 0){
                return 1;
            }
            return controller.getOwnNodeId();
        }

        public ZWaveController getController(){
            return controller;
        }

        @Override
        public void ZWaveIncomingEvent(ZWaveEvent event) {
            LOG.debug("Zwave event from node "+event.getNodeId()+" on port "+serialPort);

            EndPointIdentifier endPoint = new EndPointIdentifier(serialPort, event.getNodeId());

            boolean discovered 		=  false;	/*( (event instanceof ZWaveNodeStatusEvent) && (! ((ZWaveNodeStatusEvent)event).getState().equals(ZWaveNodeState.ALIVE)) ) ||
                    ( (event instanceof ZWaveInclusionEvent) && (! ((ZWaveInclusionEvent)event).getEvent().equals(ZWaveInclusionEvent.Type.IncludeDone)) ) ||
                    ( (event instanceof ZWaveWakeUpCommandClass.ZWaveWakeUpEvent)) ||
                    ( (event instanceof ZWaveCommandClassValueEvent));*/

           /*( (event instanceof ZWaveNodeStatusEvent) && (! ((ZWaveNodeStatusEvent)event).getState().equals(ZWaveNodeState.ALIVE)) ) ||
                    ( (event instanceof ZWaveInclusionEvent) && (! ((ZWaveInclusionEvent)event).getEvent().equals(ZWaveInclusionEvent.Type.ExcludeDone)) ) ;*/

            LOG.info("Event : " + event.getClass());
            LOG.info("EndPoint : " + event.getEndpoint());
            LOG.info("NodeId : " + event.getNodeId());
            ZWaveNode node = controller.getNode(event.getNodeId());
            if (node == null ) {
                if (isManaged(endPoint)){
                    removeDeclaration(endPoint);
                }
                return;
            }
            LOG.info("is Dead : " + controller.getNode(event.getNodeId()).isDead());

            LOG.info("is Listening : " + controller.getNode(event.getNodeId()).isListening());

            LOG.info("is Routing : " + controller.getNode(event.getNodeId()).isRouting());

            LOG.info("State : " + controller.getNode(event.getNodeId()).getNodeState());

            LOG.info("Name : " + controller.getNode(event.getNodeId()).getName());

            if (event instanceof ZWaveCommandClassValueEvent){
                discovered = node.getNodeState().equals(ZWaveNodeState.ALIVE);
            }

            if (event instanceof ZWaveNodeInfoEvent){
                discovered = node.getNodeState().equals(ZWaveNodeState.ALIVE);
            }

            if (event instanceof ZWaveWakeUpCommandClass.ZWaveWakeUpEvent){
                ZWaveWakeUpCommandClass.ZWaveWakeUpEvent wakeUpEvent = (ZWaveWakeUpCommandClass.ZWaveWakeUpEvent) event;
                LOG.info("Wke up Info" + wakeUpEvent.getEvent());
                discovered = node.getNodeState().equals(ZWaveNodeState.ALIVE);
            }


            if (event instanceof ZWaveTransactionCompletedEvent){
               if (proxiesWaiting.containsKey(endPoint)){
                   discovered = node.getNodeState().equals(ZWaveNodeState.ALIVE);
               }
            }

            boolean undiscovered 	= node.getNodeState().equals(ZWaveNodeState.DEAD) || node.getNodeState().equals(ZWaveNodeState.FAILED);

            if (discovered && ! isManaged(endPoint) ) {
                createDeclaration(endPoint);
            }

            if (undiscovered && isManaged(endPoint) ) {
                removeDeclaration(endPoint);
            }



            /**
             * Maybe can be compute in a less intensive way ...
             */
            if (discovered) {
                computeRelation();
            }
        }

        private void computeRelation(){
            /**
             * Relation Management
             */
            for (ZWaveNode node : controller.getNodes()){
                EndPointIdentifier nodeEndpoint = new EndPointIdentifier(serialPort, node.getNodeId());
                List<Integer> neighbors = node.getNeighbors();
                if (neighbors == null || !isManaged(nodeEndpoint)){
                    continue;
                }
                for (Integer neighbor : neighbors){
                    EndPointIdentifier endPointNeighbor = new EndPointIdentifier(serialPort, neighbor);

                    ZWaveNode neighborNode = controller.getNode(neighbor);
                    if (( neighborNode == null ) || !isManaged(endPointNeighbor)){
                        continue;
                    }
                    if (!isRelationManaged("ZwaveDevice#"+neighbor+"ZwaveDevice#"+node.getNodeId())) {
                        neighborsRelationCreator.create("ZwaveDevice#" + neighbor, "ZwaveDevice#" + node.getNodeId());
                        relationProxies.add("ZwaveDevice#" + neighbor + "ZwaveDevice#" + node.getNodeId());
                    }
                }
            }
        }

        private final boolean isManaged(EndPointIdentifier endPoint) {
            return proxies.containsKey(endPoint);
        }

        private final boolean isRelationManaged(String id) {
            return relationProxies.contains(id);
        }

        private final void createDeclaration(EndPointIdentifier endPoint) {

    		/*
    		 * verify node is defined in the controller
    		 */
            ZWaveNode node = controller.getNode(endPoint.nodeId);
            if (node == null) {
                LOG.error("Error creating proxy, unknown node "+endPoint.nodeId);
                return;
            }

    		/*
    		 * verify end-point is defined in the controller
    		 */
    //        if (endPoint.endPointId != 0) {
/**
 ZWaveMultiInstanceCommandClass multiInstanceCommandClass = (ZWaveMultiInstanceCommandClass) node.getCommandClass(ZWaveCommandClass.CommandClass.MULTI_INSTANCE);
 if (multiInstanceCommandClass == null) {
 LOG.error("Error creating proxy, unknown endpoint "+endPoint.endPointId+" for node "+endPoint.nodeId);
 return;
 }
 else if (multiInstanceCommandClass.getVersion() == 2 &&  multiInstanceCommandClass.getEndpoint(endPoint.endPointId) == null) {
 LOG.error("Error creating proxy, unknown endpoint "+endPoint.endPointId+" for node "+endPoint.nodeId);
 return;
 }**/
     //           LOG.warn("Endpoint != 0");
          //  }

            if (node.getManufacturer() == Integer.MAX_VALUE ||node.getDeviceId()==Integer.MAX_VALUE||node.getDeviceType() == Integer.MAX_VALUE ){
                LOG.warn("Node " + node.getNodeId() + " need to be wake up in order to provide enought information to be handle by an iCasa Proxy ! ");
                if (!proxiesWaiting.containsKey(endPoint)){
                    controller.requestNodeInfo(node.getNodeId());
                    proxiesWaiting.put(endPoint,"");
                }
                return;
            }

            ImportDeclaration zigbeeDeclaration = ImportDeclarationBuilder.empty()
                    .key(ZwaveDeviceImportDeclaration.DEVICE_ID).value(node.getDeviceId())
                    .key(ZwaveDeviceImportDeclaration.ZWAVE_ID).value(node.getNodeId())
                    .key(ZwaveDeviceImportDeclaration.DEVICE_MANUFACTURER).value(node.getManufacturer())
                    .key(ZwaveDeviceImportDeclaration.DEVICE_TYPE).value(node.getDeviceType())
                    .key("scope").value("generic")
                    .build();
            proxies.put(endPoint,"");
            proxiesWaiting.remove(endPoint);
            parent.registerZwaveDeviceImportDeclaration(endPoint,zigbeeDeclaration);

        }

        private final void removeDeclaration(EndPointIdentifier endPoint) {
            proxies.remove(endPoint);
            parent.unregisterZwaveDeviceImportDeclaration(endPoint);
        }

    }

    /**
     * A class to represent a uniquely identify an end-point of a z-wave node
     */
    private class EndPointIdentifier {

        public final String 	serialPort;
        public final int 		nodeId;

        private final int 		hash;
        private final String	label;

        public EndPointIdentifier(String serialPort, int nodeId) {

            this.serialPort 	= serialPort;
            this.nodeId			= nodeId;

            hash 				= hash();
            label				= label();
        }

        private int hash() {
            int hash = 0;

            hash = (hash ^ (serialPort.hashCode() >>> 32));
            hash = (hash ^ (nodeId >>> 32));

            return hash;
        }

        private String label() {
            return "node "+nodeId + " on port "+serialPort;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {

            if (! (obj instanceof EndPointIdentifier) )
                return false;

            EndPointIdentifier that = ((EndPointIdentifier)obj);

            return 	this.serialPort.equals(that.serialPort) &&
                    this.nodeId == that.nodeId;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private Map<EndPointIdentifier,ImportDeclaration> declarationMap = new ConcurrentHashMap<>();
    private void registerZwaveDeviceImportDeclaration(EndPointIdentifier id,ImportDeclaration declaration){
        super.registerImportDeclaration(declaration);
        declarationMap.put(id,declaration);
    }

    private void unregisterZwaveDeviceImportDeclaration(EndPointIdentifier id){
        ImportDeclaration declaration = declarationMap.remove(id);
        if (declaration != null){
            super.unregisterImportDeclaration(declaration);
        }
    }
}




