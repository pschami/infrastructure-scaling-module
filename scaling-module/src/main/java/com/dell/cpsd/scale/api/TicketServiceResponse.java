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
 * Service Ticket Response
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Message(value = "com.dell.cpsd.ticket.response", version = "1.0")
@JsonPropertyOrder({
	"messageProperties",
	"responseCode",
	"eventId"
})
public class TicketServiceResponse  implements HasMessageProperties<MessageProperties>{

    
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
     * 
     * (Required)
     * 
     */
    @JsonProperty("responseCode")
    private String responseCode;
    
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
    public TicketServiceResponse() {
    }

    /**
     * 
     * @param responseCode
     */
    public TicketServiceResponse(String responseCode, String eventId) {
        super();
        this.responseCode = responseCode;
        this.eventId = eventId;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * 
     * (Required)
     * 
     * @param responseCode
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
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
        return new HashCodeBuilder().append(messageProperties).append(responseCode).append(eventId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TicketServiceResponse) == false) {
            return false;
        }
        TicketServiceResponse rhs = ((TicketServiceResponse) other);
        return new EqualsBuilder().append(messageProperties, rhs.messageProperties).append(responseCode, rhs.responseCode).append(eventId, rhs.eventId).isEquals();
    }


}

