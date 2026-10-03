package io.github.fludakit.mail.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple string-based template processor that performs {@code :name} placeholder replacement.
 * 
 * <p>This is a fallback implementation used when FreeMarker is not available on the classpath.
 * It loads templates from the classpath and replaces {@code :name} placeholders with values
 * from the context map.</p>
 * 
 * <p>Template files should be placed in the classpath under a configurable base path
 * (default: "templates/"). Subject templates use {@code .txt} extension, body templates
 * use {@code .html} extension.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * TemplateProcessor processor = new StringTemplateProcessor();
 * Map&lt;String, Object&gt; context = Map.of("userName", "John");
 * String body = processor.render("welcome.body", context, Locale.ENGLISH);
 * </pre>
 * 
 * <p>Template file structure:</p>
 * <ul>
 *   <li>templates/welcome.subject.txt - Subject template</li>
 *   <li>templates/welcome.body.html - Body template (HTML)</li>
 * </ul>
 */
public class SimpleTemplateProcessor implements TemplateProcessor {
    
    private final String basePath;
    
    /**
     * Creates a new StringTemplateProcessor with default base path {@link TemplateProcessor#DEFAULT_BASE_PATH}.
     */
    public SimpleTemplateProcessor() {
        this(DEFAULT_BASE_PATH);
    }
    
    /**
     * Creates a new StringTemplateProcessor with a custom base path.
     *
     * @param basePath the classpath base path where templates are located
     */
    public SimpleTemplateProcessor(String basePath) {
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";
    }
    
    @Override
    public String render(String templateName, Map<String, Object> context, Locale locale) {
        // Determine file extension based on template type
        String extension = templateName.endsWith(".subject") ? ".txt" : ".html";
        
        // Try locale-specific template first (e.g., welcome.subject_en.txt)
        String localeTemplateName = basePath + templateName + "_" + locale.getLanguage() + extension;
        String content = loadTemplate(localeTemplateName);
        
        // Fall back to non-locale-specific template (e.g., welcome.subject.txt)
        if (content == null) {
            String defaultTemplateName = basePath + templateName + extension;
            content = loadTemplate(defaultTemplateName);
        }
        
        if (content == null) {
            throw new RuntimeException("Template not found: " + templateName);
        }
        
        // Replace :name placeholders with context values
        return replacePlaceholders(content, context);
    }
    
    private String loadTemplate(String resourceName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            return null;
        }
    }
    
    private String replacePlaceholders(String template, Map<String, Object> context) {
        String result = template;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = ":" + entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
