package io.github.fludakit.mail.template;

import java.util.Locale;
import java.util.Map;

/**
 * Template processor for rendering email subject and body templates.
 * 
 * <p>Implementations can use different template engines like FreeMarker, Thymeleaf, etc.</p>
 * 
 * <p>The {@link #DEFAULT} implementation checks if FreeMarker is present on the classpath.
 * If present, it uses FreeMarker for template rendering. Otherwise, it falls back to
 * {@link SimpleTemplateProcessor} for simple {@code :name} placeholder replacement.</p>
 */
@FunctionalInterface
public interface TemplateProcessor {
    
    /**
     * Default base path for template resources on the classpath.
     */
    String DEFAULT_BASE_PATH = "templates/";
    
    /**
     * Renders a template with the given context and locale.
     *
     * @param templateName the template name (e.g., "welcome.subject" or "welcome.body")
     * @param context the template context (variables to substitute)
     * @param locale the locale for internationalization
     * @return the rendered template string
     */
    String render(String templateName, Map<String, Object> context, Locale locale);
    
    /**
     * Default TemplateProcessor implementation that uses FreeMarker if available,
     * otherwise falls back to StringTemplateProcessor.
     */
    TemplateProcessor DEFAULT = (templateName, context, locale) -> {
        if (isFreeMarkerPresent()) {
            return new FreeMarkerTemplateProcessor().render(templateName, context, locale);
        }
        return new SimpleTemplateProcessor().render(templateName, context, locale);
    };
    
    /**
     * Checks if FreeMarker is available on the classpath.
     */
    private static boolean isFreeMarkerPresent() {
        try {
            Class.forName("freemarker.template.Configuration");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * FreeMarker-based template processor implementation.
     *
     * <p>This inner class uses fully qualified names for all FreeMarker classes to avoid
     * direct imports, making FreeMarker an optional dependency on the classpath.</p>
     */
    class FreeMarkerTemplateProcessor implements TemplateProcessor {

        /**
         * Renders a template using FreeMarker.
         *
         * @param templateName the template name
         * @param context the template context
         * @param locale the locale
         * @return the rendered template string
         */
        public String render(String templateName, Map<String, Object> context, Locale locale) {
            try {
                // Use fully qualified names to avoid direct imports
                freemarker.template.Configuration configuration = new freemarker.template.Configuration(
                        freemarker.template.Configuration.VERSION_2_3_32
                );
                configuration.setClassLoaderForTemplateLoading(
                        TemplateProcessor.class.getClassLoader(),
                        DEFAULT_BASE_PATH
                );
                configuration.setDefaultEncoding("UTF-8");
                configuration.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
                configuration.setLogTemplateExceptions(false);
                configuration.setWrapUncheckedExceptions(true);

                // Try locale-specific template first (e.g., welcome.subject_en.ftl)
                String localeTemplateName = templateName + "_" + locale.getLanguage() + ".ftl";
                
                freemarker.template.Template template;
                try {
                    template = configuration.getTemplate(localeTemplateName);
                } catch (java.io.IOException e) {
                    // Fall back to non-locale-specific template (e.g., welcome.subject.ftl)
                    template = configuration.getTemplate(templateName + ".ftl");
                }

                java.io.StringWriter writer = new java.io.StringWriter();
                template.process(context, writer);
                return writer.toString();
            } catch (java.io.IOException | freemarker.template.TemplateException e) {
                throw new RuntimeException("Failed to render template with FreeMarker: " + templateName, e);
            }
        }
    }
}

