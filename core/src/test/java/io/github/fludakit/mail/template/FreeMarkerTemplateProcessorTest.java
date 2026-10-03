package io.github.fludakit.mail.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FreeMarkerTemplateProcessorTest {

    @Test
    void testRenderSubjectWithLocale() {
        TemplateProcessor.FreeMarkerTemplateProcessor processor = new TemplateProcessor.FreeMarkerTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        String result = processor.render("welcome.subject", context, Locale.ENGLISH);

        assertEquals("Welcome FluDa to our platform", result);
    }

    @Test
    void testRenderSubjectFallbackToDefault() {
        TemplateProcessor.FreeMarkerTemplateProcessor processor = new TemplateProcessor.FreeMarkerTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");

        // French locale should fall back to English (FreeMarker's built-in locale resolution)
        String result = processor.render("welcome.subject", context, Locale.FRENCH);

        assertEquals("Welcome FluDa to our platform", result);
    }

    @Test
    void testRenderBodyWithLocale() {
        TemplateProcessor.FreeMarkerTemplateProcessor processor = new TemplateProcessor.FreeMarkerTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");
        context.put("accountId", "ACC-123");

        String result = processor.render("welcome.body", context, Locale.ENGLISH);

        assertEquals("<p>Hello FluDa,</p><p>Welcome to our platform! Your account ACC-123 is ready.</p>", result);
    }

    @Test
    void testRenderBodyFallbackToDefault() {
        TemplateProcessor.FreeMarkerTemplateProcessor processor = new TemplateProcessor.FreeMarkerTemplateProcessor();
        Map<String, Object> context = new HashMap<>();
        context.put("name", "FluDa");
        context.put("accountId", "ACC-123");

        // French locale should fall back to English (FreeMarker's built-in locale resolution)
        String result = processor.render("welcome.body", context, Locale.FRENCH);

        assertEquals("<p>Hello FluDa,</p><p>Welcome to our platform! Your account ACC-123 is ready.</p>", result);
    }

    @Test
    void testRenderWithMissingTemplate() {
        TemplateProcessor.FreeMarkerTemplateProcessor processor = new TemplateProcessor.FreeMarkerTemplateProcessor();
        Map<String, Object> context = new HashMap<>();

        assertThrows(RuntimeException.class, () -> {
            processor.render("nonexistent.subject", context, Locale.ENGLISH);
        });
    }
}
