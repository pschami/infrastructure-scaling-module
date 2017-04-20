package com.dell.cpsd.scale.log;

import com.dell.cpsd.scale.i18n.ERMessageBundle;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Endpoint registration message code enum.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public enum ERMessageCode
{
    RABBIT_AMQP_LISTENER_E(1001, "ER1001E"),
    POLLING_TASK_E(1004, "ER1004E"),
    QUERY_TASK_FILTER_NULL_E(1005, "ER1005E"),
    QUERY_TASK_FILTER_TYPE_NULL_E(1006, "ER1006E"),
    SERVICE_NULL_E(1007, "ER1007E"),
    ENDPOINT_REGISTRATION_EXCEPTION_E(1008, "ER1008E"),
    QUERY_TASK_RESPONSE_NULL_E(1009, "ER1009E");

    /*
     * The path to the resource bundle
     */
    private static ResourceBundle BUNDLE = ResourceBundle.getBundle(ERMessageBundle.class.getName());
    /**
     * The message code.
     */
    private final String messageCode;
    /**
     * The error code.
     */
    private       int    errorCode;

    /**
     * ERMessageCode constructor
     *
     * @param messageCode The message code.
     * @since SINCE-TBD
     */
    ERMessageCode(int errorCode, String messageCode)
    {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
    }

    /**
     * This returns the error code.
     *
     * @return The error code.
     * @since SINCE-TBD
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * This returns the message code.
     *
     * @return The message code.
     * @since SINCE-TBD
     */
    public String getMessageCode()
    {
        return messageCode;
    }

    /**
     * This returns the message.
     *
     * @return The error text.
     * @since SINCE-TBD
     */
    public String getMessage()
    {
        try
        {
            return BUNDLE.getString(messageCode);

        }
        catch (MissingResourceException exception)
        {
            return messageCode;
        }
    }

    /**
     * This formats the  message using the array of parameters.
     *
     * @param params The message parameters.
     * @return The localized message populated with the parameters.
     * @since SINCE-TBD
     */
    public String getMessage(Object... params)
    {
        String message;

        try
        {
            message = BUNDLE.getString(messageCode);

        }
        catch (MissingResourceException exception)
        {
            return messageCode;
        }

        if ((params == null) || (params.length == 0))
        {
            return message;
        }

        return MessageFormat.format(message, params);
    }

}
