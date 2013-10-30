package fr.liglab.adele.dashboard.servlet;

import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;
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
 * Time: 6:01 PM
 */

@Component
@Instantiate
public class MapServlet   extends HttpServlet {

    private final BundleContext context;
    @Property(value="/dashboard/map")
    String alias;

    public MapServlet(BundleContext c){
        this.context = c;
    }

    @Requires
    MapService mapService;

    @Bind
    public void bindHTTPService(HttpService service){
        try {
            service.registerServlet(alias, this, null, null);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    @Unbind
    public void unbindHTTPService(HttpService service){
        service.unregister(alias);
    }


    /**
     * Called by the server (via the <code>service</code> method) to
     * allow a servlet to handle a GET request.
     * <p/>
     * <p>Overriding this method to support a GET request also
     * automatically supports an HTTP HEAD request. A HEAD
     * request is a GET request that returns no body in the
     * response, only the request header fields.
     * <p/>
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or
     * output stream object, and finally, write the response data.
     * It's best to include content type and encoding. When using
     * a <code>PrintWriter</code> object to return the response,
     * set the content type before accessing the
     * <code>PrintWriter</code> object.
     * <p/>
     * <p>The servlet container must write the headers before
     * committing the response, because in HTTP the headers must be sent
     * before the response body.
     * <p/>
     * <p>Where possible, set the Content-Length header (with the
     * {@link javax.servlet.ServletResponse#setContentLength} method),
     * to allow the servlet container to use a persistent connection
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     * <p/>
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     * <p/>
     * <p>The GET method should be safe, that is, without
     * any side effects for which users are held responsible.
     * For example, most form queries have no side effects.
     * If a client request is intended to change stored data,
     * the request should use some other HTTP method.
     * <p/>
     * <p>The GET method should also be idempotent, meaning
     * that it can be safely repeated. Sometimes making a
     * method safe also makes it idempotent. For example,
     * repeating queries is both safe and idempotent, but
     * buying a product online or modifying data is neither
     * safe nor idempotent.
     * <p/>
     * <p>If the request is incorrectly formatted, <code>doGet</code>
     * returns an HTTP "Bad Request" message.
     *
     * @param req  an {@link javax.servlet.http.HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link javax.servlet.http.HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws java.io.IOException            if an input or output error is
     *                                        detected when the servlet handles
     *                                        the GET request
     * @throws javax.servlet.ServletException if the request for the GET
     *                                        could not be handled
     * @see javax.servlet.ServletResponse#setContentType
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String mapId = req.getPathInfo();
        if(mapId.startsWith("/")){
            mapId = mapId.substring(1);//remove first char "/"
        }

        if (!mapService.contains(mapId)){
             resp.setStatus(404);//map not found.
        } else {
            ICasaMap map = mapService.getMap(mapId);
            String result = getTemplate().toString();
            result = result.replace("@mapId", mapId);
            result = result.replace("@pluginIds", "");
            result = result.replace("@widgetIds", "");
            result = result.replace("@gatewayURL", map.getGatewayURL());
            result = result.replace("@mapImgSrc", "/dashboard/maps/" + map.getImgFile());
            PrintWriter writer = resp.getWriter();
            writer.append(result);
            writer.close();
        }
    }

    private StringBuilder getTemplate() throws IOException {
        //get template.
        URL f= context.getBundle().getResource("www/map.html");

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
