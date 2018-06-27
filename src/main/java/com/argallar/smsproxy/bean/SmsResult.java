package com.argallar.smsproxy.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SmsResult {

    private String id;
    private String url;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this).append("id", id)
            .append("url", url).toString();
    }
}
