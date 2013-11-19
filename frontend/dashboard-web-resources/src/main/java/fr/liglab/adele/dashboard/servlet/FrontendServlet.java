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

@Component(name="FrontendServlet")
//@Instantiate
public class FrontendServlet extends HttpServlet {


    private final BundleContext context;

    @Property(value = "/dashboard")
    private String servletName;

    @Property(value="dashboard")
    private String servletType;

    @Requires
    HttpService service;

    public FrontendServlet(BundleContext c){
        this.context = c;
    }

    @Bind
    private void bindHttpService(HttpService _service) {
        this.service = _service; //assign http service
    }

    @Unbind
    public void unbindHttpService(HttpService _service) {
        unregister();
        service = null;
    }

    /**
     * When dependencies are resolved, we register the servlet.
     */
    @Validate
    public void onValidate(){
        try {
            service.registerServlet(servletName, this, null, null);
        } catch (NamespaceException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }


    /**
     * When component is invalid, and if there is a references to the http service.
     * The servlet is unregistered.
     */
    @Invalidate
    public void onInvalidate(){
        unregister();
    }

    private void unregister(){
        if (service != null){
            service.unregister(servletName);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = getTemplate().toString();
        result = result.replace("@servletType", servletType);//dashboard or simulator.
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
