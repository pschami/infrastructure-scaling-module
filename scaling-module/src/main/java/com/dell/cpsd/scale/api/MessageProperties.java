/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 */
package com.dell.cpsd.scale.api;

import java.util.Date;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * AMQP properties properties
 * <p>
 * AMQP properties.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "timestamp",
    "correlationId",
    "replyTo"
})
public class MessageProperties implements MessagePropertiesContainer
{

    /**
     * Message timestamp
     * <p>
     * Message creation timestamp.
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("")
    private Date timestamp;
    /**
     * Correlation ID
     * <p>
     * Messages chain reference.
     * (Required)
     * 
     */
    @JsonProperty("correlationId")
    @JsonPropertyDescription("")
    private String correlationId;
    /**
     * Reply-to identifier.
     * <p>
     * Reply-to identifier. Default format is: '<sender-name>.<sender-host>'
     * 
     */
    @JsonProperty("replyTo")
    @JsonPropertyDescription("")
    private String replyTo;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MessageProperties() {
    }

    /**
     * 
     * @param replyTo
     * @param correlationId
     * @param timestamp
     */
    public MessageProperties(Date timestamp, String correlationId, String replyTo) {
        super();
        this.timestamp = timestamp;
        this.correlationId = correlationId;
        this.replyTo = replyTo;
    }

    /**
     * Message timestamp
     * <p>
     * Message creation timestamp.
     * (Required)
     * 
     * @return
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Message timestamp
     * <p>
     * Message creation timestamp.
     * (Required)
     * 
     * @param timestamp
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Correlation ID
     * <p>
     * Messages chain reference.
     * (Required)
     * 
     * @return
     *     The correlationId
     */
    @JsonProperty("correlationId")
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Correlation ID
     * <p>
     * Messages chain reference.
     * (Required)
     * 
     * @param correlationId
     *     The correlationId
     */
    @JsonProperty("correlationId")
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Reply-to identifier.
     * <p>
     * Reply-to identifier. Default format is: '<sender-name>.<sender-host>'
     * 
     * @return
     *     The replyTo
     */
    @JsonProperty("replyTo")
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * Reply-to identifier.
     * <p>
     * Reply-to identifier. Default format is: '<sender-name>.<sender-host>'
     * 
     * @param replyTo
     *     The replyTo
     */
    @JsonProperty("replyTo")
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(timestamp).append(correlationId).append(replyTo).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MessageProperties) == false) {
            return false;
        }
        MessageProperties rhs = ((MessageProperties) other);
        return new EqualsBuilder().append(timestamp, rhs.timestamp).append(correlationId, rhs.correlationId).append(replyTo, rhs.replyTo).isEquals();
    }

}
