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
package fr.liglab.adele.icasa.device.handler.test;

import static org.junit.Assert.assertEquals;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import test.component.handler.ComponentOnlyRequirePojo;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class EnableFlagDeviceHandlerTest extends BaseDeviceHandlerTest {

    @Test
    public void noPlatformComponentAccesingDeviceTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequirePojo");
        
        // Component using a requires iPOJO standard
        ComponentOnlyRequirePojo pojo = (ComponentOnlyRequirePojo) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        
        assertEquals(ComponentInstance.INVALID, manager.getState());               
        
    }
    
    @Override
    protected Boolean getAccessPolicyPropertyValue() {
        return Boolean.FALSE;
    }
    

}
