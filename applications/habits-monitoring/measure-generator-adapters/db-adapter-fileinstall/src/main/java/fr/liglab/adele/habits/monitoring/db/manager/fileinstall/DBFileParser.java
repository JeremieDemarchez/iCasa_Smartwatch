/**
 * 
 */
package fr.liglab.adele.habits.monitoring.db.manager.fileinstall;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import fr.liglab.adele.habits.monitoring.db.manager.fileinstall.model.DPInfos;
import fr.liglab.adele.habits.monitoring.db.manager.fileinstall.model.DPInfosHandler;
import org.xml.sax.SAXException;


/**
 * Parser for .db xml configuration files. 
 * @author Kettani Mehdi
 *
 */
public class DBFileParser {

	
	public static Set<DPInfos> parse(File dbFile) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
//		SAXReader reader = new SAXReader();
		
		SAXParserFactory fabrique = SAXParserFactory.newInstance();
		SAXParser parseur = fabrique.newSAXParser();
		DPInfosHandler handler = new DPInfosHandler();
		parseur.parse(dbFile, handler);
		
//		Document doc = reader.read(dbFile);
//		Set<DPInfos> dps = new HashSet<DPInfos>();
		
//		List<Node> list = doc.selectNodes( "//db/id" );
		
//		for (Node node : list){
//			DPInfos infos = new DPInfos();
//			Element elem = (Element) node;
//			infos.setUrl(elem.attributeValue("url"));
//			infos.setName(elem.attributeValue("name"));
//			for (Element interf : (List<Element>)elem.elements()){
//				if ("interface".equals(interf.getName())){
//					infos.getInterfaces().add(Class.forName(interf.getText()));
//				}
//			}
//			dps.add(infos);
//		}
		return handler.getListeInfos();
	}

	public static void unload(File dbFile) {
		// TODO Auto-generated method stub
		
	}

}
