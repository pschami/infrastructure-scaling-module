package com.dell.cpsd.scale.producer;

import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.services.ScalingModuleException;

/**
 * Scaling Module Producer Interface
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface ScalingModuleProducer
{
    /**
     * This method publishes an Scaling Module event. 
     *
     * @param event Scaling Module Event
     * @throws ScalingModuleException  Exception
     */
    void publishApplicationPerformanceEvent(final ApplicationPerformanceEvent event) throws ScalingModuleException;

   
}
