package io.github.fludakit.mail.cdi;

import io.github.fludakit.mail.MailBuilder;
import io.github.fludakit.mail.MailSender;
import io.github.fludakit.mail.template.TemplateProcessor;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 * CDI producer for MailBuilder beans.
 * 
 * <p>This producer creates MailBuilder beans with the injected MailSender and TemplateProcessor.
 * The MailBuilder is produced as {@code @Dependent} scope so each injection point gets a fresh instance.</p>
 */
@Dependent
public class MailBuilderProducer {
    
    @Inject
    Instance<TemplateProcessor> templateProcessorInstance;
    
    @Produces
    @Dependent
    public MailBuilder produceMailBuilder(MailSender mailSender) {
        TemplateProcessor templateProcessor = templateProcessorInstance.isUnsatisfied() 
            ? TemplateProcessor.DEFAULT
            : templateProcessorInstance.get();
        return new MailBuilder(mailSender, templateProcessor);
    }
}
