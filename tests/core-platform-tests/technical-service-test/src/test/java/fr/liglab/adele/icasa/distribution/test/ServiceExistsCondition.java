package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.commons.test.utils.Condition;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * COndition is true only if specified service exists.
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
