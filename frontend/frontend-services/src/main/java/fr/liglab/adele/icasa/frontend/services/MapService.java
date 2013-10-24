package fr.liglab.adele.icasa.frontend.services;

import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;

import java.util.Set;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 1:36 PM
 */
public interface MapService {

    /**
     *
     * Retrieves the set of ICasaMaps in the server.
     * @return
     */
    Set<ICasaMap> getMaps();

    /**
     * Check the existence of a map.
     * @param mapId
     * @return
     */
    boolean contains(String mapId);

    /**
     * Get a specific iCasaMap information
     * @param mapId
     * @return
     */
    ICasaMap getMap(String mapId);

    /**
     * Get the path where the maps are
     * @return
     */
    String getLocation();

    String addMap(ICasaMap map);

    ICasaMap updateMap(String id, String name, String description, String gatewayURL, String libs);

}
