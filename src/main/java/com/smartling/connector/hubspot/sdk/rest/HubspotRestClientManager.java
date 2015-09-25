package com.smartling.connector.hubspot.sdk.rest;

import java.util.function.Function;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClientManager;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.HubspotPageClient;
import com.smartling.connector.hubspot.sdk.rest.AbstractHubspotRestClient.RestExecutor;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;

import feign.FeignException;

public class HubspotRestClientManager implements HubspotClientManager, RestExecutor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HubspotRestClientManager.class);

    private final Configuration configuration;
    private final TokenProvider tokenProvider;

    public HubspotRestClientManager(final Configuration configuration)
    {
        this.tokenProvider = createTokenProvider(configuration);
        this.configuration = configuration;
    }

    @Override
    public HubspotPageClient getPageClient()
    {
        return new HubspotRestPageClient(configuration, this);
    }

    @Override
    public HubspotFormClient getFormClient()
    {
        return new HubspotRestFormClient(configuration, this);
    }

    @Override
    public <T> T execute(Function<String, T> apiCall) throws HubspotApiException
    {
        try
        {
            return apiCall.apply(tokenProvider.getTokenData().getAccessToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected TokenProvider createTokenProvider(final Configuration configuration)
    {
        TokenProvider provider = new HubspotTokenProvider(configuration);
        String decoratorClassName = configuration.getPropertyValue(TokenProvider.TOKEN_PROVIDER_DECORATOR_CLASS);
        if (StringUtils.isNotBlank(decoratorClassName))
        {
            try
            {
                provider = ConstructorUtils.invokeConstructor((Class<TokenProvider>)ClassUtils.getClass(decoratorClassName), configuration, provider);
                LOGGER.info("tokenProvider is decorated by {} class", decoratorClassName);
            }
            catch (Exception e)
            {
                LOGGER.error("Cannot decorate tokenProvider by {} decorator", decoratorClassName, e);
            }
        }
        return provider;
    }
}
