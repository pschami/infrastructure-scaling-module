/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.dell.cpsd.common.rabbitmq.annotation.Message;
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
    "incidentId",
	"incidentTitle",
    "incidentNote"
})
public class TicketDetails {

	 	
	/**
     * 
     * (Required)
     * 
     */
    @JsonProperty("incidentId")
    private String incidentId;
    
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("incidentTitle")
    private String incidentTitle;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("incidentNote")
    private String incidentNote;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TicketDetails() {
    }

    /**
     * 
     * @param incidentId
     */
    public TicketDetails(String incidentId, String incidentTitle, String incidentNote) {
        super();
        this.incidentId = incidentId;
        this.incidentTitle = incidentTitle;
        this.incidentNote = incidentNote;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The incidentId
     */
    @JsonProperty("incidentId")
    public String getIncidentId() {
        return incidentId;
    }

    /**
     * 
     * (Required)
     * 
     * @param incidentId
     *     The incidentId
     */
    @JsonProperty("incidentId")
    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The incidentTitle
     */
    @JsonProperty("incidentTitle")
    public String getIncidentTitle() {
        return incidentTitle;
    }

    /**
     * 
     * (Required)
     * 
     * @param incidentTitle
     *     The incidentTitle
     */
    @JsonProperty("incidentTitle")
    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The incidentNote
     */
    @JsonProperty("incidentNote")
    public String getIncidentNote() {
        return incidentNote;
    }

    /**
     * 
     * (Required)
     * 
     * @param incidentNote
     *     The incidentNote
     */
    @JsonProperty("incidentNote")
    public void setIncidentNote(String incidentNote) {
        this.incidentNote = incidentNote;
    }
           

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(incidentId).append(incidentTitle).append(incidentNote).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TicketDetails) == false) {
            return false;
        }
        TicketDetails rhs = ((TicketDetails) other);
        return (new EqualsBuilder().append(incidentId, rhs.incidentId).append(incidentTitle, rhs.incidentTitle).append(incidentNote, rhs.incidentNote).isEquals()) ;
    }
	

}

