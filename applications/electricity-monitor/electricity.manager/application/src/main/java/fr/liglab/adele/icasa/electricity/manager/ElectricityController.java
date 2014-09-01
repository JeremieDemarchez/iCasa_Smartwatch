package fr.liglab.adele.icasa.electricity.manager;

import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

@Controller
@Path("/electricity")
public class ElectricityController extends DefaultController {

    @Requires
    private ElectricityManager electricityManager;

    @Route(method= HttpMethod.GET, uri="/")
    public Result all() {
        return ok(electricityManager.filterSample());
    }

    @Route(method= HttpMethod.POST, uri="/")
    public Result upload() {
        return ok(electricityManager.filterSample());
    }

}
