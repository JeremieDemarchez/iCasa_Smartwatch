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
package fr.liglab.adele.icasa;

public interface Constants {

    /**
     * Property name to disable access policy.
     */
	public static final String DISABLE_ACCESS_POLICY_PROPERTY = "icasa.disable.access.policy";

    /**
     * Logger name to trace general messages for icasa.
     */
    public static final String ICASA_LOG = "icasa.platform";
    /**
     * Logger name to trace messages for icasa when performing remote operations.
     */
    public static final String ICASA_LOG_REMOTE = "icasa.platform.remote";
    /**
     * Logger name to trace messages for icasa when performing device operations..
     */
    public static final String ICASA_LOG_DEVICE = "icasa.platform.devices";
	
}
