package com.RPMS.controller.ContactStrategy;

import com.RPMS.controller.LoginController;
import com.RPMS.model.entity.Property;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.util.Properties;

public class EmailStrategy implements ContactStrategy {
    /**
     * Static instance of EmailController
     */
    private static EmailStrategy obj;
    /**
     * SMTP server URL and port
     */
    private String SMTPHostServer = "smtp.gmail.com";
    private String SMTPServerPort = "587";
    /**
     * RPMS email
     */
    private String RPMSEmail = "RPMS.System@gmail.com";
    /**
     * Password for RPMS email
     */
    private String RPMSPassword = "rUfpej-gehzav-9vorsi";
    /**
     * Mailing properties
     */
    private Properties prop;

    /**
     * Email strategy constructor
     */
    private EmailStrategy() {
        prop = new Properties();
        setServerProperties();
    }

    /**
     * getInstance method for the Singleton pattern
     *
     * @return instance of EmailController
     */
    public static EmailStrategy getInstance() {
        if (obj == null) {
            obj = new EmailStrategy();
        }
        return obj;
    }

    /**
     * Email contact strategy
     * @param message
     * @param property
     */
    @Override
    public void contactLandlord(String message, Property property) {
        sendEmail(message, property);
    }

    /**
     * Sends email from logged in user to landlord of selected property
     *
     * @param emailContents
     * @param property
     */
    public void sendEmail(String emailContents, Property property) {
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(RPMSEmail, RPMSPassword);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setReplyTo(InternetAddress.parse(getUserEmail()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(getLandlordEmail(property))
            );
            message.setSubject("With regards to property at " + property.getAddress());
            message.setText(emailContents);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets environment variables
     */
    private void setServerProperties() {
        prop.put("mail.smtp.host", SMTPHostServer);
        prop.put("mail.smtp.port", SMTPServerPort);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
    }

    /**
     * Gets email of user currently logged in
     *
     * @return
     */
    public String getUserEmail() {
        LoginController loginCont = LoginController.getInstance();
        if (loginCont.isLoggedIn()) {
            return loginCont.getAccount().getEmail().getEmailAddress();
        }
        return null;
    }

    /**
     * Gets the email of the landlord of the property
     *
     * @param property
     * @return
     */
    public String getLandlordEmail(Property property) {
        return property.getLandlord().getEmail().getEmailAddress();
    }
}
