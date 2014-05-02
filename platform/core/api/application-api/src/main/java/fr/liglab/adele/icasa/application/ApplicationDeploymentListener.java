package fr.liglab.adele.icasa.application;

/**
 * Created by aygalinc on 02/05/14.
 */
public interface ApplicationDeploymentListener {

    /**
     *
     * @param applicationId : The Id of the application which is added
     */
    public void applicationAdded(String applicationId);

    /**
     *
     * @param applicationId : The Id of the application which is removed
     */
    public void applicationRemoved(String applicationId);

    /**
     *
     * @param applicationId : The Id of the application which is modified
     */
    public void applicationModified(String applicationId);
}
