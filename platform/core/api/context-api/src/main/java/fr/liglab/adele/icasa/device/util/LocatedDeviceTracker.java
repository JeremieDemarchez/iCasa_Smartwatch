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
package fr.liglab.adele.icasa.device.util;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import org.apache.felix.ipojo.util.Tracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Utility class to track iCasa devices.
 *
 */
public class LocatedDeviceTracker implements LocatedDeviceTrackerCustomizer {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);
    /**
     * The bundle context against which this LocatedDeviceTracker object is tracking.
     */
    protected BundleContext m_context;

    /**
     * Tracker of technical service used to listen device events.
     */
    protected Tracker contextMgrTracker;

    /**
     * the filter specifying type criteria for the devices to track.
     */
    protected LocatedDeviceFilter m_typeFilter;

    /**
     * the filter specifying prop matching criteria for the devices to track.
     */
    protected LocatedDeviceFilter m_propFilter;

    /**
     * The LocatedDeviceTrackerCustomizer object for this tracker.
     */
    protected LocatedDeviceTrackerCustomizer m_customizer;

    /**
     * The class name to be tracked. If this field is set, then we are
     * tracking by class name.
     */
    private final Class<? extends GenericDevice> m_trackClass;

    /**
     * The tracked devices: String device serial number -> device object.
     */
    //  private Tracked m_tracked;

    /**
     * Creates a LocatedDeviceTracker object on the specified class name.
     * Services registered under the specified class name will be tracked by this LocatedDeviceTracker object.
     * @param context the BundleContext object against which the tracking is done.
     * @param clazz the Class name of the services to be tracked.
     * @param customizer the customizer object to call when services are added, modified, or removed in this LocatedDeviceTracker object. If customizer is null, then this LocatedDeviceTracker object will be used as
     *            the TrackerCustomizer object and the LocatedDeviceTracker object will call the TrackerCustomizer methods on itself.
     */
    public LocatedDeviceTracker(BundleContext context, final Class<? extends GenericDevice> clazz, LocatedDeviceTrackerCustomizer customizer, final String... mandatoryProperties) {

        this.m_context = context;
        this.m_trackClass = clazz;
        if (customizer == null) {
            m_customizer = this;
        } else {
            m_customizer = customizer;
        }
        this.m_typeFilter = new DeviceTypeFilter(clazz);
        if (mandatoryProperties.length == 0)
            this.m_propFilter = new DefaultTrueFilter();
        else
            this.m_propFilter = new MandatoryPropFilter(mandatoryProperties);
    }

    /**
     * Opens this LocatedDeviceTracker object and begin tracking services.
     * <p>
     * Services which match the search criteria specified when this LocatedDeviceTracker object was created are now tracked by this LocatedDeviceTracker object.
     */
    public synchronized void open() {
        logger.trace("[DeviceTracker] open");
        /**       if (m_tracked != null) { return; }

         m_tracked = new Tracked();
         contextMgrTracker = new Tracker(m_context, ContextManager.class.getName(), new TrackerCustomizer() {
        @Override
        public boolean addingService(ServiceReference serviceReference) {
        return true;
        }

        @Override
        public void addedService(ServiceReference serviceReference) {
        logger.trace("[DeviceTracker] iCasa ContextManager added");
        ContextManager contextMgr = (ContextManager) m_context.getService(serviceReference);
        if (m_tracked == null)
        return;

        synchronized (m_tracked) {
        m_tracked.startTracking(contextMgr);
        }
        m_tracked.trackInitialDevices();
        }

        @Override
        public void modifiedService(ServiceReference serviceReference, Object o) {
        // Nothing to do.
        }

        @Override
        public void removedService(ServiceReference serviceReference, Object o) {
        logger.trace("[DeviceTracker] iCasa ContextManager removed");
        if (m_tracked == null)
        return;

        synchronized (m_tracked) {
        m_tracked.stopTracking();
        }
        }
        });
         contextMgrTracker.open();**/
    }

    /**
     * Closes this LocatedDeviceTracker object.
     * <p>
     * This method should be called when this LocatedDeviceTracker object should end the tracking of services.
     */
    public synchronized void close() {
        logger.trace("[DeviceTracker] close");
        if (contextMgrTracker != null) {
            contextMgrTracker.close();
            contextMgrTracker = null;
        }

        /**    if (m_tracked == null) { return; }

         m_tracked.close();
         m_tracked = null;**/

    }

    /**
     * Default implementation of the LocatedDeviceTrackerCustomizer.addingDevice method.
     */
    public boolean addingDevice(LocatedDevice device) {
        return true;
    }

    /**
     * Default implementation of the LocatedDeviceTrackerCustomizer.addedDevice method.
     */
    public void addedDevice(LocatedDevice device) {
        // Nothing to do.
    }

    /**
     * Default implementation of the LocatedDeviceTrackerCustomizer.modifiedDevice method.
     */
    public void modifiedDevice(LocatedDevice device, String propertyName, Object oldValue, Object newValue) {
        // Nothing to do.
    }

    @Override
    public void movedDevice(LocatedDevice device, Position oldPosition, Position newPosition) {
        // Nothing to do.
    }

    /**
     * Default implementation of the LocatedDeviceTrackerCustomizer.removedDevice method.
     */
    public void removedDevice(LocatedDevice device) {
        // Nothing to do.
    }

    /**
     * Gets the list of stored devices.
     * @return the list containing devices
     */
    public List<LocatedDevice> getDevices() {
        /**  Tracked tracked = this.m_tracked; // use local var since we are not synchronized
         if (tracked == null) { // if LocatedDeviceTracker is not open
         return null;
         }
         synchronized (tracked) {
         int length = tracked.size();
         if (length == 0) { return null; }
         List<LocatedDevice> references = new ArrayList<LocatedDevice>(length);
         references.addAll(tracked.values());
         // The resulting array is sorted by ranking.
         return references;
         }**/
        return null;
    }

    /**
     * Returns the number of devices being tracked by this LocatedDeviceTracker object.
     * @return the Number of devices being tracked.
     */
    public int size() {
        /**    Tracked tracked = this.m_tracked; //use local var since we are not synchronized
         if (tracked == null) { /* if LocatedDeviceTracker is not open */
        /**        return 0;
         }
         return tracked.size();**/
        return 1;
    }



    class DeviceTypeFilter implements LocatedDeviceFilter {

        private Class _clazz;

        public DeviceTypeFilter(Class clazz) {
            _clazz = clazz;
        }

        @Override
        public boolean match(LocatedDevice device) {
            GenericDevice deviceObj = device.getDeviceObject();
            if (deviceObj == null)
                return false;
            return (_clazz.isInstance(deviceObj));
        }
    };

    class DefaultTrueFilter implements LocatedDeviceFilter {

        @Override
        public boolean match(LocatedDevice device) {
            return true;
        }
    }

    class MandatoryPropFilter implements LocatedDeviceFilter {

        private String[] _mandatoryProperties;

        public MandatoryPropFilter(String[] mandatoryProps) {
            _mandatoryProperties = mandatoryProps;
        }

        @Override
        public boolean match(LocatedDevice device) {
            Set<String> deviceProps = device.getProperties();
            for (String prop : _mandatoryProperties) {
                if (!deviceProps.contains(prop))
                    return false;
            }
            return true;
        }
    };
}
