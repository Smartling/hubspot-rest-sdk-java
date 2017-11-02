package com.smartling.connector.hubspot.sdk.rest;

import java.util.Collections;
import java.util.Map;

public class Configuration
{
    private static final String API_HOST = "https://api.hubapi.com";

    private final String apiUrl;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String redirectUri;
    private int    connectTimeoutMillis = 10_000;
    private int    readTimeoutMillis    = 60_000;
    private Map<String, String> properties = Collections.emptyMap();

    private Configuration(String apiUrl, String clientId, String clientSecret, String redirectUri, String refreshToken)
    {
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.redirectUri = redirectUri;
    }

    public static Configuration build(String clientId, String clientSecret, String redirectUri, String refreshToken)
    {
        return new Configuration(API_HOST, clientId, clientSecret, redirectUri, refreshToken);
    }

    public static Configuration build(String apiUrl, String clientId, String clientSecret, String redirectUri, String refreshToken)
    {
        return new Configuration(apiUrl, clientId, clientSecret, redirectUri, refreshToken);
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public String getRedirectUri()
    {
        return redirectUri;
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
}
