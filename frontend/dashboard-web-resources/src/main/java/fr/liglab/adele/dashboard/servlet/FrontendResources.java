package fr.liglab.adele.dashboard.servlet;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * User: garciai@imag.fr
 * Date: 11/18/13
 * Time: 10:28 AM
 */
@Component
@Instantiate
public class FrontendResources {

    @Property(value = "/dashboard/assets")
    private String resources ;

    @Property(value = "/dashboard/maps")
    private String mapResources;

    @Bind
    private void bindHttpService(HttpService service) {
        try {
            service.registerResources(resources, "/assets", null);
            service.registerResources(mapResources, "/", new HttpExternalResourceContext("maps"));
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    @Unbind
    public void unbindHttpService(HttpService service) {
        service.unregister(resources);
        service.unregister(mapResources);

    }

}
