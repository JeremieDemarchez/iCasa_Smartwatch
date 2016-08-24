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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
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
import org.zwave4j.ControllerCallback;
import org.zwave4j.ControllerCommand;
import org.zwave4j.ControllerError;
import org.zwave4j.ControllerState;
import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Notification;
import org.zwave4j.NotificationWatcher;
import org.zwave4j.Options;
import org.zwave4j.ValueId;
import org.zwave4j.ZWave4j;





import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.provider.Creator;
import fr.liglab.adele.zwave.device.api.ZWaveNetworkEvent;
import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.api.ZwaveRepeater;

@ContextEntity(services = { ZwaveController.class, ZwaveDevice.class, ZwaveRepeater.class })
@Provides(specifications = { DiscoveryService.class, DiscoveryIntrospection.class })

public class ControllerImpl extends AbstractDiscoveryComponent implements ZwaveRepeater, ZwaveDevice, ZwaveController, NotificationWatcher, ControllerCallback {

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
	private long zwaveHomeId;

	@ContextEntity.State.Field(service = ZwaveDevice.class, state = ZwaveDevice.NODE_ID)
	private short zwaveNodeId;
	
    @ContextEntity.State.Pull(service = ZwaveDevice.class,state = ZwaveDevice.NODE_ID)
    Supplier<Short> pullNodeId=()-> zwaveHomeId != -1 ? manager.getControllerNodeId(zwaveHomeId) : -1;

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
		return newMode;
	}
	

	/**
	 * The last network event
	 */

	@ContextEntity.State.Field(service = ZwaveController.class, state = ZwaveController.NETWORK_EVENT)
	private ZWaveNetworkEvent event;

	@ContextEntity.State.Push(service = ZwaveController.class, state = ZwaveController.NETWORK_EVENT)
	public ZWaveNetworkEvent notifyEvent() {
		return null;
	}

	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_TYPE)
	private Integer deviceType;

	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.MANUFACTURER_ID)
	private Integer manufacturerId;

	@ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.DEVICE_ID)
	private Integer deviceId;

	/**
	 * Constructor
	 */
	protected ControllerImpl(BundleContext bundleContext) {
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
	public int getHomeId() {
		return (int) zwaveHomeId;
	}

	@Override
	public int getNodeId() {
		return zwaveNodeId;
	}

	@Override
	public ZWaveNetworkEvent getLastEvent() {
		return event;
	}

	@Override
	public int getManufacturerId() {
		return manufacturerId;
	}

	@Override
	public int getDeviceId() {
		return deviceId;
	}

	@Override
	public int getDeviceType() {
		return deviceType;
	}

	/**
	 * LifeCycle
	 */
	@Validate
	protected synchronized void start() {
		
        Thread.currentThread().setContextClassLoader(ZWave4j.class.getClassLoader());
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);

        final Options options = Options.create("config", "", "");
        options.addOptionBool("ConsoleOutput", true);
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

	@Override
	public void onNotification(Notification notification, Object context) {
		switch (notification.getType()) {
		case DRIVER_READY:
			synchronized (this) {
				System.out.println(String.format("Driver ready\n"
						+ "\thome id: %d", notification.getHomeId()));
				zwaveHomeId = notification.getHomeId();
			}
			break;
		case DRIVER_FAILED:
			System.out.println("Driver failed");
			break;
		case DRIVER_RESET:
			System.out.println("Driver reset");
			break;
		case AWAKE_NODES_QUERIED:
			System.out.println("Awake nodes queried");
			break;
		case ALL_NODES_QUERIED:
			System.out.println("All nodes queried");
			manager.writeConfig(zwaveHomeId);
			break;
		case ALL_NODES_QUERIED_SOME_DEAD:
			System.out.println("All nodes queried some dead");
			manager.writeConfig(zwaveHomeId);
			break;
		case POLLING_ENABLED:
			System.out.println("Polling enabled");
			break;
		case POLLING_DISABLED:
			System.out.println("Polling disabled");
			break;
		case NODE_NEW:
			System.out.println(String.format(
					"Node new\n" + "\tnode id: %d",
					notification.getNodeId()));
			break;
		case NODE_ADDED:
			System.out.println(String.format("Node added\n"
					+ "\tnode id: %d", notification.getNodeId()));
			break;
		case NODE_REMOVED:
			System.out.println(String.format("Node removed\n"
					+ "\tnode id: %d", notification.getNodeId()));
			break;
		case ESSENTIAL_NODE_QUERIES_COMPLETE:
			System.out.println(String.format(
					"Node essential queries complete\n" + "\tnode id: %d",
					notification.getNodeId()));
			break;
		case NODE_QUERIES_COMPLETE:
			System.out.println(String.format("Node queries complete\n"
					+ "\tnode id: %d", notification.getNodeId()));
			break;
		case NODE_EVENT:
			System.out.println(String.format("Node event\n"
					+ "\tnode id: %d\n" + "\tevent id: %d",
					notification.getNodeId(), notification.getEvent()));
			break;
		case NODE_NAMING:
			System.out.println(String.format("Node naming\n"
					+ "\tnode id: %d", notification.getNodeId()));
			break;
		case NODE_PROTOCOL_INFO:
			System.out.println(String.format("Node protocol info\n"
					+ "\tnode id: %d\n" + "\ttype: %s", notification
					.getNodeId(), manager.getNodeType(
					notification.getHomeId(), notification.getNodeId())));
			break;
		case VALUE_ADDED:
			System.out.println(String.format("Value added\n"
					+ "\tnode id: %d\n" + "\tcommand class: %d\n"
					+ "\tinstance: %d\n" + "\tindex: %d\n"
					+ "\tgenre: %s\n" + "\ttype: %s\n" + "\tlabel: %s\n"
					+ "\tvalue: %s", notification.getNodeId(), notification
					.getValueId().getCommandClassId(), notification
					.getValueId().getInstance(), notification.getValueId()
					.getIndex(), notification.getValueId().getGenre()
					.name(), notification.getValueId().getType().name(),
					manager.getValueLabel(notification.getValueId()),
					getValue(notification.getValueId())));
			break;
		case VALUE_REMOVED:
			System.out.println(String.format("Value removed\n"
					+ "\tnode id: %d\n" + "\tcommand class: %d\n"
					+ "\tinstance: %d\n" + "\tindex: %d", notification
					.getNodeId(), notification.getValueId()
					.getCommandClassId(), notification.getValueId()
					.getInstance(), notification.getValueId().getIndex()));
			break;
		case VALUE_CHANGED:
			System.out.println(String.format("Value changed\n"
					+ "\tnode id: %d\n" + "\tcommand class: %d\n"
					+ "\tinstance: %d\n" + "\tindex: %d\n" + "\tvalue: %s",
					notification.getNodeId(), notification.getValueId()
							.getCommandClassId(), notification.getValueId()
							.getInstance(), notification.getValueId()
							.getIndex(),
					getValue(notification.getValueId())));
			break;
		case VALUE_REFRESHED:
			System.out.println(String.format("Value refreshed\n"
					+ "\tnode id: %d\n" + "\tcommand class: %d\n"
					+ "\tinstance: %d\n" + "\tindex: %d" + "\tvalue: %s",
					notification.getNodeId(), notification.getValueId()
							.getCommandClassId(), notification.getValueId()
							.getInstance(), notification.getValueId()
							.getIndex(),
					getValue(notification.getValueId())));
			break;
		case GROUP:
			System.out.println(String.format("Group\n" + "\tnode id: %d\n"
					+ "\tgroup id: %d", notification.getNodeId(),
					notification.getGroupIdx()));
			break;

		case SCENE_EVENT:
			System.out.println(String.format("Scene event\n"
					+ "\tscene id: %d", notification.getSceneId()));
			break;
		case CREATE_BUTTON:
			System.out.println(String.format("Button create\n"
					+ "\tbutton id: %d", notification.getButtonId()));
			break;
		case DELETE_BUTTON:
			System.out.println(String.format("Button delete\n"
					+ "\tbutton id: %d", notification.getButtonId()));
			break;
		case BUTTON_ON:
			System.out.println(String.format("Button on\n"
					+ "\tbutton id: %d", notification.getButtonId()));
			break;
		case BUTTON_OFF:
			System.out.println(String.format("Button off\n"
					+ "\tbutton id: %d", notification.getButtonId()));
			break;
		case NOTIFICATION:
			System.out.println("Notification "+notification.getByte());
			break;
		default:
			System.out.println(notification.getType().name());
			break;
		}
	}

	private static Object getValue(ValueId valueId) {
		switch (valueId.getType()) {
		case BOOL:
			AtomicReference<Boolean> b = new AtomicReference<>();
			Manager.get().getValueAsBool(valueId, b);
			return b.get();
		case BYTE:
			AtomicReference<Short> bb = new AtomicReference<>();
			Manager.get().getValueAsByte(valueId, bb);
			return bb.get();
		case DECIMAL:
			AtomicReference<Float> f = new AtomicReference<>();
			Manager.get().getValueAsFloat(valueId, f);
			return f.get();
		case INT:
			AtomicReference<Integer> i = new AtomicReference<>();
			Manager.get().getValueAsInt(valueId, i);
			return i.get();
		case LIST:
			return null;
		case SCHEDULE:
			return null;
		case SHORT:
			AtomicReference<Short> s = new AtomicReference<>();
			Manager.get().getValueAsShort(valueId, s);
			return s.get();
		case STRING:
			AtomicReference<String> ss = new AtomicReference<>();
			Manager.get().getValueAsString(valueId, ss);
			return ss.get();
		case BUTTON:
			return null;
		case RAW:
			AtomicReference<short[]> sss = new AtomicReference<>();
			Manager.get().getValueAsRaw(valueId, sss);
			return sss.get();
		default:
			return null;
		}
	}

}
