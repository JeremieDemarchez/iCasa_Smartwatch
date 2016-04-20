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

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.zwave.device.api.ZwaveControllerICasa;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.proxyes.*;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.openhab.binding.zwave.internal.protocol.*;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveCommandClass.CommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMultiInstanceCommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveWakeUpCommandClass.ZWaveWakeUpEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveCommandClassValueEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveInclusionEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveNodeStatusEvent;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.component.ImporterIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


@Component
@Provides(specifications = {ImporterService.class,ImporterIntrospection.class})
public class ZWaveImporter extends AbstractImporterComponent  {

	private static final Logger LOG = LoggerFactory.getLogger(ZWaveImporter.class);

	private final BundleContext context;

	private static final Map<String, ControllerManager> managers = new ConcurrentHashMap<String, ControllerManager>();

	@Creator.Field Creator.Entity<ZwaveControllerICasaImpl> controllerCreator;

	@Creator.Field Creator.Entity<FibaroMotionSensor> motionSensorCreator;

	@Creator.Field Creator.Entity<FibaroWallPlug> wallPlugCreator;

	@Creator.Field Creator.Entity<FibaroSmokeSensor> smokeSensorCreator;

	@Creator.Field Creator.Entity<FibaroDoorWindowSensor> doorWindowSensorCreator;
	@Creator.Field("isZwaveNeighbor") 	Creator.Relation<ZwaveDevice,ZwaveDevice> neighborsRelationCreator;


	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	@ServiceProperty(name = "target", value = "(&(scope=generic)(protocol=zwave)(port=*))")
	private String filter;

	/**
	 * The properties used to match the proxy factory
	 */
	public final static String FACTORY_PROPERTY_MANUFACTURER 	= "zwave.manufacturer";
	public final static String FACTORY_PROPERTY_DEVICE_ID 		= "zwave.device.id";
	public final static String FACTORY_PROPERTY_DEFAULT_PROXY	= "zwave.default.proxy";

	/**
	 *
	 */
	public final static String PROXY_PROPERTY_CONTROLLER 		= "zwave.controller";
	public final static String PROXY_PROPERTY_NODE 				= "zwave.node";
	public final static String PROXY_PROPERTY_ENDPOINT 			= "zwave.endpoint";


	public ZWaveImporter(BundleContext context) {
		this.context = context;
	}

	@Override
	protected void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		ZWaveDeclaration declaration = ZWaveDeclaration.from(importDeclaration);

		LOG.info("Importing declaration for zwave device '{}' at port {}",declaration.getId(),declaration.getPort());

		ControllerManager manager = managers.get(declaration.getPort());

