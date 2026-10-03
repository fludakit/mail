package io.github.fludakit.mail;

/**
 * Core mail sender interface for sending email messages.
 * This is the primary abstraction that can be implemented by different mail providers.
 */
public interface MailSender {
    
    /**
     * Sends an email message.
     *
     * @param message the message to send
     * @throws MailException if the message cannot be sent
     */
    void send(MailMessage message) throws MailException;
}
