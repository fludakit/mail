package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailBuilder;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests CDI producers for MailSender, MailBuilder, and MailConfig.
 * Uses {@link WeldInitiator} with explicit bean classes and decorator support.
 */
@ExtendWith(WeldJunit5Extension.class)
class MailBuilderTest {

    @WeldSetup
    WeldInitiator setup = WeldInitiator.of(
            WeldInitiator.createWeld()
                    .disableDiscovery()
                    // 1. Register the class so Weld discovers it
                    .addBeanClass(MailSenderDecorator.class)
                    .addBeanClass(MailBuilderProducer.class)
                    .addBeanClass(TestMailSenderProducer.class)
                    .addBeanClass(MailTrackingRegistry.class)
                    // 2. Enable it as a decorator
                    .decorators(MailSenderDecorator.class)
    );

    @Inject
    Instance<MailBuilder> mailBuilderInstance;

    @Inject
    MailTrackingRegistry registry;

    @BeforeEach
    void clearRegistry() {
        registry.clear();
    }

    @Test
    void beansExist() {
        assertNotNull(mailBuilderInstance);
        assertNotNull(registry);
    }

    @Test
    void sendMailWithBuilder() {
        mailBuilderInstance.get()
                .from("sender@example.com")
                .to("recipient@example.com")
                .subject("Test Subject")
                .htmlBody("<p>Test Body</p>")
                .send();

        new MailAssertions(registry.getMessages())
                .hasSentCount(1)
                .containsSubject("Test Subject")
                .sentTo("recipient@example.com");
    }

    @Test
    void sendMultipleMails() {
        mailBuilderInstance.get()
                .from("sender@example.com")
                .to("recipient1@example.com")
                .subject("First Message")
                .htmlBody("<p>First</p>")
                .send();

        mailBuilderInstance.get()
                .from("sender@example.com")
                .to("recipient2@example.com")
                .subject("Second Message")
                .htmlBody("<p>Second</p>")
                .send();

        new MailAssertions(registry.getMessages())
                .hasSentCount(2)
                .containsSubject("First Message")
                .containsSubject("Second Message")
                .sentTo("recipient1@example.com")
                .sentTo("recipient2@example.com");
    }
}
