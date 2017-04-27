package com.dell.cpsd.scale.model;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;

public interface ExecutableStep {
	
	void executeStep(Event event, ApplicationPerformanceEvent message);

	void executeStep(Event event, TicketServiceResponse message);

}
