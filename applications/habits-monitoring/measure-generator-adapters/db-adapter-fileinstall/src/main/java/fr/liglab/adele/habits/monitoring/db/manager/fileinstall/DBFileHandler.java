/**
 * 
 */
package fr.liglab.adele.habits.monitoring.db.manager.fileinstall;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.habits.monitoring.autonomic.manager.dbadapter.IDBAdapter;
import fr.liglab.adele.habits.monitoring.db.manager.fileinstall.model.DPInfos;

/**
 * Handler for .db files. 
 * @author Kettani Mehdi
 */
@Component(name = "DBFileHandler")
@Instantiate(name = "DBFileHandler-1")
@Provides(specifications={fr.liglab.adele.habits.monitoring.autonomic.manager.dbadapter.IDBAdapter.class,org.apache.felix.fileinstall.ArtifactInstaller.class})
public class DBFileHandler  implements ArtifactInstaller, IDBAdapter {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DBFileHandler.class);
//	private DBFileParser parser;
	private Set<DPInfos> infos = new HashSet<DPInfos>();

	@Override
	public boolean canHandle(File dbFile) {
		if (dbFile.getName().endsWith(".hma")){
			return true;
		}
		return false;
	}

	@Override
	public void install(File dbFile) throws Exception {
		logger.info("DBFile handler got a new file : " + dbFile.getName());
		this.infos = DBFileParser.parse(dbFile);
	}

	@Override
	public void update(File dbFile) throws Exception {
		DBFileParser.unload(dbFile);
	}

	@Override
	public void uninstall(File dbFile) throws Exception {
		logger.info("DBFile handler got an uninstall on file : " + dbFile.getName());
//		parser.unload(dbFile);
//		parser.load(dbFile);
	}

	@Override
	public String getDeviceAdapterId(Set<String> interfaceSet) {
		
		String deviceAdapterId = null;
		Iterator<DPInfos> iterator = infos.iterator();
		while (iterator.hasNext()){
			DPInfos currentDPInfos = iterator.next();
			if (currentDPInfos.getInterfaces().containsAll(interfaceSet)){
				deviceAdapterId = currentDPInfos.getName();
				break;
			}
		}
		return deviceAdapterId;
	}

	@Override
	public Object getDeviceAdapterUrl(String dpId) {

		String deviceAdapterUrl = null;
		Iterator<DPInfos> iterator = infos.iterator();
		while (iterator.hasNext()){
			DPInfos currentDPInfos = iterator.next();
			if (currentDPInfos.getName().equals(dpId)){
				deviceAdapterUrl = currentDPInfos.getUrl();
				break;
			}
		}
		return deviceAdapterUrl;
	}
}
