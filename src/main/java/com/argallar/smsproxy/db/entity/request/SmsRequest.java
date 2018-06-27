package com.argallar.smsproxy.db.entity.request;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DB entity containing all the data from a SMS the system retains.
 */
public class SmsRequest {

    private Integer id;
    private String msisdn;
    private String originator;
    private String message;
    /**
     * Timestamp containing when the SMS-request was registered.
     */
    private Date createDate;
    /**
     * Timestamp containing when the SMS-request was finally sent to MessageBird's API 
     * (its last chunk, if more than one existed).
     */
    private Date sendDate;
    /**
     * Example of data recovered from MessageBird's API once the message has been sent
     */
    private String url;

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(final String msisdn) {
        this.msisdn = msisdn;
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

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public Date getSendDate() {
        return this.sendDate;
    }

    public void setSendDate(final Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
    return new ToStringBuilder(this)
            .append("id", id).append("msisdn", msisdn)
            .append("originator", originator).append("message", message)
            .append("createDate", createDate).append("sendDate", sendDate)
            .append("url", url).toString();
    }

    @Override
    public boolean equals(final Object other) {
    if ( !(other instanceof SmsRequest) ) return false;
    SmsRequest castOther = (SmsRequest) other;
    return new EqualsBuilder().append(id, castOther.id)
            .append(msisdn, castOther.msisdn).append(originator, castOther.originator)
            .append(message, castOther.message).append(createDate, castOther.createDate)
            .append(sendDate, castOther.sendDate).append(url, castOther.url).isEquals();
    }
}
