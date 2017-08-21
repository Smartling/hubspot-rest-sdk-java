package com.smartling.connector.hubspot.sdk.rest;

import java.util.Collections;
import java.util.Map;

public class Configuration
{
    private static final String API_HOST = "https://api.hubapi.com";

    private String apiUrl;
    private String clientId;
    private String refreshToken;
    private int    connectTimeoutMillis = 10_000;
    private int    readTimeoutMillis    = 60_000;
    private Map<String, String> properties = Collections.emptyMap();

    private Configuration(String apiUrl, String clientId, String refreshToken)
    {
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.refreshToken = refreshToken;
    }

    public static Configuration build(String clientId, String refreshToken)
    {
        return new Configuration(API_HOST, clientId, refreshToken);
    }

    public static Configuration build(String apiUrl, String clientId, String refreshToken)
    {
        return new Configuration(apiUrl, clientId, refreshToken);
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
}
