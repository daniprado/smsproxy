package com.argallar.smsproxy.bean;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Result element of the utility operation getReport that shows system's status.
 *	
 */
public class ReportItem {

    private Integer id;
    private String recipient;
    private String originator;
    private String message;
    private Date createDate;
    private int chunksLeft;
    private int failedChunks;
    private Date sendDate;
    private String url;

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

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

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public int getChunksLeft() {
        return this.chunksLeft;
    }

    public void setChunksLeft(final int chunksLeft) {
        this.chunksLeft = chunksLeft;
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

    public int getFailedChunks() {
        return failedChunks;
    }

    public void setFailedChunks(int failedChunks) {
        this.failedChunks = failedChunks;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this).append("id", id)
            .append("recipient", recipient)
            .append("originator", originator).append("message", message)
            .append("createDate", createDate).append("chunksLeft", chunksLeft)
            .append("failedChunks", failedChunks).append("sendDate", sendDate)
            .append("url", url).toString();
    }

    @Override
    public boolean equals(final Object other) {
    if ( !(other instanceof ReportItem) ) return false;
    ReportItem castOther = (ReportItem) other;
    return new EqualsBuilder().append(id, castOther.id)
            .append(recipient, castOther.recipient)
            .append(originator, castOther.originator)
            .append(message, castOther.message).append(createDate, castOther.createDate)
            .append(chunksLeft, castOther.chunksLeft)
            .append(failedChunks, castOther.failedChunks)
            .append(sendDate, castOther.sendDate).append(url, castOther.url).isEquals();
    }
}
