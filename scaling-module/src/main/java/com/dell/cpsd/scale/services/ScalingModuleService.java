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
import com.dell.cpsd.scale.consumer.RequestResponseMatcher;
import com.dell.cpsd.scale.model.Event;
import com.dell.cpsd.scale.model.ExecutableStep;
import com.dell.cpsd.scale.model.Step;
import com.dell.cpsd.scale.repository.EventRepository;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

/**
 * Scaling Module Service:
 * <br/>
 * This service is an example of a customer state machine
 * which handles application performance events, triggers the
 * creation/update/closure of service tickets based on a defined workflow.
 * 
 * It is written as a single class for ease of interpretation. One should
 * consider breaking this out into its constituent objects.
 * 
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 */

@Service
public class ScalingModuleService {

	private static final Logger LOG = LoggerFactory.getLogger(ScalingModuleService.class);

	// Looks up for specific capabilities
	private final ICapabilityRegistryLookupManager capabilityRegistryLookupManager;
	// AMQP template
	private final RabbitTemplate rabbitTemplate;
	private final AmqpAdmin amqpAdmin;
	// queue for handling responses
	private final Queue scaleResponseQueue;
	// the reply to
	private final String replyTo;

	// stores map of requests to responses
	private final RequestResponseMatcher matcher;
	// stores events in memory
	private final EventRepository eventRespository;
	// stores the steps of the workflow
	private final Map<String, Step> workflowSteps;

	// definition of each step in the workflow
	private final Step newEvent;
	private final Step resolve;
	private final Step completed;

	/**
	 * Constructor Instantiates the ScalingModuleService, the workflow and the
	 * steps in the flow
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
		this.matcher = new RequestResponseMatcher();

		// defines each step and a callback function the execute for each step
		this.newEvent = new Step("newEvent", executeNewEvent());
		this.resolve = new Step("resolve", executeResolve());
		this.completed = new Step("completed", executeComplete(), true);

		// defines the order of the workflow
		workflowSteps = new HashMap<>();
		workflowSteps.put(newEvent.getStepName(), resolve);
		workflowSteps.put(resolve.getStepName(), completed);
		workflowSteps.put(completed.getStepName(), null);
	}

	/**
	 * Process application performance events, triggers the creation and storage
	 * of event pojo to track the state of the event. Executes the first step in
	 * the workflow for new events
	 * 
	 * @param message
	 */

	public void processApplicationPerformanceEvent(ApplicationPerformanceEvent message) {

		UUID eventId = UUID.fromString(message.getId());
		Event event = eventRespository.find(eventId);

		// new event, create new event object and trigger the first step
		if (event == null) {
			event = new Event(eventId, message, newEvent);

			LOG.info("creating new event for : {}", message);
			LOG.debug("Current Step : {}", event.getCurrentStep().getStepName());

			event.excuteCurrentStep(message);

			eventRespository.save(event);

		} else {
			// existing event, do nothing
			LOG.info("event already created for: {}", message);
		}

	}

	/**
	 * Process Ticket responses, finds the event related to the response
	 * using correlation id and triggers the ticket responses handler "executeStep" for 
	 * the current step in the workflow.
	 *  
	 * @param message
	 */

	public void processTicketServiceResponse(TicketServiceResponse message) {

		String correlationId = message.getMessageProperties().getCorrelationId();

		UUID eventId = matcher.received(correlationId);

		if (eventId != null) {

			Event event = eventRespository.find(eventId);

			if (event != null) {

				event.excuteCurrentStep(message);
			}		

			else {
				LOG.info("Unable to find event : {}", eventId);
			}
		} else {
			LOG.error("Unable to find event for correlation Id : {}", correlationId);
		}

	}

	/**
	 * Process approval for an event and trigger the execution of
	 * and triggers the ticket approval handler "executeApproval" for 
	 * the current step in the workflow.
	 * @param eventId
	 */

	public void processApproval(String eventId) {

		Event event = eventRespository.find(UUID.fromString(eventId));

		if (event != null) {

			String currentStep = event.getCurrentStep().getStepName();

			LOG.info("Current Step : {}", currentStep);

			event.excuteApproval();
		}

		else {
			LOG.info("Unable to find event : {}", eventId);
		}

	}
	
	/**
	 * Sends a ticket service request.
	 * Looks up raise-service-ticket capability to identify request and response exchanges.
	 * Sends a ticket service request to the ticket service request exchange. 
	 * Binds to the response exchange of the ticket service to receive responses.
	 * 
	 * @param requestMessage
	 */

