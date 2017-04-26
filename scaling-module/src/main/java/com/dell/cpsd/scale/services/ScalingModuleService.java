/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.CapabilityProvider;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.client.CapabilityRegistryException;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityRegistryLookupManager;
import com.dell.cpsd.hdp.capability.registry.client.callback.ListCapabilityProvidersResponse;
import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.MessageProperties;
import com.dell.cpsd.scale.api.TicketServiceRequest;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;


@Service
public class ScalingModuleService {

	private static final Logger LOG = LoggerFactory.getLogger(ScalingModuleService.class);

    private final ICapabilityRegistryLookupManager capabilityRegistryLookupManager;
    private final RabbitTemplate                   rabbitTemplate;
    private final AmqpAdmin                        amqpAdmin;
    private final Queue                            scaleResponseQueue;
    private final String replyTo;
	
    /**
     * 
     * @param capabilityRegistryLookupManager
     * @param rabbitTemplate
     * @param amqpAdmin
     * @param scaleResponseQueue
     * @param requestResponseMatcher
     * @param replyTo
     */
    @Autowired
    public ScalingModuleService(final ICapabilityRegistryLookupManager capabilityRegistryLookupManager,
            final RabbitTemplate rabbitTemplate, final AmqpAdmin amqpAdmin, @Qualifier("scaleResponseQueue") final Queue scaleResponseQueue,
             @Qualifier("replyTo") String replyTo)
    {
        this.capabilityRegistryLookupManager = capabilityRegistryLookupManager;
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.scaleResponseQueue = scaleResponseQueue;
        this.replyTo = replyTo;
    }
    
    
	
	public void createTicket(ApplicationPerformanceEvent message) {
		
		
		 final String requiredCapability = "raise-service-ticket";
	        try
	        {
	            final ListCapabilityProvidersResponse listCapabilityProvidersResponse = capabilityRegistryLookupManager
	                    .listCapabilityProviders(TimeUnit.SECONDS.toMillis(5));

	            for (final CapabilityProvider capabilityProvider : listCapabilityProvidersResponse.getResponse())
	            {
	                for (final Capability capability : capabilityProvider.getCapabilities())
	                {
	                    LOG.debug("Found capability {}", capability.getProfile());

	                    if (requiredCapability.equals(capability.getProfile()))
	                    {
	                        LOG.debug("Found matching capability {}", capability.getProfile());
	                        final List<EndpointProperty> endpointProperties = capability.getProviderEndpoint().getEndpointProperties();
	                        final Map<String, String> amqpProperties = endpointProperties.stream()
	                                .collect(Collectors.toMap(EndpointProperty::getName, EndpointProperty::getValue));

	                        final String requestExchange = amqpProperties.get("request-exchange");
	                        final String requestRoutingKey = amqpProperties.get("request-routing-key");

	                        final TopicExchange responseExchange = new TopicExchange(amqpProperties.get("response-exchange"));
	                        final String responseRoutingKey = amqpProperties.get("response-routing-key").replace("{replyTo}", "." + replyTo);

	                        amqpAdmin.declareBinding(BindingBuilder.bind(scaleResponseQueue).to(responseExchange).with(responseRoutingKey));

	                        LOG.debug("Adding binding {} {}", responseExchange.getName(), responseRoutingKey);
                            
	                        
                        	final UUID correlationId = UUID.randomUUID();
	                        
	                        MessageProperties messageProperties = new MessageProperties();
	                        messageProperties.setCorrelationId(correlationId.toString());
	                        messageProperties.setReplyTo(replyTo);
	                        messageProperties.setTimestamp(new Date());
	                        
	                        TicketServiceRequest requestMessage = new TicketServiceRequest();
	                        requestMessage.setRequestMessage("testing create ticket");
	                        
	                        requestMessage.setRequestType("create");
	                        requestMessage.setMessageProperties(messageProperties);           
	                      
	                                          
	                        rabbitTemplate.convertAndSend(requestExchange, requestRoutingKey, requestMessage);
	                     
	                    }
	                }
	            }
	        }
	        
	    catch (CapabilityRegistryException e)
	    {
	        LOG.error("Failed while looking up Capability Registry for {}", requiredCapability, e);
	    }
	    catch (ServiceTimeoutException e)
	    {
	        LOG.error("Service timed out while querying Capability Registry");
	    }
	    LOG.error("Unable to find required capability: {}", requiredCapability);
   
	}
}


