package fr.liglab.adele.icasa.notification;

/**
 * Created by aygalinc on 10/04/15.
 */

import org.apache.felix.ipojo.annotations.*;
import org.ow2.chameleon.mail.Mail;
import org.ow2.chameleon.mail.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides method to send medical report by mail !
 *
 */
@Component
@Instantiate
@Provides
public class NotificationServiceImpl implements NotificationService {

    //TODO : Default Adress
    private String currentAddress = "";

    private final Object m_lock = new Object();

    private  final Logger m_logger = LoggerFactory
            .getLogger(NotificationServiceImpl.class);

    @Requires
    MailSenderService mailer;

    @Validate
    public void start() {
        m_logger.info("NotificationServiceImpl Start ");
    }

    @Invalidate
    public void stop() {
        m_logger.info("NotificationServiceImpl Stop ");
    }

    @Override
    public void sendNotification(String subject, String body) {
        try {
            synchronized (m_lock) {
                mailer.send(new Mail().to(currentAddress)
                        .subject(subject)
                        .body(body));
            }
        } catch (Exception e) {
            m_logger.error(e.toString());
        }
    }

    @Override
    public void setUserAddress(String address) {
        synchronized (m_lock) {
            currentAddress = address;
        }
    }

    @Override
    public String getUserAddress() {
        return currentAddress;
    }


}