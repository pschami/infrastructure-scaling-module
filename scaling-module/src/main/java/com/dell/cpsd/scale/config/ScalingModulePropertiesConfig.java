/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import com.dell.cpsd.common.rabbitmq.config.RabbitMQPropertiesConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 *  Scaling Module properties config.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
 */
@Configuration
@PropertySources({@PropertySource(value = "classpath:META-INF/spring/apm-nagios/rabbitmq.properties"),
        @PropertySource(value = "file:/opt/dell/cpsd/registration-services/apm-nagios/conf/rabbitmq-config.properties", ignoreResourceNotFound = true)})
@Qualifier("rabbitPropertiesConfig")
public class ScalingModulePropertiesConfig extends RabbitMQPropertiesConfig
{
    /**
     * Scaling Module Properties Config constructor
     */
    public ScalingModulePropertiesConfig()
    {
        super();
    }
}

