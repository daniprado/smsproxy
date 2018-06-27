package com.argallar.smsproxy.db.entity.chunk;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DB entity containing the whole data from chunk of an SMS the system will send to 
 * MessageBird's API.
 */
public class SmsChunk {

    private Integer id;
    /**
     * DB ID of the original SMS data
     */
    private Integer idRequest;
    private String msisdn;
    private String originator;
    /**
     * UDH header to be sent. It only applies to CSMS messages.
     */
    private String udh;
    private String message;
    /**
     * Is this the last chunk on the sequence of the SMS?
     */
    private boolean last;
    
    private int retries;

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getIdRequest() {
        return this.idRequest;
    }

    public void setIdRequest(final Integer idRequest) {
        this.idRequest = idRequest;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }
    
    public String getUdh() {
        return udh;
    }

    public void setUdh(String udh) {
        this.udh = udh;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(final boolean last) {
        this.last = last;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }  

    @Override
    public String toString() {
    return new ToStringBuilder(this).append("id", id)
            .append("idRequest", idRequest)
            .append("msisdn", msisdn).append("originator", originator).append("udh", udh)
            .append("message", message).append("last", last).append("retries", retries)
            .toString();
    }

    @Override
    public boolean equals(final Object other) {
    if ( !(other instanceof SmsChunk) ) return false;
    SmsChunk castOther = (SmsChunk) other;
    return new EqualsBuilder().append(id, castOther.id)
            .append(idRequest, castOther.idRequest).append(msisdn, castOther.msisdn)
            .append(originator, castOther.originator).append(udh, castOther.udh)
            .append(message, castOther.message).append(last, castOther.last)
            .append(retries, castOther.retries).isEquals();
    }
}
