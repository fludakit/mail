package io.github.fludakit.mail.config;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.Dependent;

/**
 * Holds the {@code fluda.mail.*} MicroProfile Config properties for mail configuration.
 * 
 * <p>Supported properties:</p>
 * <ul>
 *   <li>fluda.mail.host - Mail server host (default: localhost)</li>
 *   <li>fluda.mail.port - Mail server port (default: 25)</li>
 *   <li>fluda.mail.username - Mail server username</li>
 *   <li>fluda.mail.password - Mail server password</li>
 *   <li>fluda.mail.auth-enabled - Enable authentication (default: false)</li>
 *   <li>fluda.mail.protocol - Protocol to use: smtp or pop3 (default: smtp)</li>
 *   <li>fluda.mail.starttls - Enable STARTTLS for SMTP (default: false)</li>
 *   <li>fluda.mail.from - Default from address for SMTP</li>
 * </ul>
 */
@Dependent
@ConfigProperties(prefix = "fluda.mail")
public class MailProperties {

    @ConfigProperty(name = "host", defaultValue = "localhost")
    private String host;

    @ConfigProperty(name = "port", defaultValue = "25")
    private int port;

    @ConfigProperty(name = "username", defaultValue = "")
    private String username;

    @ConfigProperty(name = "password", defaultValue = "")
    private String password;

    @ConfigProperty(name = "auth-enabled", defaultValue = "false")
    private boolean authEnabled;

    @ConfigProperty(name = "protocol", defaultValue = "smtp")
    private String protocol;

    @ConfigProperty(name = "starttls", defaultValue = "false")
    private boolean starttls;

    @ConfigProperty(name = "from", defaultValue = "")
    private String from;

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public boolean authEnabled() {
        return authEnabled;
    }

    public String protocol() {
        return protocol;
    }

    public boolean starttls() {
        return starttls;
    }

    public String from() {
        return from;
    }
}
