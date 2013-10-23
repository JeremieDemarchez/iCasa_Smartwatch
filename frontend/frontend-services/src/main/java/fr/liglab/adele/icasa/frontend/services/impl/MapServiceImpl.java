package fr.liglab.adele.icasa.frontend.services.impl;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.common.xml.utils.XMLUtils;
import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.frontend.services.utils.AbstractXMLParser;
import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.impl.iCasaREST;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.security.cert.CollectionCertStoreParameters;
import java.util.*;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 1:33 PM
 */
@Component(name = "maps-frontend-service")
@Instantiate(name = "maps-frontend-service-0")
@Provides
public class MapServiceImpl extends AbstractXMLParser implements MapService {

    @Property(name="location", value = "maps")
    private String location;

    private Map<String, ICasaMap> icasaMaps = new HashMap();

    private Object lockObject = new Object();

    @Validate
    public void validate(){
        loadMaps();
    }

    @Override
    public Set<ICasaMap> getMaps() {
        HashSet returnSet = new HashSet();
        loadMaps();
        synchronized (lockObject){
            for(String id: icasaMaps.keySet()){
                returnSet.add(icasaMaps.get(id));
            }
        }
        return returnSet;
    }

    @Override
    public boolean contains(String mapId) {
        synchronized (lockObject){
            return icasaMaps.containsKey(mapId);
        }
    }

    @Override
    public ICasaMap getMap(String mapId) {
        synchronized (lockObject){
            return icasaMaps.get(mapId);
        }
    }

    public void loadMaps(){
        synchronized (lockObject){
            List<Map> maps = loadFile();
            icasaMaps.clear();
            for (Map map: maps){
                ICasaMap iCasaMap = new ICasaMap(map);
                icasaMaps.put(iCasaMap.getId(), iCasaMap);
            }
        }
    }

    public void saveMaps(){
        writeFile();
    }

    @Override
    public List getInfo() {
        Set<ICasaMap> maps = getMaps();
        List<Map> mapsInList = new ArrayList();
        for (ICasaMap map : maps) {
            mapsInList.add(map.toMap());
        }
        return mapsInList;
    }



    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getFileName() {
        return "maps";
    }



}
