package io.github.fludakit.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailBuilderTest {

    private MailTrackingRegistry registry;
    private MockMailSender sender;
    private MailBuilder builder;

    @BeforeEach
    void setUp() {
        registry = new MailTrackingRegistry();
        sender = new MockMailSender(registry);
        builder = new MailBuilder(sender);
    }

    @Test
    void testBuildSimpleMessage() {
        builder.from("sender@example.com")
                .to("recipient@example.com")
                .subject("Test Subject")
                .htmlBody("<p>Test Body</p>")
                .send();

        MailAssertions assertions = new MailAssertions(registry.getMessages());
        assertions.hasSentCount(1)
                  .containsSubject("Test Subject")
                  .sentTo("recipient@example.com");

        MailMessage message = registry.getLastMessage();
        assertNotNull(message);
        assertEquals("sender@example.com", message.getFrom());
        assertEquals("<p>Test Body</p>", message.getHtmlBody());
    }

    @Test
    void testBuildMessageWithMultipleRecipients() {
        builder.from("sender@example.com")
                .to("recipient1@example.com", "recipient2@example.com")
                .subject("Test Subject")
                .htmlBody("<p>Test Body</p>")
                .send();

        MailAssertions assertions = new MailAssertions(registry.getMessages());
        assertions.hasSentCount(1)
                  .sentTo("recipient1@example.com")
                  .sentTo("recipient2@example.com");

        MailMessage message = registry.getLastMessage();
        assertEquals(2, message.getTo().size());
    }

    @Test
    void testBuildMultipleMessages() {
        MailBuilder builder1 = new MailBuilder(sender);
        builder1.from("sender@example.com")
                .to("recipient1@example.com")
                .subject("First Message")
                .htmlBody("<p>First</p>")
                .send();

        MailBuilder builder2 = new MailBuilder(sender);
        builder2.from("sender@example.com")
                .to("recipient2@example.com")
                .subject("Second Message")
                .htmlBody("<p>Second</p>")
                .send();

        MailAssertions assertions = new MailAssertions(registry.getMessages());
        assertions.hasSentCount(2)
                  .containsSubject("First Message")
                  .containsSubject("Second Message");
    }

    @Test
    void testGetMessage() {
        builder.from("sender@example.com")
                .to("recipient@example.com")
                .subject("Test Subject");

        MailMessage message = builder.getMessage();
        assertNotNull(message);
        assertEquals("sender@example.com", message.getFrom());
        assertEquals("Test Subject", message.getSubject());
    }
}
