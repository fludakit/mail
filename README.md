# FluDa Mail

[![Build](https://github.com/fludakit/mail/actions/workflows/build.yml/badge.svg)](https://github.com/fludakit/mail/actions/workflows/build.yml)

Mail sending support for Jakarta EE / CDI applications with multiple provider implementations.

## Modules

- `fluda-mail-core`: Core mail API, MailConfig, MailBuilder, TemplateProcessor, and Jakarta Mail implementation (pure Java SE, no CDI).
- `fluda-mail-config`: MicroProfile Config integration for mail configuration (SMTP/POP3 settings).
- `fluda-mail-cdi`: CDI extension that produces Session, MailSender, MailBuilder, and TemplateProcessor beans.
- `fluda-mail-sendgrid`: SendGrid implementation of MailSender (pure Java SE, no CDI).

## Architecture

The mail project follows a clear separation of concerns:

- **Core** is a pure Java SE library with Jakarta Mail support. It includes `MailConfig`, `MailBuilder`, `TemplateProcessor` (with FreeMarker inner class and StringTemplateProcessor), and can be used standalone without CDI.
- **Config** provides MicroProfile Config integration to produce a `MailConfig` bean from configuration properties via `MailProperties`.
- **CDI** automatically creates Jakarta Mail `Session`, `MailSender`, `MailBuilder`, and `TemplateProcessor` beans. If a `MailConfig` bean is available, it creates a Session from it. Otherwise, it checks for an existing Session bean. The TemplateProcessor automatically uses FreeMarker if available, otherwise falls back to StringTemplateProcessor.
- **SendGrid** is a pure Java SE implementation that depends only on core. Users can create alternative `MailSender` beans and activate them via `beans.xml` or `@Priority`.

## Usage

### Basic CDI Usage with MailBuilder (Recommended)

Add dependencies:

```xml
<dependency>
    <groupId>io.github.fludakit</groupId>
    <artifactId>fluda-mail-core</artifactId>
    <version>${fluda.version}</version>
</dependency>
<dependency>
    <groupId>io.github.fludakit</groupId>
    <artifactId>fluda-mail-config</artifactId>
    <version>${fluda.version}</version>
</dependency>
<dependency>
    <groupId>io.github.fludakit</groupId>
    <artifactId>fluda-mail-cdi</artifactId>
    <version>${fluda.version}</version>
</dependency>
```

Configure mail settings in `microprofile-config.properties`:

```properties
mail.host=smtp.example.com
mail.port=587
mail.auth-enabled=true
mail.starttls=true
mail.username=your-username
mail.password=your-password
mail.protocol=smtp
mail.from=noreply@example.com
```

Inject and use `MailBuilder`:

```java
import io.github.fludakit.mail.MailBuilder;
import jakarta.inject.Inject;

@Inject
MailBuilder mailBuilder;

public void sendWelcomeEmail(String to, String userName) {
    mailBuilder.from("noreply@example.com")
        .to(to)
        .subject("Welcome!")
        .htmlBody("<p>Hello " + userName + ", welcome aboard!</p>")
        .send();
}
```

Or use templates:

```java
import io.github.fludakit.mail.MailBuilder;
import jakarta.inject.Inject;
import java.util.Locale;
import java.util.Map;

@Inject
MailBuilder mailBuilder;

public void sendWelcomeEmail(String to, String userName) {
    mailBuilder.from("noreply@example.com")
        .to(to)
        .locale(Locale.ENGLISH)
        .template("welcome", Map.of("userName", userName))
        .send();
}
```

Templates should be placed in `src/main/resources/templates/`:

**With FreeMarker (if on classpath):**
- `welcome.subject.en.ftl` - Subject template (FreeMarker syntax)
- `welcome.body.en.ftl` - Body template (HTML with FreeMarker syntax)

**Without FreeMarker (StringTemplateProcessor fallback):**
- `welcome.subject.en.txt` - Subject template (simple `:name` placeholders)
- `welcome.body.en.html` - Body template (HTML with `:name` placeholders)

Example template with `:name` placeholders:
```html
<p>Hello :userName,</p>
<p>Welcome to our platform!</p>
```

Or inject `MailSender` directly:

```java
import io.github.fludakit.mail.MailSender;
import io.github.fludakit.mail.MailMessage;
import jakarta.inject.Inject;

@Inject
MailSender mailSender;

public void sendWelcomeEmail(String to, String userName) {
    MailMessage message = new MailMessage();
    message.setFrom("noreply@example.com");
    message.addTo(to);
    message.setSubject("Welcome!");
    message.setHtmlBody("<p>Hello " + userName + ", welcome aboard!</p>");

    mailSender.send(message);
}
```

### Using SendGrid

If you prefer SendGrid over SMTP:

```xml
<dependency>
    <groupId>io.github.fludakit</groupId>
    <artifactId>fluda-mail-core</artifactId>
    <version>${fluda.version}</version>
</dependency>
<dependency>
    <groupId>io.github.fludakit</groupId>
    <artifactId>fluda-mail-sendgrid</artifactId>
    <version>${fluda.version}</version>
</dependency>
```

Create a producer bean:

```java
import io.github.fludakit.mail.MailSender;
import io.github.fludakit.mail.sendgrid.SendGridMailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class SendGridProducer {
    @Produces
    @ApplicationScoped
    public MailSender mailSender() {
        return new SendGridMailSender("your-sendgrid-api-key");
    }
}
```

### Pure Java SE (No CDI)

The core module can be used without CDI:

```java
import io.github.fludakit.mail.JakartaMailSender;
import io.github.fludakit.mail.MailBuilder;
import io.github.fludakit.mail.MailConfig;
import io.github.fludakit.mail.MailSender;
import io.github.fludakit.mail.template.TemplateProcessor;
import jakarta.mail.Session;

// Create configuration
MailConfig config = new MailConfig();
config.setHost("smtp.example.com");
config.setPort(587);
config.setAuthEnabled(true);
config.setUsername("user");
config.setPassword("pass");
config.setProtocol("smtp");

// Create Jakarta Mail Session from config
Session session = Session.getInstance(config.toJakartaMailProperties());

// Create sender and builder
MailSender sender = new JakartaMailSender(session);
TemplateProcessor templateProcessor = TemplateProcessor.DEFAULT;
new MailBuilder(sender, templateProcessor)
    .from("sender@example.com")
    .to("recipient@example.com")
    .locale(Locale.ENGLISH)
    .template("welcome", Map.of("userName", "John"))
    .send();
```

## Configuration Properties

The config module supports the following properties:

### Mail Configuration
- `mail.host` - Mail server host (default: localhost)
- `mail.port` - Mail server port (default: 25)
- `mail.username` - Mail server username
- `mail.password` - Mail server password
- `mail.auth-enabled` - Enable authentication (default: false)
- `mail.protocol` - Protocol to use: smtp or pop3 (default: smtp)
- `mail.starttls` - Enable STARTTLS for SMTP (default: false)
- `mail.from` - Default from address for SMTP

### Extension Properties
You can add custom Jakarta Mail properties using the `extraProperties` map in `MailConfig`.

## MailConfig

The `MailConfig` class in the core module provides:

- Static constants for property names (e.g., `SMTP_HOST`, `SMTP_USERNAME`)
- Shared fields for SMTP/POP3: host, port, username, password, authEnabled
- Protocol field to switch between "smtp" and "pop3"
- SMTP-specific fields: starttls, from
- `extraProperties` map for extension configuration
- `toJakartaMailProperties()` method to assemble into `java.util.Properties`

## TemplateProcessor

The `TemplateProcessor` interface provides template rendering with automatic fallback:

- `TemplateProcessor.DEFAULT` - Auto-detects FreeMarker on classpath
  - If FreeMarker is present: uses `FreeMarkerTemplateProcessor` (inner class)
  - If not: uses `SimpleTemplateProcessor` for simple `:name` replacement
- FreeMarker is optional - uses fully qualified names to avoid direct imports
- `SimpleTemplateProcessor` - Simple placeholder replacement
  - Subject templates: `.txt` extension
  - Body templates: `.html` extension
  - Loads from classpath `templates/` directory

## Building

```bash
./mvnw clean install
```

## Documentation

See the [reference documentation site](https://fludakit.github.io/) for installation, quickstart, and full API reference.

## Contributing

Contributions are welcome — issues, pull requests, and feature suggestions are all encouraged. See [CONTRIBUTING.md](CONTRIBUTING.md) for how to build the project and submit changes.
