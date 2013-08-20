/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.dependency.manager.interceptor;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.dependency.interceptors.ServiceTrackingInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.TransformedServiceReference;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.dependency.manager.DeviceDependency;
import fr.liglab.adele.icasa.device.GenericDevice;

@Component(name = "DeviceAccessTrackingInterceptor")
@Provides(specifications = { ServiceTrackingInterceptor.class }, properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
@Instantiate(name = "DeviceAccessTrackingInterceptor-0")
public class DeviceAccessTrackingInterceptor implements ServiceTrackingInterceptor {
    
    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG_DEVICE);

    @Requires
    private AccessManager accessManager;

    private Boolean disableInterceptor = false;

    public DeviceAccessTrackingInterceptor(BundleContext context) {
        String disableStr = context.getProperty(Constants.DISABLE_ACCESS_POLICY_PROPERTY);

        if (disableStr != null) {
            disableInterceptor = Boolean.valueOf(disableStr);
        }
    }

    @Override
    public <S> TransformedServiceReference<S> accept(DependencyModel dependency, BundleContext context,
            TransformedServiceReference<S> ref) {

        // Dependency obtained from iCasa Requires
        if (dependency instanceof DeviceDependency) {
            DeviceDependency deviceDependency = (DeviceDependency) dependency;

            String appId = deviceDependency.getApplicationId();
            String deviceId = (String) ref.get(GenericDevice.DEVICE_SERIAL_NUMBER);

            // No application associated or Device Id not Found
            if (appId == null || deviceId == null) {
                return null;
            }

            AccessRight accessRight = accessManager.getAccessRight(appId, deviceId);
            deviceDependency.addAccessRight(deviceId, accessRight);
            
            // Not injected if device is not visible 
            if (!accessRight.isVisible()) {
                return null;
            }
            
            // No injected if device is not in tracker (mandatory properties case)
            if (!deviceDependency.deviceIsTracked(deviceId)) {
                return null;
            }

        } else {

            // Only platform components has access to the device instances using iPOJO
            if (!isPlatformComponent(dependency)) {
                // Interceptor is disable by the configuration
                if (!disableInterceptor) {
                    return null;
                }
            } else {
                logger.debug("Only platform components have access to devices");
            }
        }

        return ref;
    }

    /**
     * Determines if the component having this dependency is a iCasa Platform component
     * 
     * @param dependency
     * @return
     */
    private boolean isPlatformComponent(DependencyModel dependency) {
        String[] specs = dependency.getComponentInstance().getFactory().getComponentDescription()
                .getprovidedServiceSpecification();

        for (String specification : specs) {
            if (specification.equals(ContextManager.class.getName()))
                return true;
        }

        return false;
    }

    @Override
    public void open(DependencyModel dependency) {

    }

    @Override
    public void close(DependencyModel dependency) {

    }
}
