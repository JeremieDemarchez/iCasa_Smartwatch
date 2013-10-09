/*
 * Copyright ADELE Research Group LIG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package controllers;

import models.values.Application;
import models.values.Product;
import org.codehaus.jackson.node.ArrayNode;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


public class ApplicationController extends Controller {
	static Form<Application> applicationForm = form(Application.class);

	public static Result applications(){
        System.out.println("Getting applications");

        List<Application> allProducts = null;
        allProducts = Application.all();
        ArrayNode applications = Json.newObject().arrayNode();
        for (Application application : allProducts) {
            applications.add(Application.toJson(application));
        }
        return ok(applications);
    }

	public static Result addApplicationForm(){
		return ok(views.html.products.applications.newApplication.render(form(Application.class)));
	}

	public static Result addApplication(){
		Form<Application> filledForm = applicationForm.bindFromRequest();
		if (filledForm.hasErrors()){
			return badRequest(views.html.products.applications.newApplication.render(filledForm));
		} else {
			try{
				Application.create(filledForm.get());
			}catch (Exception ex){
				return badRequest(views.html.products.applications.newApplication.render(filledForm));
			}
			return redirect(routes.ApplicationController.applications());
		}
	}
}
