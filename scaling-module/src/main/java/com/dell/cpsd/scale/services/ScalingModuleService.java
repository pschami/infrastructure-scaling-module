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
import com.dell.cpsd.scale.api.TicketDetails;
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
			LOG.debug("Current Step : {}", event.getCurrentStep().getStepName());

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
			
			String currentStep = event.getCurrentStep().getStepName();
			LOG.debug("Current Step : {}", currentStep);

			if ("SUCCESS".equals(status) && (currentStep.equals("resolve") == false) && (currentStep.equals("completed") == false)) {
				
				Step nextStep = workflowSteps.get(currentStep);

				if (nextStep != null) {
					event.setCurrentStep(nextStep);
					LOG.debug("Next Step : {}", event.getCurrentStep().getStepName());
					event.excuteCurrentStep(message);

				} else {
					// TODO delete event from repository

					LOG.debug("Event Flow final step: {}", event.getCurrentStep().getStepName());
					eventRespository.delete(eventId);
				}

			}
		} else {
			LOG.info("Unable to find event : {}", eventId);
		}

	}
	
	//TODO Tidy up state machine flow

	public void processApproval(String eventId) {

		Event event = eventRespository.find(UUID.fromString(eventId));

		if (event != null) {
			
			String currentStep = event.getCurrentStep().getStepName();

			LOG.info("Current Step : {}", currentStep);

			event.excuteApproval();
			
			Step nextStep = workflowSteps.get(currentStep);

			if (nextStep != null) {
				event.setCurrentStep(nextStep);
				LOG.info("Next Step : {}", event.getCurrentStep().getStepName());
				event.excuteApproval();

			}

		}

		else {
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
						LOG.info("Found matching capability {}", capability.getProfile());
						final List<EndpointProperty> endpointProperties = capability.getProviderEndpoint()
								.getEndpointProperties();
						final Map<String, String> amqpProperties = endpointProperties.stream()
								.collect(Collectors.toMap(EndpointProperty::getName, EndpointProperty::getValue));

						final String requestExchange = amqpProperties.get("request-exchange");
						final String requestRoutingKey = amqpProperties.get("request-routing-key");

						final TopicExchange responseExchange = new TopicExchange(
								amqpProperties.get("response-exchange"));
						final String responseRoutingKey = amqpProperties.get("response-routing-key")
								.replace("{replyTo}", "." + replyTo);

						amqpAdmin.declareBinding(
								BindingBuilder.bind(scaleResponseQueue).to(responseExchange).with(responseRoutingKey));

						LOG.info("Adding binding {} {}", responseExchange.getName(), responseRoutingKey);

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
		} catch (CapabilityRegistryException e) {
			LOG.error("Failed while looking up Capability Registry for {}", requiredCapability, e);
		} catch (ServiceTimeoutException e) {
			LOG.error("Service timed out while querying Capability Registry");
		}
		//LOG.error("Unable to find required capability: {}", requiredCapability);
	}

	private void updateIncident(Event event, String incidentId, String updateTitle, String updateMsg) {
		TicketServiceRequest requestMessage = new TicketServiceRequest();
		TicketDetails ticketDetails = new TicketDetails(incidentId, updateTitle, updateMsg);
		requestMessage.setRequestMessage(updateMsg);
		requestMessage.setRequestType("update");
		requestMessage.setEventId(event.getId().toString());
		requestMessage.setTicketDetails(ticketDetails);
		sendRequest(requestMessage);
	}

	private ExecutableStep executeNewEvent() {
		return new ExecutableStep() {

			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {

				LOG.info("Raising New Ticket for: "  +  message.getDetails());
				
				String ticketTitle = message.getDetails();

				TicketServiceRequest requestMessage = new TicketServiceRequest();
				TicketDetails ticketDetails = new TicketDetails("",
						message.getHost() + " " + ticketTitle , message.getDetails());

				requestMessage.setRequestMessage(message.getDetails());
				requestMessage.setRequestType("create");
				requestMessage.setEventId(event.getId().toString());
				requestMessage.setTicketDetails(ticketDetails);
				sendRequest(requestMessage);

			}

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.error("Execute New Event should not be called for TicketServiceResponse ");

			}

			@Override
			public void executeApproval(Event event) {
				LOG.error("Execute New Event, no manual approval step defined");

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

				for (int i = 0; i < 1; i++) {

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					LOG.info("Hardware Capacity Overload in DataCenter A, Unable to scale autonomously, recommending capacity expansion");

					String incidentId = message.getTicketDetails().getIncidentId();
					String updateTitle = "Update Ticket";

					updateIncident(event, incidentId, updateTitle, event.getEventDetails().getDetails());

					updateIncident(event, incidentId, updateTitle,
							"[code]Hardware Capacity Overload in DataCenter A <br/><br/> No further hardware resources available, <br/><br/> Unable to scale autonomously Expand ESXi host capacity in Cluster 1. <br/><br/> ERP workload impacted[/code]");

					updateIncident(event, incidentId, updateTitle, "Hardware Expansion required:  "
							+ "[code]<br/><a href='http://localhost:8080/scale/api/approve/" + event.getId().toString()
							+ "'>Review Changes</a><br/><a href='http://localhost:8080/scale/api/approve/"
							+ event.getId().toString() + "'>Approve Changes</a><br/><br/>[/code] Request to deploy 4 Dell PowerEdge R630 HIGH-DENSITY: ALL-FLASH ");
					
					event.setIncidentId(incidentId);
				}

			}

			@Override
			public void executeApproval(Event event) {
				LOG.error("Execute Recommend, no manual approval step defined");

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
				LOG.info("Execute Resolve, no action taken, awaiting manual approval");

			}

			@Override
			public void executeApproval(Event event) {
				
				LOG.info("Expansion Approved for Incident: " + event.getIncidentId());

				TicketServiceRequest requestMessage = new TicketServiceRequest();
				TicketDetails ticketDetails = new TicketDetails(event.getIncidentId(), "Update Ticket",
						"Here is the recommendation ");
				requestMessage.setRequestMessage("Here is the approval");

				requestMessage.setRequestType("update");
				requestMessage.setEventId(event.getId().toString());
				requestMessage.setTicketDetails(ticketDetails);

				sendRequest(requestMessage);
				
				updateIncident(event, event.getIncidentId(), "Update Ticket", "Approved by Jane Doe");
				updateIncident(event, event.getIncidentId(), "Update Ticket", "Approved by Jane DoeVxRack Provisioning:"
						+ "====Expansion Complete===="
						+ "Successfully completed hardware expansion"
						+ "======Details====="
						+ "Configuring 9K5JMY1 172.17.1.7"
						+ "Configuring 9K5JMY2 172.17.1.8"
						+ "Configuring 9K5JMY3 172.17.1.9"
						+ "Configuring 9K5JMY4 172.17.1.10"
						+ "Installing ESXi……"
						+ "Added to Cluster 1"
						+ "");

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
				LOG.info("Execute Complete, no action taken, awaiting manual approval");

				
				
				

			}

			@Override
			public void executeApproval(Event event) {
				LOG.info("Closing Ticket for Incident: " + event.getIncidentId());
				
				updateIncident(event, event.getIncidentId(), "Update Ticket", "Compute and Storage Capacity Normal in DataCenter");
				
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				TicketServiceRequest requestMessage = new TicketServiceRequest();
				TicketDetails ticketDetails = new TicketDetails(event.getIncidentId(),
						"Close Ticket", "Close Ticket");
				requestMessage.setRequestMessage("Compute and Storage Capacity Normal in DataCenter");

				requestMessage.setRequestType("close");
				requestMessage.setEventId(event.getId().toString());
				requestMessage.setTicketDetails(ticketDetails);

				sendRequest(requestMessage);

			}
		};

	}

}
