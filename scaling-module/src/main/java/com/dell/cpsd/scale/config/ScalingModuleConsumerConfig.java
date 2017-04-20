/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import javax.annotation.Resource;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.dell.cpsd.common.rabbitmq.consumer.handler.DefaultMessageListener;
import com.dell.cpsd.common.rabbitmq.context.builder.DefaultContainerErrorHandler;
import com.dell.cpsd.scale.consumer.ScalingModuleConsumer;

/**
 * The Endpoint Registration Consumer Spring Config class
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
@ComponentScan(basePackages = {ScalingModuleProductionConfig.CONFIG_PACKAGE})
public class ScalingModuleConsumerConfig
{
   
    /*
     * The RabbitMQ Connection Factory.
     */
    @Autowired
    @Qualifier("rabbitConnectionFactory")
    private ConnectionFactory             rabbitConnectionFactory;
    @Resource(name = "rabbitTemplate")
    private RabbitTemplate                rabbitTemplate;
  
    /*
     * The message converter to be used.
     */
    @Autowired
    private MessageConverter              messageConverter;
  

    @Autowired
    private Queue queryRequestQueue;

    /**
     * This bean instantiates and returns the Simple message
     * listener container.
     *
     * @return Simple message listener container
     */
    @Bean
    SimpleMessageListenerContainer endpointRegistrationListenerContainer()
    {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setQueues(queryRequestQueue);
        container.setMessageListener(scalingModuleListener());
        container.setErrorHandler(new DefaultContainerErrorHandler("endpointRegistrationListenerContainer"));
        return container;
    }

    /**
     * This is the message listener for Endpoint Registration listener container.
     *
     * @return Default message listener
     */
    @Bean
    ScalingModuleConsumer scalingModuleMessageHandler()
    {
        return new ScalingModuleConsumer();
    }


    @Bean
    DefaultMessageListener scalingModuleListener()
    {
    	return new DefaultMessageListener(messageConverter, scalingModuleMessageHandler());
		
    }
 
}

