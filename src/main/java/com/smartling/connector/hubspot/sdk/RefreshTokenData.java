package com.smartling.connector.hubspot.sdk;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenData
{
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

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

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public int getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(final int expiresIn)
    {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString()
    {
        return "RefreshTokenData{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
