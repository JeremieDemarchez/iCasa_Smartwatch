package controllers;

import models.values.Category;
import org.codehaus.jackson.node.ArrayNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: torito
 * Date: 4/5/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryController extends Controller {
    @SecureSocial.SecuredAction
    public static Result getAvailableCategories(){
        List<Category> categoryList = Category.allAvailable();
        ArrayNode categories = Json.newObject().arrayNode();
        for (Category category: categoryList){
            categories.add(Category.toJson(category));
        }
        return ok(categories);
    }
}
