/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.common.rabbitmq.MessageAnnotationProcessor;
import com.dell.cpsd.common.rabbitmq.config.IRabbitMqPropertiesConfig;
import com.dell.cpsd.hdp.capability.registry.api.RegisterCapabilityProviderMessage;
import com.dell.cpsd.hdp.capability.registry.api.UnregisterCapabilityProviderMessage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Scaling Module Rabbit config.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
@Import(ScalingModulePropertiesConfig.class)
public class ScalingModuleRabbitConfig
{
    
    /**
     * The routing key for Scaling Module event
     */
    public static final  String ROUTING_KEY_APPLICATION_PERFORMANCE         = "dell.cpsd.apm";
    
    
    public static final  String BINDING_APM_REQUEST                      = "dell.cpsd.apm.request";
      
    /**
     * The name of the ping queue for Scaling Module service
     */
    private static final String QUEUE_SCALE_REQUEST                        = "queue.dell.cpsd.scale.request";
    
    
    /*
     * The logger for this class.
     */
    private static final Logger LOGGER                                  = LoggerFactory.getLogger(ScalingModuleRabbitConfig.class);
    /**
     * The retry template information
     */
    private static final int    MAX_ATTEMPTS                            = 10;
    private static final int    INITIAL_INTERVAL                        = 100;
    private static final double MULTIPLIER                              = 2.0;
    private static final int    MAX_INTERVAL                            = 50000;
    
    
    /**
     * Scaling Module Event Exchange
     */
    private static final String EXCHANGE_APPLICATION_PERFORMANCE_EVENT    = "exchange.dell.cpsd.apm.event";
   
    /*
     * The RabbitMQ connection factory.
     */
    @Autowired
    @Qualifier("rabbitConnectionFactory")
    private ConnectionFactory rabbitConnectionFactory;

    /*
     * The configuration properties for the service.
     */
    @Autowired
    @Qualifier("rabbitPropertiesConfig")
    private IRabbitMqPropertiesConfig propertiesConfig;

    /**
     * This returns the host name for the service.
     *
     * @return The host name for the service.
     * @since 1.0
     */
    @Bean
    String hostName()
    {
        try
        {
            return System.getProperty("container.id");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to identify container.id", e);
        }
    }

    /**
     * This is the reply to for the response messages.
     *
     * @return Reply to for the response messages
     */
    @Bean
    public String replyTo()
    {
        return propertiesConfig.applicationName() + "." + hostName();
    }

    /**
     * This returns the RabbitMQ template.
     *
     * @return The RabbitMQ template.
     */
    @Bean
    RabbitTemplate rabbitTemplate()
    {
        final RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setMessageConverter(messageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    /**
     * This returns the <code>RetryTemplate</code> for the <code>RabbitTemplate
     * </code>.
     *
     * @return The <code>RetryTemplate</code>.
     */
    @Bean
    RetryTemplate retryTemplate()
    {
        final RetryTemplate retryTemplate = new RetryTemplate();

        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);

        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    /**
     * This returns the <code>MessageConverter</code> for the
     * <code>RabbitTemplate</code>.
     *
     * @return The <code>MessageConverter</code> for the template.
     */
    @Bean
    public MessageConverter messageConverter()
    {
        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.setClassMapper(classMapper());
        messageConverter.setCreateMessageIds(true);

        final ObjectMapper objectMapper = new ObjectMapper();

        // use ISO8601 format for dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        messageConverter.setJsonObjectMapper(objectMapper);

        // ignore properties we don't need or aren't expecting
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        messageConverter.setJsonObjectMapper(objectMapper);

        return messageConverter;
    }

    /**
     * This returns the <code>ClassMapper</code> for the message converter.
     *
     * @return The <code>ClassMapper</code> for the message converter.
     */
    @Bean
    ClassMapper classMapper()
    {
        final DefaultClassMapper classMapper = new DefaultClassMapper();
        final Map<String, Class<?>> classMappings = new HashMap<>();
        final List<Class<?>> messageClasses = new ArrayList<>();

        messageClasses.add(ApplicationPerformanceEvent.class);
        messageClasses.add(RegisterCapabilityProviderMessage.class);
        messageClasses.add(UnregisterCapabilityProviderMessage.class);

        final MessageAnnotationProcessor messageAnnotationProcessor = new MessageAnnotationProcessor();

        messageAnnotationProcessor.process(classMappings::put, messageClasses);

        classMapper.setIdClassMapping(classMappings);

        return classMapper;
    }

    /**
     * This returns the <code>FanoutExchange</code> for the Scaling Module
     * events.
     *
     * @return The <code>FanoutExchange</code> for Scaling Module messages.
     * @since 1.0
     */
    @Bean
    TopicExchange applicationPerformanceEventExchange()
    {
        return new TopicExchange(EXCHANGE_APPLICATION_PERFORMANCE_EVENT);
    }

   
    @Bean
    Queue queryRequestQueue()
    {
        return new Queue(QUEUE_SCALE_REQUEST);
    }

   
    /*
     * TODO request queue binding
    
    @Bean
    public Binding endpointRegistrationRequestQueueBinding()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(BINDING_APM_REQUEST);

        String binding = builder.toString();

        return BindingBuilder.bind(queryRequestQueue()).to(endpointRegistrationRequestExchange()).with(binding);
    }*/

    /**
     * This returns the <code>AmqpAdmin</code> for the connection factory.
     *
     * @return The AMQP admin object for the connection factory.
     * @since 1.0
     */
    @Bean
    AmqpAdmin amqpAdmin()
    {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

}

