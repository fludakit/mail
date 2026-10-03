package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailMessage;
import io.github.fludakit.mail.MailSender;
import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

/**
 * CDI decorator that intercepts MailSender.send() calls and tracks messages
 * in the MailTrackingRegistry for test verification.
 */
@Decorator
@Priority(100)
@Dependent
public class MailSenderDecorator implements MailSender {

    @Delegate
    @Inject
    private MailSender delegate;

    @Inject
    private MailTrackingRegistry registry;

    @Override
    public void send(MailMessage message) {
        // Track the message before delegating
        registry.track(message);
        // Delegate to the actual sender
        delegate.send(message);
    }
}