	private void sendRequest(TicketServiceRequest requestMessage) {
		
		//the capability required
		final String requiredCapability = "raise-service-ticket";
		try {
			final ListCapabilityProvidersResponse listCapabilityProvidersResponse = capabilityRegistryLookupManager
					.listCapabilityProviders(TimeUnit.SECONDS.toMillis(5));

			//search the capability list for the required capability
			for (final CapabilityProvider capabilityProvider : listCapabilityProvidersResponse.getResponse()) {
				for (final Capability capability : capabilityProvider.getCapabilities()) {
					LOG.debug("Found capability {}", capability.getProfile());

					if (requiredCapability.equals(capability.getProfile())) {
						
						//lookup the request and response exchanges
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

						//bind to the response exchange (only for response including our reply to in the routing key)
						amqpAdmin.declareBinding(
								BindingBuilder.bind(scaleResponseQueue).to(responseExchange).with(responseRoutingKey));

						LOG.info("Adding binding {} {}", responseExchange.getName(), responseRoutingKey);

						//add message properties to the message to enable correlating requests and responses
						final UUID correlationId = UUID.randomUUID();

						MessageProperties messageProperties = new MessageProperties();
						messageProperties.setCorrelationId(correlationId.toString());
						messageProperties.setReplyTo(replyTo);
						messageProperties.setTimestamp(new Date());

						//save the correlation id and associated event
						matcher.put(correlationId.toString(), UUID.fromString(requestMessage.getEventId()));

						requestMessage.setMessageProperties(messageProperties);
						
						//send the message to the exchange
						rabbitTemplate.convertAndSend(requestExchange, requestRoutingKey, requestMessage);

					}
				}
			}
		} catch (CapabilityRegistryException e) {
			LOG.error("Failed while looking up Capability Registry for {}", requiredCapability, e);
		} catch (ServiceTimeoutException e) {
			LOG.error("Service timed out while querying Capability Registry");
		}
		
	}
	
	/**
	 * Sends a Ticket Service Request of type "update" which should trigger the ticketing service
	 * to update the ticket identified by the incidentId.
	 * @param event
	 * @param incidentId
	 * @param updateTitle
	 * @param updateMsg
	 */

	private void updateIncident(Event event, String incidentId, String updateTitle, String updateMsg) {
		TicketServiceRequest requestMessage = new TicketServiceRequest();
		TicketDetails ticketDetails = new TicketDetails(incidentId, updateTitle, updateMsg);
		requestMessage.setRequestMessage(updateMsg);
		requestMessage.setRequestType("update");
		requestMessage.setEventId(event.getId().toString());
		requestMessage.setTicketDetails(ticketDetails);
		sendRequest(requestMessage);
	}

	/**
	 * Pre-canned handling for new application performance event. Build out as
	 * needed to include required business logic
	 */
	private ExecutableStep executeNewEvent() {
		return new ExecutableStep() {

			/**
			 * New event, create a ticket
			 */
			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {

				LOG.info("Raising New Ticket for: " + message.getDetails());

				String ticketTitle = message.getDetails();

				TicketServiceRequest requestMessage = new TicketServiceRequest();
				TicketDetails ticketDetails = new TicketDetails("", message.getHost() + " " + ticketTitle,
						message.getDetails());

				requestMessage.setRequestMessage(message.getDetails());
				requestMessage.setRequestType("create");
				requestMessage.setEventId(event.getId().toString());
				requestMessage.setTicketDetails(ticketDetails);
				sendRequest(requestMessage);

			}
			
			/**
			 * Handle ticket response and generate a recommendation
			 */

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {

				String status = message.getResponseCode();

				if ("SUCCESS".equals(status)) {

					LOG.info(
							"Hardware Capacity Overload in DataCenter A, Unable to scale autonomously, recommending capacity expansion");

					String incidentId = message.getTicketDetails().getIncidentId();
					String updateTitle = "Update Ticket";

					updateIncident(event, incidentId, updateTitle, event.getEventDetails().getDetails());

					updateIncident(event, incidentId, updateTitle,
							"[code]Hardware Capacity Overload in DataCenter A <br/><br/> No further hardware resources available, <br/><br/> Unable to scale autonomously Expand ESXi host capacity in Cluster 1. <br/><br/> ERP workload impacted[/code]");

					//include a clickable link, unique to this event, such that the user can click on the link and trigger a rest api call
					updateIncident(event, incidentId, updateTitle, "Hardware Expansion required:  "
							+ "[code]<br/><a href='http://localhost:8080/scale/api/approve/" + event.getId().toString()
							+ "'>Review Changes</a><br/><a href='http://localhost:8080/scale/api/approve/"
							+ event.getId().toString()
							+ "'>Approve Changes</a><br/><br/>[/code] Request to deploy 4 Dell PowerEdge R630 HIGH-DENSITY: ALL-FLASH ");

					event.setIncidentId(incidentId);

					//move to the next step in the workflow
					String currentStep = event.getCurrentStep().getStepName();
					Step nextStep = workflowSteps.get(currentStep);

					if (nextStep != null) {

						LOG.debug("Setting Next Step : {}", nextStep.getStepName());
						event.setCurrentStep(nextStep);

					} else {
						LOG.error("unable to indentify next step, no action taken");
					}
				} else {
					LOG.error("received error response, no action taken");
				}

			}
			/**
			 * Not implemented, logs an error
			 */

			@Override
			public void executeApproval(Event event) {
				LOG.error("Execute New Event, no manual approval step defined");

			}
		};

	}

