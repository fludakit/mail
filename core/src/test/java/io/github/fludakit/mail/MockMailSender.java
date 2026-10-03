package io.github.fludakit.mail;

public class MockMailSender implements MailSender {

    private final MailTrackingRegistry registry;

    public MockMailSender(MailTrackingRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void send(MailMessage message) {
        // Intercept delivery and save it to memory for verification assertions
        registry.track(message);
    }

}
