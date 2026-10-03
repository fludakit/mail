package io.github.fludakit.mail;

import io.restassured.RestAssured;
import jakarta.mail.Session;
import ch.martinelli.oss.testcontainers.mailpit.MailpitContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test that starts a Mailpit container via Testcontainers and verifies
 * end-to-end email sending through {@link JakartaMailSender}.
 *
 * <p>The Mailpit container provides a real SMTP server for sending and an HTTP API
 * for verifying message delivery.</p>
 */
@Testcontainers(disabledWithoutDocker = true)
class MailSenderIntegrationTest {

    @Container
    static MailpitContainer mailpit = new MailpitContainer("axllent/mailpit:latest")
            .waitingFor(Wait.forHttp("/api/v1/messages").forPort(MailpitContainer.HTTP_PORT));

    @BeforeEach
    void clearMessages() {
        RestAssured.baseURI = "http://" + mailpit.getHost();
        RestAssured.port = mailpit.getHttpPort();
        mailpit.getClient().deleteAllMessages();
    }

    @Test
    void sendMailAndVerifyViaApi() {
        MailSender sender = createMailSender();

        MailMessage message = new MailMessage();
        message.setFrom("sender@example.com");
        message.addTo("receiver@example.com");
        message.setSubject("Test Subject - Hello from FluDa");
        message.setHtmlBody("<p>This is a test email body.</p>");

        sender.send(message);

        given()
        .when()
            .get("/api/v1/messages")
        .then()
            .statusCode(200)
            .body("total", equalTo(1))
            .body("messages[0].Subject", equalTo("Test Subject - Hello from FluDa"))
            .body("messages[0].To[0].Address", equalTo("receiver@example.com"));
    }

    @Test
    void sendMultipleMailsAndVerifyViaApi() {
        MailSender sender = createMailSender();

        MailMessage first = new MailMessage();
        first.setFrom("sender@example.com");
        first.addTo("alice@example.com");
        first.setSubject("First Message");
        first.setHtmlBody("<p>First</p>");
        sender.send(first);

        MailMessage second = new MailMessage();
        second.setFrom("sender@example.com");
        second.addTo("bob@example.com");
        second.setSubject("Second Message");
        second.setHtmlBody("<p>Second</p>");
        sender.send(second);

        given()
        .when()
            .get("/api/v1/messages")
        .then()
            .statusCode(200)
            .body("total", equalTo(2));
    }

    private MailSender createMailSender() {
        MailConfig config = new MailConfig();
        config.setHost(mailpit.getSmtpHost());
        config.setPort(mailpit.getSmtpPort());
        config.setProtocol("smtp");
        config.setAuthEnabled(false);
        config.setStarttls(false);
        Session session = Session.getInstance(config.toJakartaMailProperties());
        return new JakartaMailSender(session);
    }
}
