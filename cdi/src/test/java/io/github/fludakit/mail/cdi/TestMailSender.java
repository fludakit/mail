package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailMessage;
import io.github.fludakit.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;

import java.util.logging.Logger;

/**
 * Provides a mock MailSender for CDI unit tests that logs sent messages.
 */
@Dependent
public class TestMailSender implements MailSender {

    private static final Logger LOGGER = Logger.getLogger(TestMailSender.class.getName());

    public void send(MailMessage message) {
         LOGGER.info("Mock MailSender invoked with subject: " + message.getSubject());
    }
}
