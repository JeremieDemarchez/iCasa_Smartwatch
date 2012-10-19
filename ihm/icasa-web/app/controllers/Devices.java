package controllers;

import models.*;
import play.data.*;
import play.mvc.*;
import scala.util.Random;
import play.api.libs.iteratee.*;
import play.api.libs.concurrent.*;
import java.util.List;
import play.api.libs.EventSource$;

import static play.libs.Json.toJson;

public class Devices extends Controller {

    public static Result getAll() { // GET

        List<Device> list = Device.find.orderBy("id").findList();
        return ok(toJson(list));
    }

    public static Result getById(Long id) { // GET

        Device modelToFind = Device.find.byId(id);

        if(modelToFind!=null) {
            return ok(toJson(modelToFind));
        } else {
            return badRequest("not found");
        }
    }

    public static Result create() { //POST

        Form<Device> form = form(Device.class).bindFromRequest();
        Device model = form.get();
        model.save();
        return ok(toJson(model));
    }

    public static Result update(Long id) { //PUT

        Form<Device> form = form(Device.class).bindFromRequest();
        Device model = form.get();
        model.id = id;
        model.update();
        return ok(toJson(model));
    }


    public static Result delete(Long id) { // DELETE

        Device modelToFind = Device.find.byId(id);
        if(modelToFind!=null) {
            modelToFind.delete();
            return ok(toJson(true));
        } else {
            return badRequest("not found");
        }

    }

    public static Result query(String fieldName, String value) { // GET

        List<Device> list = Device.find.where().eq(fieldName, value).findList();
        return ok(toJson(list));
    }
}