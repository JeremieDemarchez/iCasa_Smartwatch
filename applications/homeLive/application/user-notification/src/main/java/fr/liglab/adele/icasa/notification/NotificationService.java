package fr.liglab.adele.icasa.notification;

/**
 * Created by aygalinc on 10/04/15.
 */
public interface NotificationService {

    void sendNotification(String subject, String body);

    void setUserAddress(String address);

    String getUserAddress();
}
