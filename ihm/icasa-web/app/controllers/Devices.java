/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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

    public static Result getById(Integer id) { // GET

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

    public static Result update(Integer id) { //PUT

        Form<Device> form = form(Device.class).bindFromRequest();
        Device model = form.get();
        model.id = id;
        model.update();
        return ok(toJson(model));
    }


    public static Result delete(Integer id) { // DELETE

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