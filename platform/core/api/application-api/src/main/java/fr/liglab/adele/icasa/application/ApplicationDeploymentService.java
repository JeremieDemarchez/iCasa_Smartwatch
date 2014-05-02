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
package fr.liglab.adele.icasa.application;

import java.util.Set;

/**
 * Created by aygalinc on 02/05/14.
 */
public interface ApplicationDeploymentService {

    /**
     *
     * @return A set of the application present on the plateform
     */
    public Set<String> getApplicationsIds();

    /**
     *
     * @param ApplicationId : the application unique Id (concatenation of name and version)
     * @return the application version
     */
    public String getApplicationVersion(String ApplicationId);

    /**
     *
     * @param ApplicationId : the application unique Id (concatenation of name and version)
     * @return the application Version
     */
    public String getApplicationName(String ApplicationId);

    /**
     *
     * @param ApplicationId : the application unique Id (concatenation of name and version)
     * @return the name of the application category
     */
    public String getApplicationCategory(String ApplicationId);

    /**
     *
     * @param ApplicationId : the application unique Id (concatenation of name and version)
     * @return the name of the application vendor
     */
    public String getApplicationVendor(String ApplicationId);

    /**
     *
     * @param ApplicationId : the application unique Id (concatenation of name and version)
     * @return A set of bundle ids  involve in the application
     */
    public Set<Long> getBundlesIdsFromApplicationId(String ApplicationId);

    /**
     *
     * @param BundleId : the bundle Id
     * @return : A set of Application Ids which use the bundle
     */
    public Set<String> getApplicationIdsFromBundleId(long BundleId);

    /**
     *
     * @param listener
     */
    public void addlistener(ApplicationDeploymentListener listener);

    /**
     *
     * @param listener
     */
    public void removeListener(ApplicationDeploymentListener listener);

}
