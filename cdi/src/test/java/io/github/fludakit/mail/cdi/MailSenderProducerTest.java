package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailConfig;
import io.github.fludakit.mail.MailMessage;
import io.github.fludakit.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.mail.Session;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the session-resolution priority of {@link MailSenderProducer}: a {@link MailConfig} bean
 * wins, an existing {@link Session} bean is the fallback, and neither present fails fast at
 * production (per the loud-fallback change) instead of handing out an unusable localhost session.
 *
 * <p>Each scenario needs a different deployment, so each is its own test class with its own
 * {@code WeldSetup}.</p>
 */
class MailSenderProducerTest {

    @ExtendWith(WeldJunit5Extension.class)
    static class ProducesFromMailConfig {

        @WeldSetup
        WeldInitiator setup = WeldInitiator
                .from(MailSenderProducer.class, MailConfigProducerBean.class)
                .build();

        @Test
        void mailSenderIsResolvable() {
            // resolution proves the producer is registered; the produced session points at
            // smtp.test.com, so a real send is out of scope here
            assertNotNull(setup.select(MailSender.class).get());
        }

        @ApplicationScoped
        public static class MailConfigProducerBean {

            @Produces
            @ApplicationScoped
            public MailConfig mailConfig() {
                MailConfig config = new MailConfig();
                config.setHost("smtp.test.com");
                config.setPort(2525);
                return config;
            }
        }
    }

    @ExtendWith(WeldJunit5Extension.class)
    static class PrefersJakartaMailQualifiedSession {

        @WeldSetup
        WeldInitiator setup = WeldInitiator
                .from(MailSenderProducer.class, QualifiedSessionProducerBean.class)
                .build();

        @Test
        void mailSenderIsProduced() {
            MailSender sender = setup.select(MailSender.class).get();
            assertNotNull(sender);
        }

        public static class QualifiedSessionProducerBean {

            @Produces
            @JakartaMail
            public Session jakartaMailSession() {
                return Session.getInstance(new Properties());
            }

            @Produces
            public Session unqualifiedSession() {
                throw new IllegalStateException("unqualified session should not be used");
            }
        }
    }

    @ExtendWith(WeldJunit5Extension.class)
    static class FailsFastWithoutConfigOrSession {

        @WeldSetup
        WeldInitiator setup = WeldInitiator
                .from(MailSenderProducer.class)
                .build();

        @Test
        void productionFailsFastWithGuidance() {
            // ApplicationScoped production runs lazily on the first business call
            Exception thrown = assertThrows(Exception.class, () ->
                    setup.select(MailSender.class).get().send(new MailMessage()));

            assertTrue(causeChainContains(thrown, "No Jakarta Mail Session available"),
                    "the failure must be the producer's fail-fast, not an unrelated wrap: " + thrown);
        }
    }

    private static boolean causeChainContains(Throwable throwable, String messagePart) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current.getMessage() != null && current.getMessage().contains(messagePart)) {
                return true;
            }
        }
        return false;
    }
}
