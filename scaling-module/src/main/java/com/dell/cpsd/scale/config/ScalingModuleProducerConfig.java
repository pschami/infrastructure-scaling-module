/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import com.dell.cpsd.scale.producer.ScalingModuleProducer;
import com.dell.cpsd.scale.producer.ScalingModuleProducerImpl;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This the Scaling Module producer spring config required
 * for instantiating the producer instance.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
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

    @Bean
    ScalingModuleProducer  applicationPerformancenProducer()
    {
        return new ScalingModuleProducerImpl(rabbitTemplate, applicationPerformanceEventExchange);
    }
  
}

