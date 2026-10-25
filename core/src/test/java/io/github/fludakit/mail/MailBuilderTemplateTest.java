package io.github.fludakit.mail;

import io.github.fludakit.mail.template.TemplateProcessor;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Covers {@link MailBuilder#template(String, Map)}: what the processor is asked to render, how the
 * rendered values land on the message, the locale fallback, and the no-processor failure.
 */
class MailBuilderTemplateTest {

    @Test
    void templateRendersSubjectAndBodyThroughTheProcessor() {
        MailMessage message = new MailBuilder(msg -> { }, (name, context, locale) -> name.toUpperCase())
                .template("welcome", Map.of("user", "Duke"))
                .getMessage();

        assertEquals("WELCOME.SUBJECT", message.getSubject());
        assertEquals("WELCOME.BODY", message.getHtmlBody());
    }

    @Test
    void theContextAndLocaleArePassedThrough() {
        Map<String, Object> context = Map.of("user", "Duke");

        MailBuilder builder = new MailBuilder(message -> { }, (name, ctx, locale) -> {
            assertEquals(context, ctx);
            assertEquals(Locale.ITALIAN, locale);
            return "rendered:" + name;
        }).locale(Locale.ITALIAN);

        MailMessage message = builder.template("welcome", context).getMessage();

        assertEquals("rendered:welcome.subject", message.getSubject());
        assertEquals("rendered:welcome.body", message.getHtmlBody());
    }

    @Test
    void withoutAnExplicitLocaleTheDefaultIsUsed() {
        MailBuilder builder = new MailBuilder(message -> { }, (name, ctx, locale) -> {
            assertEquals(Locale.getDefault(), locale, "the platform default is the fallback");
            return "rendered";
        });

        builder.template("welcome", Map.of());
    }

    @Test
    void withoutAProcessorTemplateFailsFast() {
        MailBuilder builder = new MailBuilder(message -> { });

        assertThrows(IllegalStateException.class, () -> builder.template("welcome", Map.of()));
    }
}
