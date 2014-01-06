/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.core.activators.DirectoryMonitor;

import java.io.File;

/**
 * User: garciai@imag.fr
 * Date: 12/10/13
 * Time: 4:13 PM
 */
@Component
@Instantiate
public class ScriptMonitor {

    private final BundleContext context;

    private DirectoryMonitor monitor;

    private File file;

    public ScriptMonitor(BundleContext context){
        this.context = context;
        file = new File(ScriptExecutorImpl.SCRIPTS_DIRECTORY);
    }

    @Validate
    public void start() {
        monitor = new DirectoryMonitor(file, 2000);
        try {
            monitor.start(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Invalidate
    public void stop(){
        try {
            monitor.stop(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
