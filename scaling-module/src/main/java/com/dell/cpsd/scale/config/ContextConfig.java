/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.scale.config;

import com.dell.cpsd.service.common.client.context.ConsumerContextConfig;
import org.springframework.context.annotation.Configuration;

/**
 * This is the client context configuration for the Scaling Module Service
 * <p>
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 * <p/>
 *
 * @since 1.0
 */
@Configuration
public class ContextConfig extends ConsumerContextConfig
{
    private static final String PROVIDER_NAME = "scaling-module";

    /**
     * ContextConfig constructor.
     *
     * @since 1.0
     */
    public ContextConfig()
    {
        super(PROVIDER_NAME, false);
    }

    //    /**
    //     * {@inheritDoc}
    //     */
    //    @Override
    //    public String requestExchange()
    //    {
    //        final String provider = PROVIDER_NAME.replace("-", ".");
    //
    //        return ProviderExchangeFormatter.formatRequestExchange(provider);
    //    }
}
