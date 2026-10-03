package io.github.fludakit.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Plain configuration for mail sending.
 * 
 * <p>This configuration supports both SMTP and POP3 protocols. Use the {@link #protocol} field
 * to switch between them. The shared fields (host, port, username, password, authEnabled) are
 * used for the selected protocol.</p>
 * 
 * <p>Property name constants are provided for convenience when configuring via MicroProfile Config
 * or other configuration sources.</p>
 */
public class MailConfig {
    
    // SMTP property names
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final String SMTP_USERNAME = "mail.smtp.username";
    public static final String SMTP_PASSWORD = "mail.smtp.password";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_STARTTLS = "mail.smtp.starttls.enable";
    public static final String SMTP_FROM = "mail.smtp.from";
    
    // POP3 property names
    public static final String POP3_HOST = "mail.pop3.host";
    public static final String POP3_PORT = "mail.pop3.port";
    public static final String POP3_USERNAME = "mail.pop3.username";
    public static final String POP3_PASSWORD = "mail.pop3.password";
    public static final String POP3_AUTH = "mail.pop3.auth";
    
    public static final MailConfig DEFAULT = new MailConfig();
    
    // Shared fields for SMTP/POP3
    private String host = "localhost";
    private int port = 25;
    private String username;
    private String password;
    private boolean authEnabled = false;
    
    // Protocol selection: "smtp" or "pop3"
    private String protocol = "smtp";
    
    // SMTP-specific fields
    private boolean starttls = false;
    private String from;
    
    // Extension properties for additional mail session configuration
    private final Map<String, String> extraProperties = new HashMap<>();
    
    /**
     * No-arg constructor required for CDI client proxies.
     */
    public MailConfig() {
    }
    
    public MailConfig(String host, int port, String username, String password, 
                      boolean authEnabled, String protocol) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.authEnabled = authEnabled;
        this.protocol = protocol;
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
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
     * <p>The properties are assembled based on the selected protocol. For example, if protocol
     * is "smtp", the host/port/username/password/authEnabled fields are mapped to mail.smtp.*
     * properties.</p>
     *
     * @return Properties object suitable for creating a Jakarta Mail Session
     */
    public Properties toJakartaMailProperties() {
        Properties props = new Properties();
        
        if ("smtp".equalsIgnoreCase(protocol)) {
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
        } else if ("pop3".equalsIgnoreCase(protocol)) {
            props.put(POP3_HOST, host);
            props.put(POP3_PORT, String.valueOf(port));
            if (authEnabled) {
                props.put(POP3_AUTH, "true");
            }
        }
        
        // Add extra properties
        props.putAll(extraProperties);
        
        return props;
    }
}
