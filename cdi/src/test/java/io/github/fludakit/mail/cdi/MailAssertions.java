package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailMessage;

import java.util.List;

public class MailAssertions {

    private final List<MailMessage> messages;

    public MailAssertions(List<MailMessage> messages) {
        this.messages = messages;
    }

    public MailAssertions hasSentCount(int expected) {
        if (messages.size() != expected) {
            throw new AssertionError("Expected " + expected + " emails sent, but found " + messages.size());
        }
        return this;
    }

    public MailAssertions containsSubject(String expectedSubject) {
        boolean match = messages.stream().anyMatch(m -> m.getSubject().contains(expectedSubject));
        if (!match) {
            throw new AssertionError("No email found containing the subject snippet: '" + expectedSubject + "'");
        }
        return this;
    }

    public MailAssertions sentTo(String recipientEmail) {
        boolean match = messages.stream().anyMatch(m -> m.getTo().contains(recipientEmail));
        if (!match) {
            throw new AssertionError("No email found addressing target recipient: " + recipientEmail);
        }
        return this;
    }
}
