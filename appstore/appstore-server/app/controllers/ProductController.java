package controllers;

import models.User;
import models.values.Category;
import models.values.Order;
import models.values.Product;
import models.values.ProductPrice;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.h2.util.IOUtils;
import play.api.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import java.io.*;
import java.sql.DriverManager;
import java.util.List;

public class ProductController extends Controller {

	static Form<Product> productForm = form(Product.class);

	private static final String TOP_NUMBER = "topNumber";
	
	private static final String PAGE = "page";
	
	private static final String PRODUCTS_PER_PAGE = "productsPerPage";

    @SecureSocial.SecuredAction
    public static Result products(){
        System.out.println("Getting products");
        DynamicForm requestData = form().bindFromRequest();
        System.out.println("get params: " + requestData.toString());
        if (requestData.get(TOP_NUMBER) != null) {
            System.out.println("Getting top products");
            return topProducts(requestData.get(TOP_NUMBER));
        } else if (requestData.get(PAGE) != null){
            System.out.println("Getting products per page");
			return productsPerPage(requestData.get(PAGE), requestData.get(PRODUCTS_PER_PAGE));
		}
		List<Product> allProducts = null;
		allProducts = Product.all();
		ArrayNode products = Json.newObject().arrayNode();
		for (Product product : allProducts) {
			products.add(Product.toJson(product));
		}
		return ok(products);
	}

    @SecureSocial.SecuredAction
    public static Result buyProduct(String id){
        Identity socialUser = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User user = User.getUserByUserId(socialUser.id().id());
        Product toBuy = Product.find.byId(Integer.parseInt(id));
        user.buyProduct(toBuy);

        return ok();
    }

    public static Result topProducts(String topNumber){
		List<Product> allProducts = null;
		int top = 10;
		try{
			top = Integer.valueOf(topNumber);
		}catch(Exception ex){
			top = 10;
		}
		allProducts = Product.getTopProducts(top);
		ArrayNode products = Json.newObject().arrayNode();
		for (Product product : allProducts) {
			products.add(Product.toJson(product));
		}
		return ok(products);
	}
    @SecureSocial.SecuredAction
    public static Result ownedProducts(){
        Identity socialUser = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User user = User.getUserByUserId(socialUser.id().id());
        List<Order> buy = user.orders;
        List<Product> allProducts = null;

        allProducts = user.getOwnedProducts();
        ArrayNode products = Json.newObject().arrayNode();
        for (Product product : allProducts) {
            products.add(Product.toJson(product));
        }
        return ok(products);
    }

    public static Result productsPerPage(String page, String productsPerPage){
		List<Product> allProducts = null;
		int _page;
		int _productsPerPage;
		try{
			_page = Integer.valueOf(page);
			_productsPerPage = Integer.valueOf(productsPerPage);
		}catch(Exception ex){
			ex.printStackTrace();
			_page = 1;
			_productsPerPage = 10;
		}
		allProducts = Product.getPageProducts(_productsPerPage, _page);
		int totalPages = Product.getPageCount(_productsPerPage);
		ObjectNode returningJSONObject = Json.newObject();
		returningJSONObject.put("totalPages", totalPages);
		ArrayNode products = Json.newObject().arrayNode();
		for (Product product : allProducts) {
			products.add(Product.toJson(product));
		}
		returningJSONObject.put("products", products);
		return ok(returningJSONObject);
	}

    @SecureSocial.SecuredAction
    public static Result getProductsPerCategory(String category){
        int _category = 0;
        try{
            _category = Integer.valueOf(category);
        }catch(Exception ex){
            ex.printStackTrace();
            _category = 0;
        }
        List<Product> allProducts = Product.getProductByCategory(_category);
        ArrayNode products = Json.newObject().arrayNode();
        for (Product product : allProducts) {
            products.add(Product.toJson(product));
        }
        return ok(products);
    }

    @SecureSocial.SecuredAction
    public static Result addProduct(){

		JsonNode body = request().body().asJson();
		Form<Product> filledForm = productForm.bindFromRequest();
        Product product;
		if (filledForm.hasErrors()){
            System.out.println("bad form");
			return badRequest();
		} else {
			try{
                product = filledForm.get();
				Product.create(product);
			}catch (Exception ex){
                ex.printStackTrace();
				return badRequest();
			}
			return ok(Product.toJson(product));
		}
	}

    @SecureSocial.SecuredAction
    public static Result uploadImage(String productId){
        System.out.println(productId);
        Product product = Product.find.byId(Integer.parseInt(productId));
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("productPicture");
        String fileURL = "public/images/products/" + productId + ".jpg";
        String serverURL = "assets/images/products/" + productId + ".jpg";
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            File file = picture.getFile();
            try {
                InputStream is    = new FileInputStream(file);
                OutputStream os = new FileOutputStream(new File(fileURL));
                IOUtils.copy(is,os);
                is.close();
                os.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                flash("error", "unable to save file");
                return badRequest();
            }
            product.imageURL = serverURL ;
            product.save();
            return ok(Product.toJson(product));
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }

}