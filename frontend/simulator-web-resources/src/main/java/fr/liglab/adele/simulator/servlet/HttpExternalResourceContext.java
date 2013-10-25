package fr.liglab.adele.simulator.servlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: garciai@imag.fr
 * Date: 10/22/13
 * Time: 10:17 AM
 */
public class HttpExternalResourceContext implements HttpContext {

    private final String rootDir;


    public HttpExternalResourceContext(String rootDir){
        this.rootDir = rootDir;
    }

    /**
     * Handles security for the specified request.
     * <p/>
     * <p/>
     * The Http Service calls this method prior to servicing the specified
     * request. This method controls whether the request is processed in the
     * normal manner or an error is returned.
     * <p/>
     * <p/>
     * If the request requires authentication and the Authorization header in
     * the request is missing or not acceptable, then this method should set the
     * WWW-Authenticate header in the response object, set the status in the
     * response object to Unauthorized(401) and return <code>false</code>. See
     * also RFC 2617: <i>HTTP Authentication: Basic and Digest Access
     * Authentication </i> (available at http://www.ietf.org/rfc/rfc2617.txt).
     * <p/>
     * <p/>
     * If the request requires a secure connection and the <code>getScheme</code>
     * method in the request does not return 'https' or some other acceptable
     * secure protocol, then this method should set the status in the response
     * object to Forbidden(403) and return <code>false</code>.
     * <p/>
     * <p/>
     * When this method returns <code>false</code>, the Http Service will send
     * the response back to the client, thereby completing the request. When
     * this method returns <code>true</code>, the Http Service will proceed with
     * servicing the request.
     * <p/>
     * <p/>
     * If the specified request has been authenticated, this method must set the
     * {@link #AUTHENTICATION_TYPE} request attribute to the type of
     * authentication used, and the {@link #REMOTE_USER} request attribute to
     * the remote user (request attributes are set using the
     * <code>setAttribute</code> method on the request). If this method does not
     * perform any authentication, it must not set these attributes.
     * <p/>
     * <p/>
     * If the authenticated user is also authorized to access certain resources,
     * this method must set the {@link #AUTHORIZATION} request attribute to the
     * <code>Authorization</code> object obtained from the
     * <code>org.osgi.service.useradmin.UserAdmin</code> service.
     * <p/>
     * <p/>
     * The servlet responsible for servicing the specified request determines
     * the authentication type and remote user by calling the
     * <code>getAuthType</code> and <code>getRemoteUser</code> methods,
     * respectively, on the request.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return <code>true</code> if the request should be serviced, <code>false</code>
     *         if the request should not be serviced and Http Service will send
     *         the response back to the client.
     * @throws java.io.IOException may be thrown by this method. If this
     *                             occurs, the Http Service will terminate the request and close
     *                             the socket.
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return true;
    }

    /**
     * Maps a resource name to a URL.
     * <p/>
     * <p/>
     * Called by the Http Service to map a resource name to a URL. For servlet
     * registrations, Http Service will call this method to support the
     * <code>ServletContext</code> methods <code>getResource</code> and
     * <code>getResourceAsStream</code>. For resource registrations, Http Service
     * will call this method to locate the named resource. The context can
     * control from where resources come. For example, the resource can be
     * mapped to a file in the bundle's persistent storage area via
     * <code>bundleContext.getDataFile(name).toURL()</code> or to a resource in
     * the context's bundle via <code>getClass().getResource(name)</code>
     *
     * @param name the name of the requested resource
     * @return URL that Http Service can use to read the resource or
     *         <code>null</code> if the resource does not exist.
     */
    @Override
    public URL getResource(String name) {
        File file = new File(rootDir, name);
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Maps a name to a MIME type.
     * <p/>
     * Called by the Http Service to determine the MIME type for the name. For
     * servlet registrations, the Http Service will call this method to support
     * the <code>ServletContext</code> method <code>getMimeType</code>. For
     * resource registrations, the Http Service will call this method to
     * determine the MIME type for the Content-Type header in the response.
     *
     * @param name determine the MIME type for this name.
     * @return MIME type (e.g. text/html) of the name or <code>null</code> to
     *         indicate that the Http Service should determine the MIME type
     *         itself.
     */
    @Override
    public String getMimeType(String name) {
        return null;
    }
}
