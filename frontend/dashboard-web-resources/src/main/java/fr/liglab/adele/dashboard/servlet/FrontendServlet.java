package fr.liglab.adele.dashboard.servlet;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 4:11 PM
 */

@Component
@Instantiate
public class FrontendServlet extends HttpServlet {


    private final BundleContext context;

    @Property(value = "/dashboard")
    private String servletName;

    @Property(value = "/dashboard/assets")
    private String resources ;

    @Property(value = "/dashboard/maps")
    private String mapResources;

    public FrontendServlet(BundleContext c){
        this.context = c;
    }

    @Bind
    private void bindHttpService(HttpService service) {
        try {
            service.registerServlet(servletName, this, null, null);
            service.registerResources(resources, "/assets", null);
            service.registerResources(mapResources, "/", new HttpExternalResourceContext("maps"));
        } catch (NamespaceException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Unbind
    public void unbindHttpService(HttpService service) {
        service.unregister(servletName);
        service.unregister(resources);
        service.unregister(mapResources);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = getTemplate().toString();
        PrintWriter writer = resp.getWriter();
        writer.append(result);
        writer.close();
    }

    private StringBuilder getTemplate() throws IOException {
        //get template.
        URL f= context.getBundle().getResource("www/index.html");

        byte[] buf = new byte[8192];

        InputStream is = f.openStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        StringBuilder result = new StringBuilder();
        while( ( line = reader.readLine() ) != null)
        {
            result.append(line).append("\n");
        }
        reader.close();
        return result;

    }

}
