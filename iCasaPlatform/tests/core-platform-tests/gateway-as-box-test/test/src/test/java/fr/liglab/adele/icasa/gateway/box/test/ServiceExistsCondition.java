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
package fr.liglab.adele.icasa.gateway.box.test;

import fr.liglab.adele.commons.test.utils.Condition;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Condition is true only if specified service exists.
 *
 * @author Thomas Leveque
 */
public class ServiceExistsCondition implements Condition {

    private BundleContext _context;
    private Class _clazz;

    public ServiceExistsCondition(BundleContext context, Class clazz) {
        _context = context;
        _clazz = clazz;
    }

    public boolean isChecked() {
        return getService(_context, _clazz) != null;
    }

    private Object getService(BundleContext context, Class clazz) {
        ServiceReference serviceRef = context.getServiceReference(clazz.getName());
        if (serviceRef == null)
            return null;

        return context.getService(serviceRef);
    }

    public String getDescription() {
        return "A service providing interface " + _clazz.getName() + " must exist.";
    }
}
