/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;

import com.dell.cpsd.common.rabbitmq.consumer.handler.MessageHandler;
import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.services.ScalingModuleService;

/**
 * This class handles Scaling Module messages.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ApplicationPerformanceEventHandler implements MessageHandler<ApplicationPerformanceEvent>
{
	
	   private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPerformanceEventHandler.class);

      /**
     * The service that actually handles the request
     */

	   
	@Autowired    
	private final ScalingModuleService service;
	
   
   public ApplicationPerformanceEventHandler( ScalingModuleService service)
    {
       
        this.service = service;
    }
	
	
	@Override
	public boolean canHandle(Message message, Object body) {
		// TODO Auto-generated method stub
		return ApplicationPerformanceEvent.class.isInstance(body);
	}

	@Override
	public void handleMessage(ApplicationPerformanceEvent message) throws Exception {
		LOGGER.debug(message.toString());
		
		service.processApplicationPerformanceEvent(message);
	
		
	}
}



