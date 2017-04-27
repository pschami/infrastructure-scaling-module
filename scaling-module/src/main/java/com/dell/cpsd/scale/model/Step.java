package com.dell.cpsd.scale.model;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.api.TicketServiceResponse;

/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */
public class Step
{
    private final String  stepName;
    private final boolean isFinalStep;
    private final ExecutableStep executableStep;

    public Step(final String stepName, final ExecutableStep executableStep)
    {
        this(stepName,executableStep, false);
    }

    public String getStepName() {
		return stepName;
	}

	public boolean isFinalStep() {
		return isFinalStep;
	}

	public Step(final String stepName, final ExecutableStep executableStep, final boolean isFinalStep)
    {
        this.stepName = stepName;
        this.isFinalStep = isFinalStep;
        this.executableStep = executableStep;
    }

	public void execute(Event event, ApplicationPerformanceEvent message) {
		executableStep.executeStep(event,message);
		
	}


	public void execute(Event event, TicketServiceResponse message) {
		
		executableStep.executeStep(event,message);		
		
	}

}