	/**
	 * Pre-canned handling for application performance event workflow. Build out
	 * as needed to include required business logic
	 */

	private ExecutableStep executeResolve() {
		return new ExecutableStep() {

			/**
			 * Not implemented, logs an error
			 */
			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {
				LOG.error("Execute Resolve should not be called for ApplicationPerformanceEvent ");

			}
			
			/**
			 * Not implemented, logs an error
			 */
			@Override
			public void executeStep(Event event, TicketServiceResponse message) {
				LOG.info("Execute Resolve, no action taken");

			}
			
			/**
			 *  Handles an approval event, updates the ticket to approved 
			 *  and moves to next step in workflow
			 */

			@Override
			public void executeApproval(Event event) {

				LOG.info("Expansion Approved for Incident: " + event.getIncidentId());

				updateIncident(event, event.getIncidentId(), "Update Ticket",
						"Approved by Jane Doe" + "VxRack Provisioning:" + "====Expansion Complete===="
								+ "Successfully completed hardware expansion" + "======Details====="
								+ "Configuring 9K5JMY1 172.17.1.7" + "Configuring 9K5JMY2 172.17.1.8"
								+ "Configuring 9K5JMY3 172.17.1.9" + "Configuring 9K5JMY4 172.17.1.10"
								+ "Installing ESXi……" + "Added to Cluster 1" + "");

				String currentStep = event.getCurrentStep().getStepName();
				Step nextStep = workflowSteps.get(currentStep);

				if (nextStep != null) {

					LOG.debug("Setting Next Step : {}", nextStep.getStepName());
					event.setCurrentStep(nextStep);

				} else {
					LOG.error("unable to indentify next step, no action taken");
				}

			}
		};

	}

	/**
	 * Pre-canned handling for application performance event workflow. Build out
	 * as needed to include required business logic
	 */

	private ExecutableStep executeComplete() {
		return new ExecutableStep() {

			/**
			 * Not implemented, logs an error
			 */
			@Override
			public void executeStep(Event event, ApplicationPerformanceEvent message) {
				LOG.error("Execute Complete should not be called for ApplicationPerformanceEvent ");

			}
			
			/**
			 *  Handle ticket response and closes ticket related to event specified
			 */

			@Override
			public void executeStep(Event event, TicketServiceResponse message) {

				String status = message.getResponseCode();
				String eventId = event.getId().toString();

				if ("SUCCESS".equals(status)) {

					LOG.info("Closing Ticket for Incident: " + event.getIncidentId());

					TicketServiceRequest requestMessage = new TicketServiceRequest();
					TicketDetails ticketDetails = new TicketDetails(event.getIncidentId(), "Close Ticket",
							"Compute and Storage Capacity Normal in DataCenter");
					requestMessage.setRequestMessage("Compute and Storage Capacity Normal in DataCenter");

					requestMessage.setRequestType("close");
					requestMessage.setEventId(eventId);
					requestMessage.setTicketDetails(ticketDetails);

					sendRequest(requestMessage);

					LOG.info("WorkFlow Flow complete, deleting event: {} ", eventId);
					eventRespository.delete(event.getId());

				} else {
					LOG.error("received error response, no action taken");
				}

			}
			
			/**
			 * Not implemented, logs an error
			 */

			@Override
			public void executeApproval(Event event) {
				LOG.error("Execute Complete, no manual approval step defined");

			}
		};

	}

}
