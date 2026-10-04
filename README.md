# FluDa Mail

[![Build](https://github.com/fludakit/mail/actions/workflows/build.yml/badge.svg)](https://github.com/fludakit/mail/actions/workflows/build.yml)

Mail sending support for Jakarta EE / CDI applications with multiple provider implementations.

## Modules

- `fluda-mail-core`: Core mail API, MailConfig, MailBuilder, TemplateProcessor, and Jakarta Mail implementation (pure Java SE, no CDI).
- `fluda-mail-config`: MicroProfile Config integration for mail configuration (SMTP/POP3 settings).
- `fluda-mail-cdi`: CDI extension that produces Session, MailSender, MailBuilder, and TemplateProcessor beans.

## Architecture

The mail project follows a clear separation of concerns:

- **Core** is a pure Java SE library with Jakarta Mail support. It includes `MailConfig`, `MailBuilder`, and `TemplateProcessor` (with a FreeMarker implementation and a `SimpleTemplateProcessor` fallback), and can be used standalone without CDI.
- **Config** provides MicroProfile Config integration to produce a `MailConfig` bean from `fluda.mail.*` properties via `MailProperties`.
- **CDI** automatically creates Jakarta Mail `Session`, `MailSender`, `MailBuilder`, and `TemplateProcessor` beans. If a `MailConfig` bean is available, it creates a Session from it; otherwise, it uses an existing `Session` bean. The `TemplateProcessor` uses FreeMarker when it is on the classpath, otherwise falls back to `SimpleTemplateProcessor`.

## Usage

In plain Java SE, build a `Session`, wrap it in a `JakartaMailSender`, and send with the fluent `MailBuilder`:

```java
import io.github.fludakit.mail.JakartaMailSender;
import io.github.fludakit.mail.MailBuilder;
import io.github.fludakit.mail.MailSender;
import jakarta.mail.Session;
import java.util.Properties;

Session session = Session.getInstance(new Properties()); // configure for your SMTP server
MailSender sender = new JakartaMailSender(session);

new MailBuilder(sender)
    .from("sender@example.com")
    .to("recipient@example.com")
    .subject("Hello from FluDa Mail")
    .htmlBody("<p>This is a test email sent with FluDa Mail.</p>")
    .send();
```

In a CDI environment, add `fluda-mail-cdi` (and optionally `fluda-mail-config` for `fluda.mail.*` property-based configuration) and inject `MailBuilder` or `MailSender` directly.

For CDI and Jakarta EE setup, configuration properties, templates, and custom providers such as SendGrid, see the [Mail documentation](https://fludakit.github.io/documentation/mail/getting-started/).

## Building

```bash
./mvnw clean install
```

## Documentation

See the [reference documentation site](https://fludakit.github.io/documentation/mail/getting-started/) for getting started, templates, custom `MailSender` implementations, and full API reference.

## Contributing

Contributions are welcome — issues, pull requests, and feature suggestions are all encouraged. See [CONTRIBUTING.md](CONTRIBUTING.md) for how to build the project and submit changes.
