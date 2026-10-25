package io.github.fludakit.mail;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Direct coverage of {@link MailConfig#toJakartaMailProperties()} - the property mapping the
 * plain-Java-SE usage and {@link MailSessionFactory} both rely on.
 */
class MailConfigTest {

    @Test
    void defaultsMapToSmtpLocalhostWithoutAuth() {
        Properties props = new MailConfig().toJakartaMailProperties();

        assertEquals("localhost", props.getProperty(MailConfig.SMTP_HOST));
        assertEquals("25", props.getProperty(MailConfig.SMTP_PORT));
        assertNull(props.getProperty(MailConfig.SMTP_AUTH));
        assertNull(props.getProperty(MailConfig.SMTP_STARTTLS));
        assertNull(props.getProperty(MailConfig.SMTP_FROM));
    }

    @Test
    void configuredFieldsMapToSmtpProperties() {
        MailConfig config = new MailConfig();
        config.setHost("smtp.test.com");
        config.setPort(587);
        config.setAuthEnabled(true);
        config.setStarttls(true);
        config.setFrom("noreply@test.com");

        Properties props = config.toJakartaMailProperties();

        assertEquals("smtp.test.com", props.getProperty(MailConfig.SMTP_HOST));
        assertEquals("587", props.getProperty(MailConfig.SMTP_PORT));
        assertEquals("true", props.getProperty(MailConfig.SMTP_AUTH));
        assertEquals("true", props.getProperty(MailConfig.SMTP_STARTTLS));
        assertEquals("noreply@test.com", props.getProperty(MailConfig.SMTP_FROM));
    }

    @Test
    void extraPropertiesAreMergedIntoTheResult() {
        MailConfig config = new MailConfig();
        config.addExtraProperty("mail.smtp.connectiontimeout", "5000");
        config.addExtraProperty("mail.smtp.writetimeout", "4000");

        Properties props = config.toJakartaMailProperties();

        assertEquals("5000", props.getProperty("mail.smtp.connectiontimeout"));
        assertEquals("4000", props.getProperty("mail.smtp.writetimeout"));
        assertEquals("localhost", props.getProperty(MailConfig.SMTP_HOST), "built-ins still present");
    }
}
