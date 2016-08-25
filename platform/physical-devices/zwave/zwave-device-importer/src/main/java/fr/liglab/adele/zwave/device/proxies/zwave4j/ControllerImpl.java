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
package fr.liglab.adele.zwave.device.proxies.zwave4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.framework.BundleContext;

import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryIntrospection;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zwave4j.ControllerCallback;
import org.zwave4j.ControllerCommand;
import org.zwave4j.ControllerError;
import org.zwave4j.ControllerState;
import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Notification;
import org.zwave4j.NotificationWatcher;
import org.zwave4j.Options;
import org.zwave4j.ZWave4j;


import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.provider.Creator;
import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;


@ContextEntity(services = { ZwaveController.class, ZwaveDevice.class})
@Provides(specifications = { DiscoveryService.class, DiscoveryIntrospection.class })

public class ControllerImpl extends AbstractDiscoveryComponent implements ZwaveDevice, ZwaveController, NotificationWatcher, ControllerCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerImpl.class);

    /**
     * The zwave4j manager
     */
	private Manager manager;

	/**
	 * The configured serial port
	 */
	@ContextEntity.State.Field(service = ZwaveController.class, state = ZwaveController.SERIAL_PORT, directAccess = true)
	private String serialPort;


	/**
	 * The network identifier of the controller
	 */
	@ContextEntity.State.Field(service = ZwaveDevice.class, state = ZwaveDevice.HOME_ID, directAccess=true)
	private Integer zwaveHomeId;

	@ContextEntity.State.Field(service = ZwaveDevice.class, state = ZwaveDevice.NODE_ID)
	private Integer zwaveNodeId;
	
    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.NODE_ID)
    Supplier<Short> pullNodeId = () -> {
		return zwaveHomeId != -1 ? manager.getControllerNodeId(zwaveHomeId) : -1;
	};

	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.MANUFACTURER_ID)
	private int manufacturerId;

    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.MANUFACTURER_ID)
	Supplier<Integer> pullManufactererId = () -> {
		String value = zwaveHomeId != -1 ? manager.getNodeManufacturerId(zwaveHomeId,zwaveNodeId.shortValue()) : null;
		return value != null ? Integer.parseInt(value,16) : -1;
	};
	
	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_TYPE)
	private int deviceType;

    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_TYPE)
	Supplier<Integer> pullDeviceType = () -> {
		String value = zwaveHomeId != -1 ? manager.getNodeProductType(zwaveHomeId,zwaveNodeId.shortValue()) : null;
		return value != null ? Integer.parseInt(value,16) : -1;
	};
	
	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_ID)
	private int deviceId;
	
    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_ID)
	Supplier<Integer> pullDeviceId =  () -> {
		String value = zwaveHomeId != -1 ? manager.getNodeProductId(zwaveHomeId,zwaveNodeId.shortValue()) : null;
		return value != null ? Integer.parseInt(value,16) : -1;
	};
	

	/**
	 * Whether this is the master controller
	 */
	@ContextEntity.State.Field(service = ZwaveController.class, state = ZwaveController.MASTER)
	private boolean master;

    @ContextEntity.State.Pull(service = ZwaveController.class,state = ZwaveController.MASTER)
    Supplier<Boolean> pullMaster=()-> zwaveHomeId != -1 ? manager.isPrimaryController(zwaveHomeId) : false;
	
	/**
	 * Relation to the neighbor devices
	 */

	@Creator.Field("isZwaveNeighbor")
	Creator.Relation<ZwaveDevice, ZwaveDevice> neighborsRelationCreator;

	/**
	 * The identifiers of neighbor devices
	 */
	@ContextEntity.State.Field(service = ZwaveDevice.class, state = ZwaveDevice.NEIGHBORS)
	private List<Integer> neighbors;

	@ContextEntity.State.Push(service = ZwaveDevice.class, state = ZwaveDevice.NEIGHBORS)
	public List<Integer> pushNeighbors() {
		List<Integer> neighbors = new ArrayList<>();
		for (ZwaveDevice device : neighborDevices) {
			neighbors.add(device.getNodeId());
		}
		return neighbors;
	}

	@ContextEntity.Relation.Field(value = "isZwaveNeighbor", owner = ZwaveDevice.class)
	@Requires(id = "zwavesNeighbors", specification = ZwaveDevice.class, optional = true)
	private List<ZwaveDevice> neighborDevices;

	@Bind(id = "zwavesNeighbors")
	public void bindZDevice(ZwaveDevice device) {
		pushNeighbors();
	}

	@Unbind(id = "zwavesNeighbors")
	public void unbindZDevice(ZwaveDevice device) {
		pushNeighbors();
	}

	/**
	 * The current operation mode
	 */

	@ContextEntity.State.Field(service = ZwaveController.class, state = ZwaveController.MODE)
	private ZwaveController.Mode mode;

    @ContextEntity.State.Apply(service = ZwaveController.class,state = ZwaveController.MODE)
    private Consumer<ZwaveController.Mode> requestChangeMode =  (requestedMode) -> requestChangeMode(requestedMode); 
	
	@ContextEntity.State.Push(service = ZwaveController.class, state = ZwaveController.MODE)
	public ZwaveController.Mode changeModeNotification(ZwaveController.Mode newMode) {
        LOG.debug("Zwave mode changed "+newMode);
		return newMode;
	}
	

	/**
	 * Configuration parameters
	 */
	private final File		configDirectory;
	private final String 	optionsValue;

	
	/**
	 * Constructor
	 */
	protected ControllerImpl(BundleContext bundleContext) {
		super(bundleContext);
		
		optionsValue 		= optional(bundleContext.getProperty("zwave4j.options"),"");
		configDirectory		= optional(bundleContext.getProperty("zwave4j.config.directory"),new File("conf","zwave4j"),File::new);
		
        LOG.debug("Zwave zwave4j : config directory = "+configDirectory+ " options = "+optionsValue);

	}

	private static final <T> T optional(T value, T defaultValue) {
		return value != null ? value : defaultValue;
	}

	private static final <S,T> T optional(S value, T defaultValue, Function<S, T> map) {
		return value != null ? map.apply(value) : defaultValue;
	}
	
	/**
	 *
	 * SERVICES
	 */
	@Override
	public boolean isMaster() {
		return master;
	}

	public ZwaveController.Mode getMode() {
		return mode;
	}

	public void changeMode(ZwaveController.Mode requestedMode) {
		this.mode = requestedMode;
	}

	@Override
	public List<Integer> getNeighbors() {
		return neighbors;
	}

	@Override
	public Integer getHomeId() {
		return (int) zwaveHomeId;
	}

	@Override
	public Integer getNodeId() {
		return zwaveNodeId;
	}

	@Override
	public Integer getManufacturerId() {
		return manufacturerId;
	}

	@Override
	public Integer getDeviceId() {
		return deviceId;
	}

	@Override
	public Integer getDeviceType() {
		return deviceType;
	}

	/**
	 * LifeCycle
	 */
	@Validate
	protected synchronized void start() {
		
        Thread.currentThread().setContextClassLoader(ZWave4j.class.getClassLoader());
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);

        Options options = Options.create(configDirectory.getPath(),configDirectory.getPath(),optionsValue);
        options.addOptionBool("Logging", false);
        options.addOptionBool("ConsoleOutput", false);
        options.lock();

        zwaveHomeId = -1;
        
        manager = Manager.create();
        manager.addWatcher(this, this);
        manager.addDriver(serialPort);

        changeModeNotification(ZwaveController.Mode.NORMAL);
        
		super.start();
	}

	@Invalidate
	protected synchronized void stop() {
		super.stop();
		
		manager.removeWatcher(this, this);
        manager.removeDriver(serialPort);
        Manager.destroy();
        Options.destroy();		
	}

	@Override
	public String getName() {
		return "Zwave4jDeviceDiscovery";
	}

	/**
	 * Handle mode change requests by sending the appropriate command to the controller and tracking
	 * progress of the operation.
	 * 
	 */
    private void requestChangeMode(ZwaveController.Mode requestedMode) {
    	
        LOG.debug("Zwave mode change requested"+requestedMode);

		switch (mode) {

			case NORMAL:
				switch (requestedMode) {
					case EXCLUSION:
						changeModeNotification(requestedMode);
						manager.beginControllerCommand(zwaveHomeId, ControllerCommand.REMOVE_DEVICE,this);
						break;
	
					case INCLUSION:
						changeModeNotification(requestedMode);
						manager.beginControllerCommand(zwaveHomeId, ControllerCommand.ADD_DEVICE, this);
						break;
	
					case NORMAL:
						break;
				}

				break;

			case INCLUSION:
				switch (requestedMode) {
					case NORMAL:
						changeModeNotification(requestedMode);
						manager.cancelControllerCommand(zwaveHomeId);
						break;
					default:
						break;
				}

				break;

			case EXCLUSION:
				switch (requestedMode) {
					case NORMAL:
						changeModeNotification(requestedMode);
						manager.cancelControllerCommand(zwaveHomeId);
						break;
					default:
						break;
				}
				break;

			}
	}
    
    /**
     * Tracks mode change progress.
     * 
     * Inclusion/Exclusion is automatically left when a device is added/removed, or otherwise 
     * a failure is reported by the controller.
     * 
     * Notice however that in Inclusion/Exclusion mode while the controller is waiting for some
     * user action messages from the network are not processed, so some form of timeout has to
     * be handled outside this controller.
     * 
     */
	
	@Override
	public void onCallback(ControllerState state, ControllerError err, Object context) {
		switch (mode) {
			case INCLUSION:
			case EXCLUSION:
				switch (state) {
					case COMPLETED:
					case FAILED:
					case CANCEL:
						changeMode(Mode.NORMAL); 
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Handle notifications from the controller to perform device discovery and initialization
	 */
	@Override
	public void onNotification(Notification notification, Object context) {
		switch (notification.getType()) {
		case DRIVER_READY:
			synchronized (this) {
				LOG.debug("Driver ready home id: "+notification.getHomeId());
				zwaveHomeId = ((Long)notification.getHomeId()).intValue();
			}
			break;
		case DRIVER_FAILED:
			LOG.error("Driver failed");
			break;
		case DRIVER_RESET:
			LOG.error("Driver reset");
			break;
			
		case AWAKE_NODES_QUERIED:
		case ALL_NODES_QUERIED:
		case ALL_NODES_QUERIED_SOME_DEAD:
			LOG.debug("Nodes queried saving configuration");
			manager.writeConfig(zwaveHomeId);
			break;

			
		case NODE_NEW:
			LOG.debug("Node new node id: "+notification.getNodeId());
			break;
		case NODE_ADDED:
			LOG.debug("Node added node id: "+notification.getNodeId());
			break;
		case ESSENTIAL_NODE_QUERIES_COMPLETE:
			LOG.debug("Node essential queries completed node id: "+notification.getNodeId());
			break;
		case NODE_QUERIES_COMPLETE:
			LOG.debug("Node complete queries completed node id: "+notification.getNodeId());
			manager.writeConfig(zwaveHomeId);
			break;

		case NODE_REMOVED:
			LOG.debug("Node removed node id: "+notification.getNodeId());
			manager.writeConfig(zwaveHomeId);
			break;
		
		
		case NOTIFICATION:
			LOG.debug("Event "+notification.getType().name()+ " node id "+notification.getNodeId()+ " "+notification.getByte());
			break;
		
		default:
			LOG.debug("Event "+notification.getType().name()+ " node id "+notification.getNodeId());
			break;
		}
	}


}
