/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package fr.liglab.adele.habits.autonomic.manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.Node;
import fr.liglab.adele.cilia.builder.Builder;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;

/**
 * 
 * Live update ,
 * 
 * @author Denis Morand
 * 
 */

public class LiveUpdateManager implements ArtifactInstaller {
	private static Logger logger = LoggerFactory.getLogger(LiveUpdateManager.class);
	private CiliaContext ciliaContext;
	private Properties config;

	public void start() {
		logger.info("Service {} started ", this.getClass().getSimpleName());
	}

	public void stop() {
		logger.info("Service {} stopped ", this.getClass().getSimpleName());

	}

	@Override
	public boolean canHandle(File file) {
		return file.getName().equalsIgnoreCase("update.cfg");
	}

	@Override
	public void install(File file) throws Exception {
		try {
			config.load(new URL(file.getAbsolutePath()).openStream());
		} catch (MalformedURLException e) {
			logger.error("Invalid URL");
		} catch (IOException e) {
		}
	}

	@Override
	public void uninstall(File arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(File arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void executePatch(String oldComponent, String newComponent) {
		Node[] old,upgrade ;
		try {
			old=ciliaContext.getApplicationRuntime().nodeByType(oldComponent) ;
			upgrade = ciliaContext.getApplicationRuntime().nodeByType(newComponent);
			Builder builder = ciliaContext.getBuilder();
		} catch (CiliaIllegalParameterException e) {
		}
	}
}
