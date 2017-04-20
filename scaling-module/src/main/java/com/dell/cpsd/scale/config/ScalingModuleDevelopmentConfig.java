/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Development config that instantiates un-authenticated
 * connection factory.
 * <b>This should only be used for development purpose.</b>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class ScalingModuleDevelopmentConfig
{
    protected static final String PROFILE  = "development";
    private static final   String HOSTNAME = System.getProperty("container.id");
    /*
     * The properties configuration.
     */
    @Autowired
    private ScalingModulePropertiesConfig propertiesConfig;

    /**
     * Unsecured Rabbit connection factory. Uses default credentials on localhost.
     * This is for use in development environments.
     *
     * @return the connection factory
     */
    /*@Bean
    @Qualifier("unauthenticatedRabbitConnectionFactory")
    public ConnectionFactory unauthenticatedRabbitConnectionFactory()
    {
        final Integer amqpBrokerPort = propertiesConfig.rabbitPort();
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory(HOSTNAME, amqpBrokerPort);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }*/
}