		if(manager == null) {
			try {
				manager	= new ControllerManager(declaration.getPort());
				managers.put(declaration.getPort(),manager);
				manager.open();
				Map<String,Object> properties= new HashMap<>();
				properties.put(ContextEntity.State.ID(ZwaveControllerICasa.class,ZwaveControllerICasa.CONTROLLER),manager.getController());
				properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.ZWAVE_ID),manager.getManagerZwaveId());
				properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.ZWAVE_NEIGHBORS),new ArrayList<>());

				controllerCreator.create("ZwaveDevice#"+manager.getManagerZwaveId(),properties);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		handleImportDeclaration(importDeclaration);

	}

	@Override
	protected void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {

		ZWaveDeclaration declaration = ZWaveDeclaration.from(importDeclaration);

		LOG.info("Removing imported declaration for zwave device '{}' at port {}",declaration.getId(),declaration.getPort());

		ControllerManager manager	= managers.remove(declaration.getPort());

		if (manager != null) {
			manager.close();
		}

		unhandleImportDeclaration(importDeclaration);
	}


	public String getName() {
		return name;
	}


	/**
	 * This class handles the import for a given controller
	 */
	private class ControllerManager implements ZWaveEventListener  {

		private final String			serialPort;
		private final ZWaveController 	controller;

		private Map<EndPointIdentifier,String> proxies = new ConcurrentHashMap<>();

		private Set<String> relationProxies = new ConcurrentSkipListSet<>();
		public ControllerManager(String serialPort) throws SerialInterfaceException {

			this.serialPort		= serialPort;
			this.controller		= new ZWaveController(true,false,serialPort,10000,false);
			/**
			 * Put the controller in the zwaveDevice Managed list
			 */
			proxies.put(new EndPointIdentifier(serialPort,getManagerZwaveId(),0),"ZwaveDevice#"+getManagerZwaveId());
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

			EndPointIdentifier endPoint = new EndPointIdentifier(serialPort, event.getNodeId(), event.getEndpoint());

			boolean discovered 		= 	( (event instanceof ZWaveNodeStatusEvent) && (! ((ZWaveNodeStatusEvent)event).getState().equals(ZWaveNodeState.ALIVE)) ) ||
					( (event instanceof ZWaveInclusionEvent) && (! ((ZWaveInclusionEvent)event).getEvent().equals(ZWaveInclusionEvent.Type.IncludeDone)) ) ||
					( (event instanceof ZWaveWakeUpEvent)) ||
					( (event instanceof ZWaveCommandClassValueEvent));

			boolean undiscovered 	= 	( (event instanceof ZWaveNodeStatusEvent) && (! ((ZWaveNodeStatusEvent)event).getState().equals(ZWaveNodeState.ALIVE)) ) ||
					( (event instanceof ZWaveInclusionEvent) && (! ((ZWaveInclusionEvent)event).getEvent().equals(ZWaveInclusionEvent.Type.ExcludeDone)) ) ;

			if (discovered && ! isManaged(endPoint) ) {
				createProxy(endPoint);
			}

			if (undiscovered && isManaged(endPoint) ) {
				destroyProxy(endPoint);
			}

			/**
			 * Relation Management
			 */
			for (ZWaveNode node : controller.getNodes()){
				EndPointIdentifier nodeEndpoint = new EndPointIdentifier(serialPort, node.getNodeId(),0);
				List<Integer> neighbors = node.getNeighbors();
				if (neighbors == null || !isManaged(nodeEndpoint)){
					continue;
				}
				for (Integer neighbor : neighbors){
					EndPointIdentifier endPointNeighbor = new EndPointIdentifier(serialPort, neighbor,0);

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

		private final void createProxy(EndPointIdentifier endPoint) {
    		
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
			if (endPoint.endPointId != 0) {

				ZWaveMultiInstanceCommandClass multiInstanceCommandClass = (ZWaveMultiInstanceCommandClass) node.getCommandClass(CommandClass.MULTI_INSTANCE);
				if (multiInstanceCommandClass == null) {
					LOG.error("Error creating proxy, unknown endpoint "+endPoint.endPointId+" for node "+endPoint.nodeId);
					return;
				}
				else if (multiInstanceCommandClass.getVersion() == 2 &&  multiInstanceCommandClass.getEndpoint(endPoint.endPointId) == null) {
					LOG.error("Error creating proxy, unknown endpoint "+endPoint.endPointId+" for node "+endPoint.nodeId);
					return;
				}
			}
    		
    		/*
    		 * Look for matching proxy
    		 */
			Creator.Entity creator = getCreator(node);

			if (creator == null){
				LOG.warn("Zwave device"+ node.getNodeId() +" not support by iCasa");
				return;
			}

			LOG.info(" Try to create ZWave Device");
			Map<String,Object> properties = new HashMap<>();
			properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.ZWAVE_ID),node.getNodeId());
			properties.put(ContextEntity.State.ID(ZwaveDevice.class,ZwaveDevice.ZWAVE_NEIGHBORS),new ArrayList<>());
			properties.put(ContextEntity.State.ID(GenericDevice.class,GenericDevice.DEVICE_SERIAL_NUMBER),"ZwaveDevice#"+node.getNodeId());

			creator.create("ZwaveDevice#"+node.getNodeId(),properties);
			proxies.put(endPoint,"ZwaveDevice#"+node.getNodeId());
			/**	for (ServiceReference<Factory> factoryDescription : factoryDescriptions) {
			 try {

			 String manufacterer 	= (String) factoryDescription.getProperty(FACTORY_PROPERTY_MANUFACTURER);
			 String deviceId 		= (String) factoryDescription.getProperty(FACTORY_PROPERTY_DEVICE_ID);
			 String defaultProxy		= (String) factoryDescription.getProperty(FACTORY_PROPERTY_DEFAULT_PROXY);

			 boolean match 			= ( manufacterer != null && Integer.valueOf(manufacterer,16).intValue() == node.getManufacturer()) &&
			 ( deviceId != null && Integer.valueOf(deviceId,16).intValue() == node.getDeviceId());

			 boolean isDefault		= ( defaultProxy != null && Boolean.valueOf(defaultProxy).booleanValue() && node.getManufacturer() == Integer.MAX_VALUE);

			 if (match || isDefault) {
			 LOG.debug("Zwave creating proxy for end point "+endPoint);

			 String id = "ZWaveDevice["+node.getHomeId()+"."+endPoint.nodeId+"."+endPoint.endPointId+"]";

			 Hashtable<String, Object> configuration = new Hashtable<String, Object>();

			 configuration.put( GenericDevice.DEVICE_SERIAL_NUMBER, id);

			 configuration.put(PROXY_PROPERTY_CONTROLLER, controller);
			 configuration.put(PROXY_PROPERTY_NODE, endPoint.nodeId);
			 configuration.put(PROXY_PROPERTY_ENDPOINT, endPoint.endPointId);

			 configuration.put(Factory.INSTANCE_NAME_PROPERTY, id);

			 Factory factory = context.getService(factoryDescription);
			 proxies.put(endPoint,factory.createComponentInstance(configuration));
			 }

			 } catch (Exception exception) {
			 LOG.error("Error in Zwave creating proxy for end point "+endPoint,exception);
			 }
			 }**/

		}

		private final void destroyProxy(EndPointIdentifier endPoint) {
			LOG.debug("Zwave destroying proxy for end point "+endPoint);
			String proxyContextId = proxies.remove(endPoint);
			if (proxyContextId != null) {
				/**
				 * Looking for the appropriate Creator
				 */
				Creator.Entity creator = getCreator(controller.getNode(endPoint.nodeId));

				if (creator == null){
					LOG.error("Unable to destroy iCasa proxy related to Node " + endPoint.nodeId);
					return;
				}
				creator.delete(proxyContextId);
			}
		}

	}

	/**
	 * A class to represent a uniquely identify an end-point of a z-wave node
	 */
	private class EndPointIdentifier {

		public final String 	serialPort;
		public final int 		nodeId;
		public final int 		endPointId;

		private final int 		hash;
		private final String	label;

		public EndPointIdentifier(String serialPort, int nodeId, int endPointId) {

			this.serialPort 	= serialPort;
			this.nodeId			= nodeId;
			this.endPointId		= endPointId;

			hash 				= hash();
			label				= label();
		}

		private int hash() {
			int hash = 0;

			hash = (hash ^ (serialPort.hashCode() >>> 32));
			hash = (hash ^ (nodeId >>> 32));
			hash = (hash ^ (endPointId >>> 32));

			return hash;
		}

		private String label() {
			return "node "+nodeId+" end point "+endPointId+ " on port "+serialPort;
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
					this.nodeId == that.nodeId &&
					this.endPointId == that.endPointId;
		}

		@Override
		public String toString() {
			return label;
		}
	}


	private Creator.Entity getCreator(ZWaveNode node){
		if (node.getManufacturer() == Integer.MAX_VALUE){
			LOG.warn("Node " + node.getNodeId() + " has zwave device Type or manufacturer or device ID with default value. iCasa cannot choose an appropriate proxy ! Need a wake up !");
			return null;
		}

		if (isFibaroGFMS001(node)){
			LOG.info("Fibaro Multi Sensor detected");
			return motionSensorCreator;
		}
		else if (isFibaroFGWPE101(node)){
			LOG.info("Fibaro Wall Plug detected");
			return wallPlugCreator;
		}
		else if (isFibaroFGK101(node)){
			LOG.info("Fibaro Door Window Sensor detected");
			return doorWindowSensorCreator;
		}
		else if (isFibaroFGSD002(node)){
			LOG.info("Fibaro Smoke Sensor detected");
			return smokeSensorCreator;
		}
		return null;
	}

	/**
	 * Detect if ZwaveNode is a MultiSensor
	 * @param node
	 * @return
     */
	private boolean isFibaroGFMS001(ZWaveNode node){
		if (node.getManufacturer() != Integer.valueOf("010F",16)){
			return false;
		}
		if (node.getDeviceType() == Integer.valueOf("0800",16)){
			if (node.getDeviceId()==Integer.valueOf("1001",16) || node.getDeviceId()==Integer.valueOf("2001",16) || node.getDeviceId()==Integer.valueOf("3001",16) ){
				return true;
			}
		}else if (node.getDeviceType() == Integer.valueOf("0801",16)){
			if (node.getDeviceId()==Integer.valueOf("1001",16) || node.getDeviceId()==Integer.valueOf("2001",16)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a smart Plug
	 * @param node
     * @return
     */
	private boolean isFibaroFGWPE101(ZWaveNode node){
		if (node.getManufacturer() != Integer.valueOf("010F",16)){
			return false;
		}
		if (node.getDeviceType() == Integer.valueOf("0600",16)) {
			if (node.getDeviceId() == Integer.valueOf("1000", 16)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a Fibaro Door Sensor
	 * @param node
	 * @return
     */
	private boolean isFibaroFGK101(ZWaveNode node){
		if (node.getManufacturer() != Integer.valueOf("010F",16)){
			return false;
		}
		if (node.getDeviceType() == Integer.valueOf("0700",16)) {
			if (node.getDeviceId() == Integer.valueOf("1000", 16) || node.getDeviceId()==Integer.valueOf("2000",16) || node.getDeviceId()==Integer.valueOf("3000",16)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if ZwaveNode is a Fibaro Smoke sensor
	 * @param node
	 * @return
	 */
	private boolean isFibaroFGSD002(ZWaveNode node){
		if (node.getManufacturer() != Integer.valueOf("010F",16)){
			return false;
		}
		if (node.getDeviceType() == Integer.valueOf("0C02",16)) {
			if (node.getDeviceId() == Integer.valueOf("1002", 16)) {
				return true;
			}
		}
		return false;
	}
}
