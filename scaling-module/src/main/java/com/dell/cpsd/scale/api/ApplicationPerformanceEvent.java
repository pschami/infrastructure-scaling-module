/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 */
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
 * Json object published when a performance event is received.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 0.1
 * @since 0.1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Message(value = "com.dell.cpsd.apm.event", version = "1.0")
@JsonPropertyOrder({ "severity", "id", "timestamp", "host", "details" })
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

	/**
	 * Constructor
	 * 
	 * @param severity
	 * @param id
	 * @param timestamp
	 * @param host
	 * @param details
	 */
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
	 * @return The severity
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
	 *            The severity
	 */
	@JsonProperty("severity")
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	/**
	 * @return id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return timestamp
	 */
	@JsonProperty("timestamp")
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 */
	@JsonProperty("timestamp")
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return host
	 */
	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 */
	@JsonProperty("host")
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return details
	 */
	@JsonProperty("details")
	public String getDetails() {
		return details;
	}

	/**
	 * @param details
	 */
	@JsonProperty("details")
	public void setDetails(String details) {
		this.details = details;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(severity).append(id).append(timestamp).append(host).append(details)
				.toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ApplicationPerformanceEvent) == false) {
			return false;
		}
		ApplicationPerformanceEvent rhs = ((ApplicationPerformanceEvent) other);
		return new EqualsBuilder().append(severity, rhs.severity).append(id, rhs.id).append(timestamp, rhs.timestamp)
				.append(host, rhs.host).append(details, rhs.details).isEquals();
	}

}
