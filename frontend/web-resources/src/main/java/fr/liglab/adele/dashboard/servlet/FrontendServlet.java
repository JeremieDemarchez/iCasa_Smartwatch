package fr.liglab.adele.dashboard.servlet;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.framework.BundleContext;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 4:11 PM
 */

@Component
@Provides
@Instantiate
public class FrontendServlet extends DefaultController {


    private final BundleContext context;

    public FrontendServlet(BundleContext c){
        this.context = c;
    }



    @Route(method = HttpMethod.GET, uri = "/dashboard")
    public Result getDashboard(){
        String result = null;
        try {
            result = getTemplate().toString();
        } catch (IOException e) {
            return internalServerError();
        }
        result = result.replace("@servletType", "dashboard");//dashboard or simulator.
        return ok(result).as(MimeTypes.HTML);
    }

    @Route(method = HttpMethod.GET, uri = "/simulator")
    public Result getSimulator(){
        String result = null;
        try {
            result = getTemplate().toString();
        } catch (IOException e) {
            return internalServerError();
        }
        result = result.replace("@servletType", "simulator");//dashboard or simulator.
        return ok(result).as(MimeTypes.HTML);
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
