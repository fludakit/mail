package io.github.fludakit.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Plain configuration for mail sending over SMTP.
 *
 * <p>Property name constants are provided for convenience when configuring via MicroProfile Config
 * or other configuration sources.</p>
 */
public class MailConfig {

    // SMTP property names
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_STARTTLS = "mail.smtp.starttls.enable";
    public static final String SMTP_FROM = "mail.smtp.from";

    private String host = "localhost";
    private int port = 25;
    private String username;
    private String password;
    private boolean authEnabled = false;

    private boolean starttls = false;
    private String from;

    // Extension properties for additional mail session configuration
    private final Map<String, String> extraProperties = new HashMap<>();

    /**
     * No-arg constructor required for CDI client proxies.
     */
    public MailConfig() {
    }
    
    // Getters and setters
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public boolean isStarttls() {
        return starttls;
    }
    
    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public Map<String, String> getExtraProperties() {
        return extraProperties;
    }
    
    public void addExtraProperty(String key, String value) {
        this.extraProperties.put(key, value);
    }
    
    /**
     * Converts this configuration to Jakarta Mail Properties.
     *
     * <p>The host/port/authEnabled/starttls/from fields are mapped to their {@code mail.smtp.*}
     * properties, followed by any extra properties.</p>
     *
     * @return Properties object suitable for creating a Jakarta Mail Session
     */
    public Properties toJakartaMailProperties() {
        Properties props = new Properties();

        props.put(SMTP_HOST, host);
        props.put(SMTP_PORT, String.valueOf(port));
        if (authEnabled) {
            props.put(SMTP_AUTH, "true");
        }
        if (starttls) {
            props.put(SMTP_STARTTLS, "true");
        }
        if (from != null) {
            props.put(SMTP_FROM, from);
        }

        // Add extra properties
        props.putAll(extraProperties);

        return props;
    }
}
