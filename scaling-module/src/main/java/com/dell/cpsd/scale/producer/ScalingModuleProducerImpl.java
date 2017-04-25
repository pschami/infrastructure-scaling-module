/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.producer;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.config.ScalingModuleRabbitConfig;
import com.dell.cpsd.scale.services.ScalingModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Scaling Module Producer implementation class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ScalingModuleProducerImpl implements ScalingModuleProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScalingModuleProducerImpl.class);
    private Exchange       applicationPerformanceEventExchange;
    private RabbitTemplate rabbitTemplate;

    /**
     * @param rabbitTemplate                    Rabbit Template
     * @param applicationPerformanceEventExchange Event Exchange
     */
    public ScalingModuleProducerImpl(final RabbitTemplate rabbitTemplate, final Exchange applicationPerformanceEventExchange)
    {
        this.applicationPerformanceEventExchange = applicationPerformanceEventExchange;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishApplicationPerformanceEvent(ApplicationPerformanceEvent event) throws ScalingModuleException
    {
        if (event == null)
        {
            throw new ScalingModuleException("Scaling Module message is null.");
        }

        rabbitTemplate.convertAndSend(applicationPerformanceEventExchange.getName(),
                ScalingModuleRabbitConfig.ROUTING_KEY_APPLICATION_PERFORMANCE, event);

    }
    
}
    

