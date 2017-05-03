/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.scale.model;

import java.util.UUID;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;

/**
 * Event
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

	public Event() {
		// TODO Auto-generated constructor stub
	}

	public UUID getId() {
		return id;
	}

	public Step getCurrentStep() {
		return currentStep;
	}

	public ApplicationPerformanceEvent getEventDetails() {
		return eventDetails;
	}

	public void setEventDetails(ApplicationPerformanceEvent eventDetails) {
		this.eventDetails = eventDetails;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}
	

	public String getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(String incidentId) {
		this.incidentId = incidentId;
	}

	public void excuteCurrentStep(ApplicationPerformanceEvent message) {
		this.currentStep.execute(this, message);
		
	}

	public void excuteCurrentStep(TicketServiceResponse message) {
		
		this.currentStep.execute(this, message);
		// TODO Auto-generated method stub
		
	}

	public void excuteApproval() {
		
		this.currentStep.executeApproval(this);
		// TODO Auto-generated method stub
		
	}


}
