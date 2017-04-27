/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.services;

import java.util.Date;
import java.util.HashMap;
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
import com.dell.cpsd.scale.api.TicketServiceResponse;
import com.dell.cpsd.scale.model.Event;
import com.dell.cpsd.scale.model.ExecutableStep;
import com.dell.cpsd.scale.model.Step;
import com.dell.cpsd.scale.repository.EventRepository;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

@Service
public class ScalingModuleService {

	private static final Logger LOG = LoggerFactory.getLogger(ScalingModuleService.class);

	private final ICapabilityRegistryLookupManager capabilityRegistryLookupManager;
	private final RabbitTemplate rabbitTemplate;
	private final AmqpAdmin amqpAdmin;
	private final Queue scaleResponseQueue;
	private final String replyTo;
	private final EventRepository eventRespository;
	private final Map<String, Step> workflowSteps;
	private final Step newEvent;
	private final Step recommend;
	private final Step resolve;
	private final Step completed;

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
			final RabbitTemplate rabbitTemplate, final AmqpAdmin amqpAdmin,
			@Qualifier("scaleResponseQueue") final Queue scaleResponseQueue, @Qualifier("replyTo") String replyTo,
			final EventRepository eventRespository) {
		this.capabilityRegistryLookupManager = capabilityRegistryLookupManager;
		this.rabbitTemplate = rabbitTemplate;
		this.amqpAdmin = amqpAdmin;
		this.scaleResponseQueue = scaleResponseQueue;
		this.replyTo = replyTo;
		this.eventRespository = eventRespository;

		this.newEvent = new Step("newEvent", executeNewEvent());
		this.recommend = new Step("recommend", executeRecommend());
		this.resolve = new Step("resolve", executeResolve());
		this.completed = new Step("completed", executeComplete(), true);

		workflowSteps = new HashMap<>();
		workflowSteps.put(newEvent.getStepName(), recommend);
		workflowSteps.put(recommend.getStepName(), resolve);
		workflowSteps.put(resolve.getStepName(), completed);
		workflowSteps.put(completed.getStepName(), null);
	}

	public void processApplicationPerformanceEvent(ApplicationPerformanceEvent message) {

		UUID eventId = UUID.fromString(message.getId());
		Event event = eventRespository.find(eventId);

		if (event == null) {
			event = new Event(eventId, message, newEvent);

			LOG.info("createing new event for : {}", message);
			LOG.info("Current Step : {}", event.getCurrentStep().getStepName());

			event.excuteCurrentStep(message);

			eventRespository.save(event);

		} else {

			LOG.info("event already created for: {}", message);
		}

	}

	public void processTicketServiceResponse(TicketServiceResponse message) {

		UUID eventId = UUID.fromString(message.getEventId());

		Event event = eventRespository.find(eventId);

		if (event != null) {

			String status = message.getResponseCode();

			if ("SUCCESS".equals(status)) {
				LOG.info("Current Step : {}", event.getCurrentStep().getStepName());
				Step nextStep = workflowSteps.get(event.getCurrentStep().getStepName());
				
				if(nextStep != null)
				{				
					event.setCurrentStep(nextStep);
					LOG.info("Next Step : {}", event.getCurrentStep().getStepName());
					event.excuteCurrentStep(message);					
					
				}
				else
				{
					//TODO delete event from repository
					
					LOG.info("Event Flow final step: {}", event.getCurrentStep().getStepName());
					eventRespository.delete(eventId);
				}

			}
		} else {
			LOG.info("Unable to find event : {}", eventId);
		}

	}
	
	private void sendRequest(TicketServiceRequest requestMessage) {
		final String requiredCapability = "raise-service-ticket";
		try {
			final ListCapabilityProvidersResponse listCapabilityProvidersResponse = capabilityRegistryLookupManager
					.listCapabilityProviders(TimeUnit.SECONDS.toMillis(5));

			for (final CapabilityProvider capabilityProvider : listCapabilityProvidersResponse.getResponse()) {
				for (final Capability capability : capabilityProvider.getCapabilities()) {
					LOG.debug("Found capability {}", capability.getProfile());

					if (requiredCapability.equals(capability.getProfile())) {
						LOG.debug("Found matching capability {}", capability.getProfile());
						final List<EndpointProperty> endpointProperties = capability.getProviderEndpoint()
								.getEndpointProperties();
						final Map<String, String> amqpProperties = endpointProperties.stream().collect(
								Collectors.toMap(EndpointProperty::getName, EndpointProperty::getValue));

						final String requestExchange = amqpProperties.get("request-exchange");
						final String requestRoutingKey = amqpProperties.get("request-routing-key");

						final TopicExchange responseExchange = new TopicExchange(
								amqpProperties.get("response-exchange"));
						final String responseRoutingKey = amqpProperties.get("response-routing-key")
								.replace("{replyTo}", "." + replyTo);

						amqpAdmin.declareBinding(BindingBuilder.bind(scaleResponseQueue).to(responseExchange)
								.with(responseRoutingKey));

						LOG.debug("Adding binding {} {}", responseExchange.getName(), responseRoutingKey);

						final UUID correlationId = UUID.randomUUID();

						MessageProperties messageProperties = new MessageProperties();
						messageProperties.setCorrelationId(correlationId.toString());
						messageProperties.setReplyTo(replyTo);
						messageProperties.setTimestamp(new Date());								
						
						requestMessage.setMessageProperties(messageProperties);

						rabbitTemplate.convertAndSend(requestExchange, requestRoutingKey, requestMessage);

					}
				}
			}
		}	catch (CapabilityRegistryException e) {
			LOG.error("Failed while looking up Capability Registry for {}", requiredCapability, e);
		} catch (ServiceTimeoutException e) {
			LOG.error("Service timed out while querying Capability Registry");
		}
		LOG.error("Unable to find required capability: {}", requiredCapability);
	}


	private ExecutableStep executeNewEvent() {
		return new ExecutableStep() {

			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {

				LOG.info("Execute New Event");
				
				TicketServiceRequest requestMessage = new TicketServiceRequest();
				requestMessage.setRequestMessage(message.getDetails());

				requestMessage.setRequestType("create");
				requestMessage.setEventId(event.getId().toString());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendRequest(requestMessage);

			}
			

			
			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.error("Execute New Event should not be called for TicketServiceResponse ");

			}
		};

	}

	private ExecutableStep executeRecommend() {
		return new ExecutableStep() {

			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {
				LOG.error("Execute Recommend should not be called for ApplicationPerformanceEvent ");
				
			}

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.info("Execute Recommend");
				
				TicketServiceRequest requestMessage = new TicketServiceRequest();
				requestMessage.setRequestMessage("Here is the recommendation");

				requestMessage.setRequestType("update");
				requestMessage.setEventId(event.getId().toString());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendRequest(requestMessage);
				
				
			}
		};

	}

	private ExecutableStep executeResolve() {
		return new ExecutableStep() {
			
			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {
				LOG.error("Execute Resolve should not be called for ApplicationPerformanceEvent ");
				
			}

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.info("Execute Resolve");
				
				TicketServiceRequest requestMessage = new TicketServiceRequest();
				requestMessage.setRequestMessage("Here is the approval");

				requestMessage.setRequestType("approve");
				requestMessage.setEventId(event.getId().toString());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendRequest(requestMessage);
				
				
			}
		};

	}

	private ExecutableStep executeComplete() {
		return new ExecutableStep() {
			

			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {
				LOG.error("Execute Complete should not be called for ApplicationPerformanceEvent ");
				
			}

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.info("Execute Resolve");
				
				TicketServiceRequest requestMessage = new TicketServiceRequest();
				requestMessage.setRequestMessage("Here is the approval");

				requestMessage.setRequestType("close");
				requestMessage.setEventId(event.getId().toString());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendRequest(requestMessage);
				
				
			}
		};

	}

}
