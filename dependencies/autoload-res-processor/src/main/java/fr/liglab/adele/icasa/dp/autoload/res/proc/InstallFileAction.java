package fr.liglab.adele.icasa.dp.autoload.res.proc;

import java.io.File;

public class InstallFileAction {

	private File _targetFile;
	private File _tempFile;
	private String _resName;
	private boolean _isToBeInstalled;
	private boolean _failIfError = true;
	
	
	public final File getTargetFile() {
		return _targetFile;
	}
	
	public final void setTargetFile(File targetFile) {
		this._targetFile = targetFile;
	}
	
	public final File getTempFile() {
		return _tempFile;
	}
	
	public final void setTempFile(File tempFile) {
		this._tempFile = tempFile;
	}
	
	public final String getResourceName() {
		return _resName;
	}
	
	public InstallFileAction(String resName) {
		_resName = resName;
	}

	public boolean isToBeInstalled() {
		return _isToBeInstalled;
	}
	
	public boolean isToBeUninstalled() {
		return !_isToBeInstalled;
	}

	public void setIsToBeInstalled(boolean isToBeInstalled) {
		_isToBeInstalled = isToBeInstalled;
	}

	public void setFailIfError(boolean failIfError) {
		_failIfError = failIfError;
	}

	public boolean isFailIfError() {
		return _failIfError;
	}
	
}
