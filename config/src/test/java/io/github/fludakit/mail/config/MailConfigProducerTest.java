package io.github.fludakit.mail.config;

import io.github.fludakit.mail.MailConfig;
import io.smallrye.config.inject.ConfigExtension;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests CDI producers for MailConfig from MicroProfile Config properties.
 * Uses microprofile-config.properties in test resources.
 */
@ExtendWith(WeldJunit5Extension.class)
class MailConfigProducerTest {

    @WeldSetup
    WeldInitiator setup = WeldInitiator
            .from(
                    ConfigExtension.class,
                    MailConfigProducer.class, MailProperties.class
            )
            .build();

    @Inject
    MailConfig mailConfig;

    @Test
    void mailConfigIsProduced() {
        assertNotNull(mailConfig);
    }

    @Test
    void mailConfigFromProperties() {
        // Verify that the config values are read from microprofile-config.properties
        assertEquals("smtp.test.com", mailConfig.getHost());
        assertEquals(587, mailConfig.getPort());
        assertEquals("smtp", mailConfig.getProtocol());
        assertTrue(mailConfig.isAuthEnabled());
        assertTrue(mailConfig.isStarttls());
        assertEquals("testuser", mailConfig.getUsername());
        assertEquals("testpass", mailConfig.getPassword());
        assertEquals("noreply@test.com", mailConfig.getFrom());
    }
}
