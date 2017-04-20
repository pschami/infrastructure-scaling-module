
package com.dell.cpsd.scale.api;

import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    "alert"
})
public class ApplicationPerformanceEvent  {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("alert")
    private String alert;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ApplicationPerformanceEvent() {
    }

    /**
     * 
     * @param alert
     */
    public ApplicationPerformanceEvent(String alert) {
        super();
        this.alert = alert;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The alert
     */
    @JsonProperty("alert")
    public String getAlert() {
        return alert;
    }

    /**
     * 
     * (Required)
     * 
     * @param alert
     *     The alert
     */
    @JsonProperty("alert")
    public void setAlert(String alert) {
        this.alert = alert;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(alert).toHashCode();
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
        return new EqualsBuilder().append(alert, rhs.alert).isEquals();
    }

}
