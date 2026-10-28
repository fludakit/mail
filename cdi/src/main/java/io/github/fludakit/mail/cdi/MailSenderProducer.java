package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.JakartaMailSender;
import io.github.fludakit.mail.MailConfig;
import io.github.fludakit.mail.MailSender;
import io.github.fludakit.mail.MailSessionFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.mail.Session;

/**
 * CDI producer for MailSender beans.
 *
 * <p>The Jakarta Mail {@link Session} is resolved in order of preference: a {@link MailConfig}
 * bean (e.g. produced by the optional {@code config} module) is converted into a session here;
 * otherwise an existing {@link Session} bean is used. When neither is present, production fails
 * fast with {@link IllegalStateException} rather than silently falling back to an
 * unauthenticated localhost:25 session that would only fail at first send.</p>
 */
@ApplicationScoped
public class MailSenderProducer {

    @Inject
    Instance<MailConfig> mailConfigInstance;

    @Inject
    @JakartaMail
    Instance<Session> qualifiedSessionInstance;

    @Inject
    Instance<Session> existingSessionInstance;

    @Produces
    @ApplicationScoped
    public MailSender produceMailSender() {
        Session session = resolveSession();
        return new JakartaMailSender(session);
    }

    private Session resolveSession() {
        // If MailConfig is available, create Session from it
        if (!mailConfigInstance.isUnsatisfied()) {
            MailConfig config = mailConfigInstance.get();
            return MailSessionFactory.from(config);
        }

        // Session with @JakartaMail
        if(!qualifiedSessionInstance.isUnsatisfied()) {
            return qualifiedSessionInstance.get();
        }

        // Otherwise, check if a Session already exists
        if (!existingSessionInstance.isUnsatisfied()) {
            return existingSessionInstance.get();
        }

        throw new IllegalStateException(
                "No Jakarta Mail Session available: provide a MailConfig bean (e.g. add the "
                        + "fluda-mail-config module with fluda.mail.* properties) or produce a "
                        + "jakarta.mail.Session bean");
    }
}
