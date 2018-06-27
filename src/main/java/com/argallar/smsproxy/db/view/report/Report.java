package com.argallar.smsproxy.db.view.report;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DB entity containing report data about a SMS previously requested.
 */
public class Report {

    private Integer id;
    private String msisdn;
    private String originator;
    private String message;
    private Date createDate;
    private Date sendDate;
    private String url;

    /**
     * Field containing the number of chunks the SMS has that have not already been sent
     */
    private int chunksLeft;
    /**
     * Field containing the number of chunks the SMS whose send process has failed and 
     * are not yet sent
     */
    private int failedChunks;

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
            .append("msisdn", msisdn)
            .append("originator", originator).append("message", message)
            .append("createDate", createDate).append("sendDate", sendDate)
            .append("url", url).append("chunksLeft", chunksLeft)
            .append("failedChunks", failedChunks).toString();
    }
}
