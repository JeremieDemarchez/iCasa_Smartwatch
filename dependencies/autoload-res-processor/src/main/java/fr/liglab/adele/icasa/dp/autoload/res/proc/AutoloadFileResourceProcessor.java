package fr.liglab.adele.icasa.dp.autoload.res.proc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.deploymentadmin.spi.ResourceProcessorException;

/**
 * This component copy files embedded in deployment packages to the felix autoload directory.
 * 
 * 
 * @author Thomas Leveque
 *
 */
@Instantiate(name="autoload-file-res-proc-1")
@Component(name="autoload-file-res-proc")
@Provides(properties={ 
		@StaticServiceProperty(name="service.pid", value="org.osgi.deployment.rp.autoload",mandatory=false, type="java.lang.String") })
public class AutoloadFileResourceProcessor implements ResourceProcessor, ArtifactInstaller {
	
	private BundleContext _context;
	private DeploymentSession _session = null;
	
	private File _autoloadDir;
	private Map<String, InstallFileAction> _toBeManaged = new ConcurrentHashMap<String, InstallFileAction>();
	private PersistencyManager _persistencyManager; 
	
	@Requires
	private DeploymentAdmin _deployAdmin;

	private AutoloadFileResourceProcessor(BundleContext context) {
		_context = context;
	}
	
	@Validate
	public void start() throws IOException {
		File root = _context.getDataFile("");
        if (root == null) {
            throw new IOException("No file system support");
        }
        File persistRoot = new File(root, "autoloadResProc/");
        persistRoot.mkdir();
        _persistencyManager = new PersistencyManager(persistRoot);
	}
	
	@Invalidate
	public void stop() throws IOException {
		if (_persistencyManager != null) {
			_persistencyManager.deleteAll();
			_persistencyManager = null;
		}
	}

	@Override
	public void begin(DeploymentSession session) {
		_session = session;
	}

	@Override
	public void process(String name, InputStream stream)
			throws ResourceProcessorException {
		if (_session == null) {
	          throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "Can not process resource without a Deployment Session");
	     }
		
		File targetFile = getTargetFile(name);
		if (targetFile == null)
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot discover file install directory");
		
		InstallFileAction installAction = new InstallFileAction(name);
		installAction.setIsToBeInstalled(true);
		installAction.setTargetFile(targetFile);
		try {
			_persistencyManager.store(name, stream);
		} catch (IOException e) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot persist file content");
		}
		
		_toBeManaged.put(name, installAction);
	}

	private File getTargetFile(String name) {
		int lastSeparator = name.lastIndexOf(File.separator);
		String fileName = (lastSeparator == -1) ? name : name.substring(lastSeparator + 1);
		
		if (_autoloadDir == null) {
			return null;
		}
		
		File targetFile = new File(_autoloadDir, fileName);
		return targetFile;
	}

	@Override
	public void dropped(String name) throws ResourceProcessorException {
		if (_session == null) {
            throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "Can not process resource without a Deployment Session");
        }
		
		File targetFile = getTargetFile(name);
		if (targetFile == null)
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot discover file install directory");
		
		InstallFileAction installAction = new InstallFileAction(name);
		installAction.setIsToBeInstalled(false);
		installAction.setTargetFile(targetFile);
		
		_toBeManaged.put(name, installAction);
	}

	@Override
	public void dropAllResources() throws ResourceProcessorException {
		if (_session == null) {
            throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "Can not drop all resources without a Deployment Session");
        }
		
		for (String resName : _session.getTargetDeploymentPackage().getResources()) {
			File targetFile = getTargetFile(resName);
			if (targetFile == null)
				throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot discover file install directory");
			
			InstallFileAction installAction = new InstallFileAction(resName);
			installAction.setIsToBeInstalled(false);
			installAction.setTargetFile(targetFile);
			installAction.setFailIfError(false);
			
			_toBeManaged.put(resName, installAction);
		}
	}

	@Override
	public void prepare() throws ResourceProcessorException {
		if (_session == null) {
            throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "Can not process resource without a Deployment Session");
        }
		
		for (InstallFileAction action : _toBeManaged.values()) {
			if (action.isToBeUninstalled())
				continue;
			
			File targetFile = null;
			try {
				InputStream is = _persistencyManager.load(action.getResourceName());
				targetFile = action.getTargetFile();
				if (!targetFile.exists())
					FileUtil.copyContent(is, targetFile);
			} catch (IOException e) {
				throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot copy file " + targetFile.getName() + " into file install directory");
			}
		}
		
		final String dpName = _session.getTargetDeploymentPackage().getName();
		for (InstallFileAction action : _toBeManaged.values()) {
			if (action.isToBeInstalled())
				continue; //already performed
			
			boolean mustBeDeleted = true;
			for (DeploymentPackage dp : _deployAdmin.listDeploymentPackages()) {
				if (dp.getName().equals(dpName))
					continue;
				
				Object resProc = dp.getResourceProcessor(action.getResourceName());
				if (resProc != null) {
					mustBeDeleted = false;
					break;
				}
			}
			if (!mustBeDeleted)
				continue;
			
			File targetFile = action.getTargetFile();
			boolean deleted = targetFile.delete();
			if (!deleted && action.isFailIfError())
				throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR, "cannot delete file " + targetFile.getName() + " from file install directory");
		}
	}

	@Override
	public void commit() {
		_session = null;
		clearModifsToApply();
	}

	private void clearModifsToApply() {
		_toBeManaged.clear();
		try {
			_persistencyManager.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
		_session = null;
		clearModifsToApply();
	}

	@Override
	public void cancel() {
		rollback();
	}

	/*
	 * Use these methods to get the file install directory
	 */
	@Override
	public boolean canHandle(File file) {
		if ((_autoloadDir == null) && file.getName().endsWith(".dp")) {
			_autoloadDir = file.getParentFile();
		}
		return false;
	}

	@Override
	public void install(File artifact) throws Exception {
		// never called
	}

	@Override
	public void update(File artifact) throws Exception {
		// never called
	}

	@Override
	public void uninstall(File artifact) throws Exception {
		// never called
	}

}
