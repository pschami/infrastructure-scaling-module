/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import com.dell.cpsd.service.common.client.context.ConsumerContextConfig;
import org.springframework.context.annotation.Configuration;

/**
 * This is the client context configuration for the Scaling Module Service
 * <p>
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 *
 * @since 0.1
 */
@Configuration
public class ContextConfig extends ConsumerContextConfig
{
    private static final String PROVIDER_NAME = "scaling-module";

    /**
     * ContextConfig constructor.
     *
     * @since 0.1
     */
    public ContextConfig()
    {
        super(PROVIDER_NAME, false);
    } 
}
