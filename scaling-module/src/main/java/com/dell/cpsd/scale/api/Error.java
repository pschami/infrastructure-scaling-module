
package com.dell.cpsd.scale.api;

import java.util.Date;
import com.dell.cpsd.common.rabbitmq.message.ErrorContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "code",
    "message",
    "correlationId",
    "timestamp"
})
public class Error implements ErrorContainer
{

    /**
     * Error code
     * <p>
     * The error code provided by the service.
     * (Required)
     * 
     */
    @JsonProperty("code")
    @JsonPropertyDescription("")
    private String code;
    /**
     * Error message
     * <p>
     * The error message provided by the service.
     * (Required)
     * 
     */
    @JsonProperty("message")
    @JsonPropertyDescription("")
    private String message;
    /**
     * Correlation id
     * <p>
     * The correlation id of the message which generated error.
     * (Required)
     * 
     */
    @JsonProperty("correlationId")
    @JsonPropertyDescription("")
    private String correlationId;
    /**
     * The date
     * <p>
     * The time the error message was generated.
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("")
    private Date timestamp;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Error() {
    }

    /**
     * 
     * @param code
     * @param correlationId
     * @param message
     * @param timestamp
     */
    public Error(String code, String message, String correlationId, Date timestamp) {
        super();
        this.code = code;
        this.message = message;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
    }

    /**
     * Error code
     * <p>
     * The error code provided by the service.
     * (Required)
     * 
     * @return
     *     The code
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    /**
     * Error code
     * <p>
     * The error code provided by the service.
     * (Required)
     * 
     * @param code
     *     The code
     */
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Error message
     * <p>
     * The error message provided by the service.
     * (Required)
     * 
     * @return
     *     The message
     */
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     * Error message
     * <p>
     * The error message provided by the service.
     * (Required)
     * 
     * @param message
     *     The message
     */
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Correlation id
     * <p>
     * The correlation id of the message which generated error.
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
     * Correlation id
     * <p>
     * The correlation id of the message which generated error.
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
     * The date
     * <p>
     * The time the error message was generated.
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
     * The date
     * <p>
     * The time the error message was generated.
     * (Required)
     * 
     * @param timestamp
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(code).append(message).append(correlationId).append(timestamp).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Error) == false) {
            return false;
        }
        Error rhs = ((Error) other);
        return new EqualsBuilder().append(code, rhs.code).append(message, rhs.message).append(correlationId, rhs.correlationId).append(timestamp, rhs.timestamp).isEquals();
    }

}
