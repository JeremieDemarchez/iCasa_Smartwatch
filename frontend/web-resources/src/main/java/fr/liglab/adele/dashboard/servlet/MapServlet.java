package fr.liglab.adele.dashboard.servlet;

import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;


import java.io.*;
import java.net.URL;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 6:01 PM
 */

@Component
@Provides
@Instantiate
public class MapServlet   extends DefaultController {

    private final BundleContext context;

    public MapServlet(BundleContext c){
        this.context = c;
    }

    @Requires
    MapService mapService;

    @Route(method = HttpMethod.GET, uri = "/simulator/map/{mapId}")
    public Result getMap(@Parameter("mapId") String mapId){
        String result = null;

        if (!mapService.contains(mapId)){
            return notFound();
        } else {
            ICasaMap map = mapService.getMap(mapId);
            try {
                result = getTemplate().toString();
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError();
            }
            result = result.replace("@mapId", mapId);
            result = result.replace("@pluginIds", "");
            result = result.replace("@widgetIds", "");
            result = result.replace("@servletType", "simulator");//dashboard or simulator.
            result = result.replace("@gatewayURL", map.getGatewayURL());
            result = result.replace("@mapImgSrc", "/dashboard/maps/" + map.getImgFile());
        }
        result = result.replace("@servletType", "simulator");//dashboard or simulator.
        return ok(result).as(MimeTypes.HTML);
    }

    @Route(method = HttpMethod.GET, uri = "/dashboard/map/{mapId}")
    public Result getDashboardMap(@Parameter("mapId") String mapId){
        String result = null;

        if (!mapService.contains(mapId)){
            return notFound();
        } else {
            ICasaMap map = mapService.getMap(mapId);
            try {
                result = getTemplate().toString();
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError();
            }
            result = result.replace("@mapId", mapId);
            result = result.replace("@pluginIds", "");
            result = result.replace("@widgetIds", "");
            result = result.replace("@servletType", "dashboard");//dashboard or simulator.
            result = result.replace("@gatewayURL", map.getGatewayURL());
            result = result.replace("@mapImgSrc", "/dashboard/maps/" + map.getImgFile());
        }
        result = result.replace("@servletType", "dashboard");//dashboard or simulator.
        return ok(result).as(MimeTypes.HTML);
    }

    @Route(method = HttpMethod.GET, uri = "/dashboard/maps/{mapFile}")
    public Result getMapFile(@Parameter("mapFile") String mapFile){
        File file = new File("maps", mapFile);
        return ok(file);
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
