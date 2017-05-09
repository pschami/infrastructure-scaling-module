/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 */

package com.dell.cpsd.scale.capabilityintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.dell.cpsd.hdp.capability.registry.client.CapabilityRegistryException;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityRegistryLookupManager;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBindingService;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityMatcher;
import com.dell.cpsd.hdp.capability.registry.client.binder.rpc.AmqpRpcCapabilityBindingService;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

/**
 * A class to handle the integration between the Scaling Module and the Capability Registry.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
 */
@Component
public class CapabilityRegistryIntegration
{
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CapabilityRegistryIntegration.class);

         
    /**
     * Creates a capability binder to bind the event 
     * to  application-performance-event exchange
     * 
     * @param capabilityRegistryLookupManager
     * @param amqpAdmin
     * @param queue
     * @param replyTo
     * @return
     */
    @Bean
    public CapabilityBinder CapabilityRegistryBinder(
            @Autowired ICapabilityRegistryLookupManager capabilityRegistryLookupManager,
            @Autowired AmqpAdmin amqpAdmin,
            @Autowired @Qualifier("scaleApmEventsQueue") Queue queue,
            @Autowired String replyTo)
    {
        CapabilityBindingService bindingService = new AmqpRpcCapabilityBindingService(capabilityRegistryLookupManager, amqpAdmin, queue,
                replyTo);

        CapabilityMatcher endpointMatcher = new CapabilityMatcher()
                .withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                .withProfile("application-performance-event");

        CapabilityBinder binder = new CapabilityBinder(bindingService, endpointMatcher);
        binder.register(capabilityRegistryLookupManager);

        return binder;
    }
    /**
     * Binds capabilityBinder on application event
     * @param capabilityBinder
     * @return
     */

    @Bean
    public ApplicationListener<ContextRefreshedEvent> contextRefreshedEventListener(
            @Autowired CapabilityBinder capabilityBinder)
    {
        return new ApplicationListener<ContextRefreshedEvent>()
        {
            /**
             * When spring context is fully loaded, do the binding
             *
             * @param contextRefreshedEvent
             */
            @Override
            public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
            {
                try
                {
                    capabilityBinder.bind();
                }
                catch (ServiceTimeoutException | CapabilityRegistryException e)
                {
                    LOGGER.error("Unable to bind for capability", e);
                }
            }
        };
    } 

   
}
