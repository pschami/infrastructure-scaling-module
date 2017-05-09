/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.api;

import java.util.List;

import com.dell.cpsd.common.rabbitmq.message.HasErrors;
import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;


public class TicketServiceErrorResponse implements HasErrors<Error>, HasMessageProperties<MessageProperties> {

	@Override
	public MessageProperties getMessageProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMessageProperties(MessageProperties messageProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Error> getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setErrors(List<Error> errorMessages) {
		// TODO Auto-generated method stub
		
	}
	
	  public TicketServiceErrorResponse withMessageProperties() {
	       //TODO Auto-generated method stub
	        return this;
	    }

}
