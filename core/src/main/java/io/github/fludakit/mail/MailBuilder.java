package io.github.fludakit.mail;

import io.github.fludakit.mail.template.TemplateProcessor;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Fluent builder for constructing and sending email messages.
 * 
 * <p>This is a pure Java SE implementation with no CDI dependencies. You must provide
 * a {@link MailSender} instance to send the message.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * MailSender sender = new JakartaMailSender(session);
 * new MailBuilder(sender)
 *     .from("sender@example.com")
 *     .to("recipient@example.com")
 *     .subject("Hello")
 *     .htmlBody("&lt;p&gt;World&lt;/p&gt;")
 *     .send();
 * </pre>
 * 
 * <p>Usage example with templates:</p>
 * <pre>
 * MailSender sender = new JakartaMailSender(session);
 * TemplateProcessor templateProcessor = new FreeMarkerTemplateProcessor();
 * new MailBuilder(sender, templateProcessor)
 *     .from("sender@example.com")
 *     .to("recipient@example.com")
 *     .locale(Locale.ENGLISH)
 *     .template("welcome", Map.of("userName", "John"))
 *     .send();
 * </pre>
 */
public class MailBuilder {

    private final MailSender sender;
    private final TemplateProcessor templateProcessor;
    private final MailMessage message = new MailMessage();
    private Locale locale;

    /**
     * Creates a new MailBuilder with the given MailSender (no template support).
     *
     * @param sender the MailSender to use for sending the message
     */
    public MailBuilder(MailSender sender) {
        this(sender, null);
    }

    /**
     * Creates a new MailBuilder with the given MailSender and TemplateProcessor.
     *
     * @param sender the MailSender to use for sending the message
     * @param templateProcessor the TemplateProcessor for rendering templates (can be null)
     */
    public MailBuilder(MailSender sender, TemplateProcessor templateProcessor) {
        this.sender = sender;
        this.templateProcessor = templateProcessor;
    }

    /**
     * Sets the from address.
     *
     * @param address the from address
     * @return this builder
     */
    public MailBuilder from(String address) {
        message.setFrom(address);
        return this;
    }

    /**
     * Adds recipient addresses.
     *
     * @param addresses the recipient addresses
     * @return this builder
     */
    public MailBuilder to(String... addresses) {
        message.addTo(addresses);
        return this;
    }

    /**
     * Sets the subject.
     *
     * @param subject the subject
     * @return this builder
     */
    public MailBuilder subject(String subject) {
        message.setSubject(subject);
        return this;
    }

    /**
     * Sets the HTML body.
     *
     * @param htmlBody the HTML body
     * @return this builder
     */
    public MailBuilder htmlBody(String htmlBody) {
        message.setHtmlBody(htmlBody);
        return this;
    }

    /**
     * Sets the locale for template rendering.
     *
     * @param locale the locale
     * @return this builder
     */
    public MailBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Adds an attachment with a stream supplier.
     *
     * @param filename the attachment filename
     * @param contentType the content type
     * @param streamSupplier supplier for the attachment input stream
     * @return this builder
     */
    public MailBuilder attachment(String filename, String contentType, Supplier<InputStream> streamSupplier) {
        this.message.getAttachments().add(new MailAttachment(filename, contentType, streamSupplier));
        return this;
    }

    /**
     * Adds an attachment with an immediate input stream.
     *
     * @param filename the attachment filename
     * @param contentType the content type
     * @param immediateStream the attachment input stream
     * @return this builder
     */
    public MailBuilder attachment(String filename, String contentType, InputStream immediateStream) {
        return attachment(filename, contentType, () -> immediateStream);
    }

    /**
     * Renders templates for subject and body using the configured TemplateProcessor.
     * 
     * <p>This method looks for templates named "{baseName}.subject" and "{baseName}.body"
     * and renders them with the given context and locale.</p>
     *
     * @param baseName the base name for templates (e.g., "welcome" looks for "welcome.subject" and "welcome.body")
     * @param context the template context (variables to substitute)
     * @return this builder
     * @throws IllegalStateException if no TemplateProcessor was provided
     */
    public MailBuilder template(String baseName, Map<String, Object> context) {
        if (templateProcessor == null) {
            throw new IllegalStateException("TemplateProcessor not configured. Use constructor with TemplateProcessor parameter.");
        }
        
        Locale activeLocale = locale != null ? locale : Locale.getDefault();
        
        String renderedSubject = templateProcessor.render(baseName + ".subject", context, activeLocale);
        String renderedBody = templateProcessor.render(baseName + ".body", context, activeLocale);
        
        this.message.setSubject(renderedSubject != null ? renderedSubject.trim() : "");
        this.message.setHtmlBody(renderedBody);
        
        return this;
    }

    /**
     * Gets the locale.
     *
     * @return the locale, or null if not set
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the message being built.
     *
     * @return the message
     */
    public MailMessage getMessage() {
        return message;
    }

    /**
     * Sends the message using the configured MailSender.
     *
     * @throws MailException if the message cannot be sent
     */
    public void send() {
        sender.send(message);
    }
}
