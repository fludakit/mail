package io.github.fludakit.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an email message to be sent.
 */
public class MailMessage {

    private String from;
    private final List<String> to = new ArrayList<>();
    private String subject;
    private String htmlBody;
    private final List<MailAttachment> attachments = new ArrayList<>();

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void addTo(String... addresses) {
        this.to.addAll(Arrays.asList(addresses));
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public List<MailAttachment> getAttachments() {
        return attachments;
    }
}
