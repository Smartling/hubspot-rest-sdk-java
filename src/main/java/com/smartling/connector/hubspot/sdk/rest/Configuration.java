package com.smartling.connector.hubspot.sdk.rest;

import java.util.Collections;
import java.util.Map;

public class Configuration
{
    private static final String API_HOST = "https://api.hubapi.com";

    private String apiUrl;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String redirectUri;
    private int    connectTimeoutMillis = 10_000;
    private int    readTimeoutMillis    = 60_000;
    private Map<String, String> properties = Collections.emptyMap();

    private Configuration(String apiUrl, String clientId, String clientSecret, String refreshToken, String redirectUri)
    {
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.refreshToken = refreshToken;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public static Configuration build(String clientId, String clientSecret, String refreshToken, String redirectUri)
    {
        return new Configuration(API_HOST, clientId, clientSecret, refreshToken, redirectUri);
    }

    public static Configuration build(String apiUrl, String clientId, String clientSecret, String refreshToken, String redirectUri)
    {
        return new Configuration(apiUrl, clientId, clientSecret, refreshToken, redirectUri);
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public int getConnectTimeoutMillis()
    {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis()
    {
        return readTimeoutMillis;
    }

    public String getPropertyValue(String name)
    {
        return this.properties.get(name);
    }

    public Configuration setConnectTimeoutMillis(int connectTimeoutMillis)
    {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public Configuration setReadTimeoutMillis(int readTimeoutMillis)
    {
        this.readTimeoutMillis = readTimeoutMillis;
        return this;
    }

    public Configuration setProperties(Map<String, String> properties)
    {
        this.properties = properties;
        return this;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public String getRedirectUri()
    {
        return redirectUri;
    }
}
