/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Service Ticket Request
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Message(value = "com.dell.cpsd.ticket.request", version = "1.0")
@JsonPropertyOrder({
	"messageProperties",
    "requestType",
	"requestMessage",
    "eventId"
})
public class TicketServiceRequest implements HasMessageProperties<MessageProperties>  {

	  /**
     * AMQP properties properties
     * <p>
     * AMQP properties.
     * (Required)
     * 
     */
    @JsonProperty("messageProperties")
    private MessageProperties messageProperties;
    /**
	
	
	/**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestType")
    private String requestType;
    
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestMessage")
    private String requestMessage;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventId")
    private String eventId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TicketServiceRequest() {
    }

    /**
     * 
     * @param requestType
     */
    public TicketServiceRequest(String requestType, String requestMessage, String eventId) {
        super();
        this.requestType = requestType;
        this.requestMessage = requestMessage;
        this.eventId = eventId;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The requestType
     */
    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    /**
     * 
     * (Required)
     * 
     * @param requestType
     *     The requestType
     */
    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The requestMessage
     */
    @JsonProperty("requestMessage")
    public String getRequestMessage() {
        return requestMessage;
    }

    /**
     * 
     * (Required)
     * 
     * @param requestMessage
     *     The requestMessage
     */
    @JsonProperty("requestMessage")
    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The eventId
     */
    @JsonProperty("eventId")
    public String getEventId() {
        return eventId;
    }

    /**
     * 
     * (Required)
     * 
     * @param eventId
     *     The eventId
     */
    @JsonProperty("eventId")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    
    @Override
    @JsonProperty("messageProperties")
	public MessageProperties getMessageProperties() {
		// TODO Auto-generated method stub
		return messageProperties;
	}

	@Override
    @JsonProperty("messageProperties")
	public void setMessageProperties(MessageProperties messageProperties) {
		this.messageProperties = messageProperties;
		
	}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(messageProperties).append(requestType).append(requestMessage).append(eventId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TicketServiceRequest) == false) {
            return false;
        }
        TicketServiceRequest rhs = ((TicketServiceRequest) other);
        return (new EqualsBuilder().append(messageProperties, rhs.messageProperties).append(requestType, rhs.requestType).append(requestMessage, rhs.requestMessage).append(eventId, rhs.eventId).isEquals()) ;
    }
	

}

