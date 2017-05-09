/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.model;

import java.util.UUID;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;

/**
 * Represents an event and stores data relating to the event.
 * In a workflow context stores details of the current step 
 * in the workflow. Calls the execution of the step
 * based on specific events.
 * 
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 */
public class Event {

	private UUID id;
	private Step currentStep;
	private ApplicationPerformanceEvent eventDetails;
	private String incidentId;

	public Event(final UUID id, final ApplicationPerformanceEvent eventDetails, final Step currentStep) {
		this.id = id;
		this.eventDetails = eventDetails;
		this.currentStep = currentStep;
	}

	/**
	 * Constructor 
	 */
	public Event() {
		
	}
	/**
	 * 
	 * @return
	 */

	public UUID getId() {
		return id;
	}
	
	/**
	 * 
	 * @return
	 */

	public Step getCurrentStep() {
		return currentStep;
	}
	/**
	 * 
	 * @return
	 */

	public ApplicationPerformanceEvent getEventDetails() {
		return eventDetails;
	}
	
	/**
	 * 
	 * @param eventDetails
	 */

	public void setEventDetails(ApplicationPerformanceEvent eventDetails) {
		this.eventDetails = eventDetails;
	}
	
	/**
	 * 
	 * @param id
	 */

	public void setId(UUID id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @param currentStep
	 */

	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}
	
	/**
	 * 
	 * @return
	 */

	public String getIncidentId() {
		return incidentId;
	}
	
	/**
	 * 
	 * @param incidentId
	 */

	public void setIncidentId(String incidentId) {
		this.incidentId = incidentId;
	}
	
	/**
	 * Call execution step for application performance events
	 * @param message
	 */

	public void excuteCurrentStep(ApplicationPerformanceEvent message) {
		this.currentStep.execute(this, message);
		
	}
	
	/**
	 * Call execution step for ticket service responses
	 * @param message
	 */

	public void excuteCurrentStep(TicketServiceResponse message) {
		
		this.currentStep.execute(this, message);
				
	}
	
	/**
	 * Call execution step for approval event
	 */

	public void excuteApproval() {
		
		this.currentStep.executeApproval(this);		
		
	}


}
