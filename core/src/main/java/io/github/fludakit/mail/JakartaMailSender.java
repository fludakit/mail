package io.github.fludakit.mail;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * Jakarta Mail implementation of MailSender using SMTP.
 * This is a pure Java SE implementation with no CDI dependencies.
 * 
 * <p>Usage example:</p>
 * <pre>
 * Session session = // obtain or create Jakarta Mail Session
 * MailSender sender = new JakartaMailSender(session);
 * MailMessage message = new MailMessage();
 * message.setFrom("sender@example.com");
 * message.addTo("recipient@example.com");
 * message.setSubject("Hello");
 * message.setHtmlBody("&lt;p&gt;World&lt;/p&gt;");
 * sender.send(message);
 * </pre>
 */
public class JakartaMailSender implements MailSender {

    private final Session session;

    /**
     * Creates a new JakartaMailSender with the given Session.
     *
     * @param session the Jakarta Mail Session to use for sending emails
     */
    public JakartaMailSender(Session session) {
        this.session = session;
    }

    @Override
    public void send(MailMessage emailMessage) {
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(emailMessage.getFrom()));
            for (String recipient : emailMessage.getTo()) {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }
            mimeMessage.setSubject(emailMessage.getSubject(), "UTF-8");

            if (emailMessage.getAttachments().isEmpty()) {
                mimeMessage.setContent(emailMessage.getHtmlBody(), "text/html; charset=UTF-8");
            } else {
                MimeMultipart multipart = new MimeMultipart("mixed");
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(emailMessage.getHtmlBody(), "text/html; charset=UTF-8");
                multipart.addBodyPart(bodyPart);

                for (MailAttachment attachment : emailMessage.getAttachments()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new jakarta.activation.DataHandler(attachment.toDataSource()));
                    attachmentPart.setFileName(attachment.getFilename());
                    multipart.addBodyPart(attachmentPart);
                }
                mimeMessage.setContent(multipart);
            }
            Transport.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Failed to send email via SMTP", e);
        }
    }
}
