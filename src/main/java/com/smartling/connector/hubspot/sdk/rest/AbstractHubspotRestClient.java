package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotApiNotFoundException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.FeignException;
import feign.RequestInterceptor;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.function.Supplier;

public abstract class AbstractHubspotRestClient
{
    private final TokenProvider tokenProvider;
    private RefreshTokenData refreshTokenData;

    public AbstractHubspotRestClient(TokenProvider tokenProvider)
    {
        this.tokenProvider = tokenProvider;
    }

    public RequestInterceptor getAuthenticationInterceptor()
    {
        return template -> template.header("Authorization", "Bearer " + refreshTokenData.getAccessToken());
    }

    protected <T> T execute(Supplier<T> apiCall) throws HubspotApiException
    {
        refreshTokenData = tokenProvider.getTokenData();

        try
        {
            return apiCall.get();
        }
        catch (FeignException.NotFound e)
        {
            throw new HubspotApiNotFoundException("Hubspot asset not found", e);
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    protected static Gson configuredGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static class DateSerializer implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException
        {
            return new Date(json.getAsLong());
        }
    }
}
