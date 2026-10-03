package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.JakartaMailSender;
import io.github.fludakit.mail.MailConfig;
import io.github.fludakit.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

/**
 * CDI producer for MailSender beans.
 *
 * <p>This producer creates a MailSender bean from the available Session bean.
 * The Session is produced by {@link MailSessionProducer}.</p>
 */
@ApplicationScoped
public class MailSenderProducer {

    @Inject
    Instance<MailConfig> mailConfigInstance;

    @Inject
    Instance<Session> existingSessionInstance;

    @Produces
    @ApplicationScoped
    public MailSender produceMailSender() {
        Session session = resolveSession();
        return new JakartaMailSender(session);
    }

    public Session resolveSession() {
        // If MailConfig is available, create Session from it
        if (!mailConfigInstance.isUnsatisfied()) {
            MailConfig config = mailConfigInstance.get();
            return createSessionFromConfig(config);
        }

        // Otherwise, check if a Session already exists
        if (!existingSessionInstance.isUnsatisfied()) {
            return existingSessionInstance.get();
        }

        // Fallback: create a default session (localhost:25)
        return Session.getInstance(new java.util.Properties());
    }

    private Session createSessionFromConfig(MailConfig config) {
        java.util.Properties props = config.toJakartaMailProperties();

        if (config.isAuthEnabled()) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            config.getUsername(),
                            config.getPassword()
                    );
                }
            });
        } else {
            return Session.getInstance(props);
        }
    }
}
