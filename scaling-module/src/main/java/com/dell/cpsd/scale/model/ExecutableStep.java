/**
* Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.cpsd.scale.model;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;
/**
 * 
 * Simple interface for step specific handling of events
 *
 */
public interface ExecutableStep {
	
	/**
	 * Step to execute for application performance events
	 * @param event
	 * @param message
	 */
	void executeStep(Event event, ApplicationPerformanceEvent message);

	/**
	 * Step to execute for ticket service responses
	 * @param event
	 * @param message
	 */
	void executeStep(Event event, TicketServiceResponse message);

	/**
	 * Step to execute for approval event
	 * @param event
	 */
	void executeApproval(Event event);

}
