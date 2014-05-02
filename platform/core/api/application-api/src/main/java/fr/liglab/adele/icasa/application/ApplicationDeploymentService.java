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
     * @return A set of bundle ids  involve in the application
     */
    public Set<Long> getBundlesIdsFromApplicationId(String ApplicationId);

    /**
     *
     * @param BundleId : the bundle Id
     * @return : A set of Application Ids which use the bundle
     */
    public Set<String> getApplicationIdsFromBundleId(long BundleId);

}
