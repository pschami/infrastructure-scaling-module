/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This the Scaling Module producer spring config required
 * for instantiating the producer instance.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
 */
@Configuration
@ComponentScan(ScalingModuleProductionConfig.CONFIG_PACKAGE)
public class ScalingModuleProducerConfig
{
    /**
     * The Spring RabbitMQ template.
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Exchange applicationPerformanceEventExchange;

    @Autowired
    private String hostName;
  
  
}

