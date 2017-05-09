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
import com.dell.cpsd.scale.consumer.ApplicationPerformanceEventHandler;
import com.dell.cpsd.scale.consumer.TicketServiceResponseHandler;
import com.dell.cpsd.scale.services.ScalingModuleService;

/**
 * The Scaling Module Consumer Spring Config class
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
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
  

    /*
     * The business logic of the service
     */
    
    @Autowired
    private ScalingModuleService scalingModuleService;
  
    /*
     * The events queue
     */
    
    @Autowired
    private Queue scaleApmEventsQueue;
    
    /*
     * The response queue
     */
    
    @Autowired
    private Queue scaleResponseQueue;

    /**
     * This bean instantiates and returns the Simple message
     * listener container.
     *
     * @return Simple message listener container
     */
    @Bean
    SimpleMessageListenerContainer scalingModuleListenerContainer()
    {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setQueues(scaleApmEventsQueue, scaleResponseQueue);
        container.setMessageListener(scalingModuleListener());
        container.setErrorHandler(new DefaultContainerErrorHandler("scalingModuleListenerContainer"));
        return container;
    }

    

	/**
	 * This is the message listener for Scaling Module listener container.
	 * Bean which creates two handlers; 
	 * a handler for performance events and 
	 * a handler for ticket service responses
	 * @return Default message listener
	 */
    @Bean
    DefaultMessageListener scalingModuleListener()
    {
    	return new DefaultMessageListener(messageConverter, applicationPerformanceEventHandler(), ticketServiceResponseHandler());
		
    }
    /**
     * Ticket Service Response message handler
     * @return
     */
    @Bean
	TicketServiceResponseHandler ticketServiceResponseHandler() {
		
		 return new TicketServiceResponseHandler(scalingModuleService);
		
	}
    
    /**
     * Application Performance Event handler
     *
     * @return Default message listener
     */
    @Bean
    ApplicationPerformanceEventHandler applicationPerformanceEventHandler()
    {
        return new ApplicationPerformanceEventHandler(scalingModuleService);
    }
    
   
}

