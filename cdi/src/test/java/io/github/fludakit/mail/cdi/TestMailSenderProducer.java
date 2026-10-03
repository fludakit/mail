package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.logging.Logger;

/**
 * Provides a mock MailSender for CDI unit tests that logs sent messages.
 */
@ApplicationScoped
public class TestMailSenderProducer {

    private static final Logger LOGGER = Logger.getLogger(TestMailSenderProducer.class.getName());

    @Produces
    @ApplicationScoped
    public MailSender mailSender() {
        return message -> LOGGER.info("Mock MailSender invoked with subject: " + message.getSubject());
    }
}
