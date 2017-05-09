/**
* Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.model;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;

/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * 
 * Represents a step in a work flow and stores instance of an 
 * executable step with associated event handling
 *  
 */
public class Step
{
    private final String  stepName;
    private final boolean isFinalStep;
    private final ExecutableStep executableStep;

    /**
     * Constructor
     * @param stepName
     * @param executableStep
     */
    public Step(final String stepName, final ExecutableStep executableStep)
    {
        this(stepName,executableStep, false);
    }
    
    /**
	 * Constructor
	 * @param stepName
	 * @param executableStep
	 * @param isFinalStep
	 */

	public Step(final String stepName, final ExecutableStep executableStep, final boolean isFinalStep)
    {
        this.stepName = stepName;
        this.isFinalStep = isFinalStep;
        this.executableStep = executableStep;
    }
	
    /**
     * 
     * @return
     */

    public String getStepName() {
		return stepName;
	}
    
    /**
     * 
     * @return
     */

	public boolean isFinalStep() {
		return isFinalStep;
	}
	
	/**
	 * Call execution step for application performance events
	 * @param message
	 */	

	public void execute(Event event, ApplicationPerformanceEvent message) {
		executableStep.executeStep(event,message);
		
	}
	
	/**
	 * Call execution step for ticket service responses
	 * @param message
	 */
	
	public void execute(Event event, TicketServiceResponse message) {
		
		executableStep.executeStep(event,message);		
		
	}
	
	/**
	 * Call execution step for approval event
	 * @param message
	 */
	
	public void executeApproval(Event event) {
		executableStep.executeApproval(event);
		
	}

}
