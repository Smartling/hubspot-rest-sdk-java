package com.smartling.connector.hubspot.sdk.rest.api;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenData
{
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(final String accessToken)
    {
        this.accessToken = accessToken;
    }

    public int getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(final int expiresIn)
    {
        this.expiresIn = expiresIn;
    }
}
