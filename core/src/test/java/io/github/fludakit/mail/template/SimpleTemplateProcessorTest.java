package io.github.fludakit.mail.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTemplateProcessorTest {

    @Test
    void testRenderSubjectWithLocale() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        String result = processor.render("welcome.subject", context, Locale.ENGLISH);

        assertEquals("Welcome FluDa to our platform", result);
    }

    @Test
    void testRenderSubjectFallbackToDefault() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        // French locale should fall back to default (no _fr template)
        String result = processor.render("welcome.subject", context, Locale.FRENCH);

        assertEquals("Welcome to our platform", result);
    }

    @Test
    void testRenderBodyWithLocale() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");
        context.put("accountId", "ACC-123");

        String result = processor.render("welcome.body", context, Locale.ENGLISH);

        assertEquals("<p>Hello FluDa,</p><p>Welcome to our platform! Your account ACC-123 is ready.</p>", result);
    }

    @Test
    void testRenderBodyFallbackToDefault() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        // French locale should fall back to default (no _fr template)
        String result = processor.render("welcome.body", context, Locale.FRENCH);

        assertEquals("<p>Hello FluDa,</p><p>Welcome to our platform!</p>", result);
    }

    @Test
    void testRenderWithNullValue() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", null);

        String result = processor.render("welcome.subject", context, Locale.ENGLISH);

        assertEquals("Welcome  to our platform", result);
    }

    @Test
    void testRenderWithMissingTemplate() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor();
        Map<String, Object> context = new HashMap<>();

        assertThrows(RuntimeException.class, () -> {
            processor.render("nonexistent.subject", context, Locale.ENGLISH);
        });
    }

    @Test
    void testCustomBasePath() {
        SimpleTemplateProcessor processor = new SimpleTemplateProcessor("templates/");
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        String result = processor.render("welcome.subject", context, Locale.ENGLISH);

        assertEquals("Welcome FluDa to our platform", result);
    }
}
