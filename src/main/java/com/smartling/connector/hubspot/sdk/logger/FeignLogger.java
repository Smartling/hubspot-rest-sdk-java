package com.smartling.connector.hubspot.sdk.logger;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;
import static feign.Util.valuesOrEmpty;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class FeignLogger extends Logger
{
    private final org.slf4j.Logger logger;

    public FeignLogger()
    {
        this(FeignLogger.class);
    }

    public FeignLogger(Class<?> clazz)
    {
        this(LoggerFactory.getLogger(clazz));
    }

    public FeignLogger(String name)
    {
        this(LoggerFactory.getLogger(name));
    }

    FeignLogger(org.slf4j.Logger logger)
    {
        this.logger = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(methodTag(configKey) + format);
        }
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request)
    {
        if (logger.isTraceEnabled())
        {
            List<StructuredArgument> arguments = new ArrayList<>();

            try (Formatter message = new Formatter())
            {
                message.format("{} --->\n{} {}\n");
                arguments.add(value("methodTag", methodName(configKey)));
                arguments.add(value("method", request.httpMethod().name()));
                arguments.add(value("url", request.url()));
                if (logLevel.ordinal() >= Level.HEADERS.ordinal())
                {

                    for (String field : request.headers().keySet())
                    {
                        for (String value : valuesOrEmpty(request.headers(), field))
                        {
                            if (!AUTHORIZATION.equalsIgnoreCase(field))
                            {
                                message.format("%s: %s\n", field, value);
                            }
                        }
                    }

                    int bodyLength = 0;
                    if (request.body() != null)
                    {
                        bodyLength = request.length();
                        if (logLevel.ordinal() >= Level.FULL.ordinal())
                        {
                            String bodyText =
                                    request.charset() != null
                                            ? new String(request.body(), request.charset())
                                            : null;
                            message.format("\n%s\n", bodyText != null ? bodyText : "Binary data");
                        }
                    }
                    arguments.add(value("bodyLength", bodyLength));
                }

                arguments.add(value("direction", "request"));
                logger.trace(message.toString(), arguments.toArray());
            }
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey,
                                              Level logLevel,
                                              Response response,
                                              long elapsedTime)
            throws IOException
    {
        if (logger.isTraceEnabled())
        {
            List<StructuredArgument> arguments = new ArrayList<>();

            try (Formatter message = new Formatter())
            {
                message.format("{} <---\n{} {}\n");
                arguments.add(value("methodTag", methodName(configKey)));
                arguments.add(value("method", response.request().httpMethod().name()));
                arguments.add(value("url", response.request().url()));

                String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0
                        ? " " + response.reason()
                        : "";
                int status = response.status();
                message.format("HTTP/1.1 {}{} ({}ms)\n");
                arguments.add(value("status", status));
                arguments.add(value("reason", reason));
                arguments.add(value("elapsedTime", elapsedTime));

                arguments.add(value("direction", "response"));
                if (logLevel.ordinal() >= Level.HEADERS.ordinal())
                {

                    for (String field : response.headers().keySet())
                    {
                        for (String value : valuesOrEmpty(response.headers(), field))
                        {
                            message.format("%s: %s\n", field, value);
                        }

                    }

                    int bodyLength = 0;
                    if (response.body() != null && !(status == 204 || status == 205))
                    {
                        // HTTP 204 No Content "...response MUST NOT include a message-body"
                        // HTTP 205 Reset Content "...response MUST NOT include an entity"
                        if (logLevel.ordinal() >= Level.FULL.ordinal())
                        {
                            message.format("\n");
                        }
                        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                        bodyLength = bodyData.length;
                        if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0)
                        {
                            message.format("%s\n", decodeOrDefault(bodyData, UTF_8, "Binary data"));
                        }
                        arguments.add(value("bodyLength", bodyLength));

                        logger.trace(message.toString(), arguments.toArray());
                        return response.toBuilder().body(bodyData).build();
                    } else
                    {
                        arguments.add(value("bodyLength", bodyLength));
                    }
                }
                logger.trace(message.toString(), arguments.toArray());
            }
            return response;
        }
        return response;
    }

    @Override
    protected IOException logIOException(String configKey,
                                         Level logLevel,
                                         IOException ioe,
                                         long elapsedTime)
    {
        List<StructuredArgument> arguments = new ArrayList<>();

        try (Formatter message = new Formatter())
        {
            message.format("{} <---\nERROR ({}ms)\n");
            arguments.add(value("methodTag", methodName(configKey)));
            arguments.add(value("elapsedTime", elapsedTime));

            logger.trace(message.toString(), arguments.toArray(), ioe);
        }

        return ioe;
    }

    private static String methodName(String configKey)
    {
        return configKey.substring(0, configKey.indexOf('('));
    }
}
