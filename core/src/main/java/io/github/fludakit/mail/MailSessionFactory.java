package io.github.fludakit.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Properties;

/**
 * Creates Jakarta Mail {@link Session}s from a {@link MailConfig}.
 *
 * <p>This is the single owner of the config-to-Session conversion: the mail properties plus,
 * when authentication is enabled, the {@link Authenticator} carrying the configured credentials.
 * Use it instead of {@code Session.getInstance(config.toJakartaMailProperties())} — a session
 * built from the properties alone cannot authenticate, because the credentials only reach the
 * session through the authenticator.</p>
 */
public final class MailSessionFactory {

    private MailSessionFactory() {
    }

    /**
     * Creates a Session from the configuration, wiring a password authenticator when
     * {@link MailConfig#isAuthEnabled()} is set.
     */
    public static Session from(MailConfig config) {
        Properties props = config.toJakartaMailProperties();
        if (config.isAuthEnabled()) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUsername(), config.getPassword());
                }
            });
        }
        return Session.getInstance(props);
    }
}
