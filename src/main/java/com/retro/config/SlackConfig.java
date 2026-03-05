package com.retro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "slack")
public class SlackConfig {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;

    public String getClientId() { return clientId; }
    public void setClientId(String v) { this.clientId = v; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String v) { this.clientSecret = v; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String v) { this.redirectUri = v; }

    public String getScopes() { return scopes; }
    public void setScopes(String v) { this.scopes = v; }
}