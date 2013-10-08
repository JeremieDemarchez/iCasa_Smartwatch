/**
 * 
 */
package fr.liglab.adele.habits.monitoring.db.manager.fileinstall;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.habits.monitoring.autonomic.manager.dbadapter.IDBAdapter;
import fr.liglab.adele.habits.monitoring.autonomic.manager.listeners.DPInfos;
import fr.liglab.adele.habits.monitoring.autonomic.manager.listeners.DPInfosListener;

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
	private final EventListenerList listeners = new EventListenerList();

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
		Set<DPInfos> parsedInfos = DBFileParser.parse(dbFile);
		if(this.infos.size() > 0){
			infos.addAll(parsedInfos);
		} else {
			this.infos = parsedInfos;
		}
		fireNewDPInfosAdded(parsedInfos);
	}

	/**
	 * Fire a new event related to the arrival of new dps to dp infos listeners.
	 * @param parsedInfos
	 */
	private void fireNewDPInfosAdded(Set<DPInfos> parsedInfos) {
		
		for (DPInfos infos : parsedInfos){
			for (DPInfosListener listener : getDPInfosListeners()){
				listener.DPInfosAdded(infos);
			}
		}
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
	
	public void addDPInfosListener(DPInfosListener listener){
		logger.info("dp infos listener added");
		this.listeners.add(DPInfosListener.class, listener);
	}
	
	public void removeDPInfosListener(DPInfosListener listener){
		logger.info("dp infos listener removed");
		this.listeners.remove(DPInfosListener.class, listener);
	}
	
	public DPInfosListener[] getDPInfosListeners() {
        return listeners.getListeners(DPInfosListener.class);
    }
}
