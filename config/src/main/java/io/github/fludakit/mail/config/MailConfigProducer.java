package io.github.fludakit.mail.config;

import io.github.fludakit.mail.MailConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperties;

/**
 * Produces the {@code @ApplicationScoped} {@link MailConfig} bean from the {@code mail.*}
 * MicroProfile Config properties. This is the optional integration point consumed by the {@code cdi}
 * module.
 */
@ApplicationScoped
public class MailConfigProducer {

    @Inject @ConfigProperties
    private MailProperties properties;

    @Produces
    @ApplicationScoped
    public MailConfig produce() {
        MailConfig config = new MailConfig();
        config.setHost(properties.host());
        config.setPort(properties.port());
        config.setUsername(properties.username());
        config.setPassword(properties.password());
        config.setAuthEnabled(properties.authEnabled());
        config.setProtocol(properties.protocol());
        config.setStarttls(properties.starttls());
        config.setFrom(properties.from());
        
        return config;
    }
}
