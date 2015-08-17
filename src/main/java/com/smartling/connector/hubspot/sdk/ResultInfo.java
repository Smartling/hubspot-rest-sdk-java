package com.smartling.connector.hubspot.sdk;

public class ResultInfo
{
    private boolean succeeded;
    private String  message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    public boolean isSucceeded()
    {
        return succeeded;
    }

    public void setSucceeded(final boolean succeeded)
    {
        this.succeeded = succeeded;
    }
}
