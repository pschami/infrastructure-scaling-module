/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.services;



/**
 * Scaling Module custom exception class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
 */
public class ScalingModuleException extends Exception
{
    /*
     * The serial version identifier.
     */
    private static final long serialVersionUID = 13264591L;

    /**
     * ScalingModuleException constructor.
     *
     * @param message The exception message.
     * @since 0.1
     */
    public ScalingModuleException(final String message)
    {
        super(message);
    }

    /**
     * ScalingModuleException constructor.
     *
     * @param cause The cause of the exception.
     * @since 0.1
     */
    public ScalingModuleException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * ScalingModuleException constructor.
     *
     * @param message The exception message.
     * @param cause   The cause of the exception.
     * @since 0.1
     */
    public ScalingModuleException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}

