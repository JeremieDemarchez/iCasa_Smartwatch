/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research Group
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
package fr.liglab.adele.icasa.bridge.test.app1;

import fr.liglab.adele.m2mappbuilder.application.ApplicationProvider;
import fr.liglab.adele.m2mappbuilder.common.ProgressMonitor;
import org.osgi.framework.BundleContext;

public class App1 implements ApplicationProvider {
	
	private BundleContext _context;

	public App1(BundleContext context) {
		_context = context;
	}

	public void start(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public void stop(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public void resume(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public void pause(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public BundleContext getBundleContext() {
		return _context;
	}

}
