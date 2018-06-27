package com.argallar.smsproxy.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean containing the data inside a request sent to the system. 
 * It is built from the JSON document in request's body.
 * Fields are validated to reject wrong requests directly back to sender.
 */
public class RequestData {

    @JsonProperty("originator")
    @NotNull
    @Pattern(regexp="[a-zA-Z0-9]*")
    @Size(min = 4, max = 11)
    private String originator;

    // The configured apiKey only lets my phone number as recipient...
    // but I assume parameter validation should accept any phone number.
    // In other hand, this validation shouldbe more complex in a Production environment
    // to make sure the recipient is a well-formed phone number, but I cannot validate
    // this type of complex behaviour here... so I wont' do it.
    @JsonProperty("recipient")
    @NotNull
    @Size(min = 9, max = 11)
    @Pattern(regexp="[1-9][0-9]*")
    private String recipient;

    @JsonProperty("message")
    @NotNull
    @Size(min = 1, max = 4000)
    private String message;

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public String getOriginator() {
        return this.originator;
    }

    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this)
            .append("originator", originator)
            .append("recipient", recipient).append("message", message).toString();
    }
}
