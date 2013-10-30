package fr.liglab.adele.icasa.frontend.services.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: garciai@imag.fr
 * Date: 10/22/13
 * Time: 9:31 AM
 */
public class JSONMap {

    public static JSONObject toJSON(ICasaMap map) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", map.getId());
        json.put("name", map.getName());
        json.put("description", map.getDescription());
        json.put("gatewayURL", map.getGatewayURL());
        json.put("imgFile", map.getImgFile());
        json.put("libs", map.getLibs());
        return json;
    }

    public static ICasaMap toJSON(JSONObject json) throws JSONException {
        ICasaMap map = null;//new ICasaMap();
        json.put("id", map.getId());
        json.put("name", map.getName());
        json.put("description", map.getDescription());
        json.put("gatewayURL", map.getGatewayURL());
        json.put("imgFile", map.getImgFile());
        json.put("libs", map.getLibs());
        return map;
    }



}
