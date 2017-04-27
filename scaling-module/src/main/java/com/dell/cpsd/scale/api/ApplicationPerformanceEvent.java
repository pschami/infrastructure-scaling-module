
package com.dell.cpsd.scale.api;

import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Published when a performance event is received.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Message(value = "com.dell.cpsd.apm.event", version = "1.0")
@JsonPropertyOrder({
    "severity",
    "id",
    "timestamp",
    "host",
    "details"
})
public class ApplicationPerformanceEvent {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("severity")
    private String severity;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    private String id;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    private Date timestamp;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("host")
    private String host;
    
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("details")
    private String details;


	/**
     * No args constructor for use in serialization
     * 
     */
    public ApplicationPerformanceEvent() {
    }

    /**
     * 
     * @param severity
     */
    public ApplicationPerformanceEvent(String severity) {
        super();
        this.severity = severity;
    }
    
    

    public ApplicationPerformanceEvent(String severity, String id, Date timestamp, String host, String details) {
		super();
		this.severity = severity;
		this.id = id;
		this.timestamp = timestamp;
		this.host = host;
		this.details = details;
	}

	/**
     * 
     * (Required)
     * 
     * @return
     *     The severity
     */
    @JsonProperty("severity")
    public String getSeverity() {
        return severity;
    }

    /**
     * 
     * (Required)
     * 
     * @param severity
     *     The severity
     */
    @JsonProperty("severity")
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    @JsonProperty("id") 
    public String getId() {
		return id;
	}

    @JsonProperty("id") 
	public void setId(String id) {
		this.id = id;
	}

    @JsonProperty("timestamp") 
	public Date getTimestamp() {
		return timestamp;
	}

    @JsonProperty("timestamp") 
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

    @JsonProperty("host") 
	public String getHost() {
		return host;
	}

    @JsonProperty("host") 
	public void setHost(String host) {
		this.host = host;
	}

    @JsonProperty("details") 
	public String getDetails() {
		return details;
	}

    @JsonProperty("details") 
	public void setDetails(String details) {
		this.details = details;
	}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(severity).append(id).append(timestamp).append(host).append(details).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ApplicationPerformanceEvent) == false) {
            return false;
        }
        ApplicationPerformanceEvent rhs = ((ApplicationPerformanceEvent) other);
		return new EqualsBuilder().append(severity, rhs.severity).append(id, rhs.id).append(timestamp, rhs.timestamp).append(host, rhs.host).append(details, rhs.details).isEquals();
    }

}
