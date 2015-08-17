package com.smartling.connector.hubspot.sdk.rest;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.smartling.connector.hubspot.sdk.HubspotApiException;

public abstract class AbstractHubspotRestClient
{
    private final RestExecutor executor;

    public AbstractHubspotRestClient(RestExecutor executor)
    {
        this.executor = executor;
    }

    protected <T> T execute(Function<String, T> apiCall) throws HubspotApiException
    {
        return executor.execute(apiCall);
    }

    protected static Gson configuredGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
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

    public interface RestExecutor
    {
        public <T> T execute(Function<String, T> apiCall) throws HubspotApiException;
    }
}
