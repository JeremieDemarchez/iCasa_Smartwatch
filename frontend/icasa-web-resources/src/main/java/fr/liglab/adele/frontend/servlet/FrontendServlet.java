package fr.liglab.adele.frontend.servlet;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 4:11 PM
 */

@Component
@Instantiate
public class FrontendServlet {




    //@Property(value = "/simulator")
    //private String servletName;

    @Property(value = "/simulator/assets")
    private String resources ;

    @Property(value = "/simulator")
    private String resourcesPages;

    @Property(value = "/simulator/maps")
    private String mapResources;



    @Bind
    private void bindHttpService(HttpService service) {
        System.out.println("Register resources");
        try {
            //service.registerServlet(servletName, this, null, null);
            service.registerResources(resources, "/assets", null);
            service.registerResources(resourcesPages, "/www", null);
            service.registerResources(mapResources, "/", new HttpExternalResourceContext("maps"));
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    @Unbind
    public void unbindHttpService(HttpService service) {
        System.out.println("Unregister resources");
        //service.unregister(servletName);
        service.unregister(resources);
        service.unregister(resourcesPages);
        service.unregister(mapResources);

    }

}
