package com.argallar.smsproxy.util;

import java.util.function.Function;

import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.db.view.report.Report;

/**
 * Bean adaptation helper.
 */
public class DBAdapter {

    private DBAdapter() { }

    /**
     * DTO -> DB adapter for Request beans.
     */
    public static final Function<RequestData, SmsRequest> serviceToDbRequest = request -> {
        final SmsRequest result = new SmsRequest();
        result.setMsisdn(request.getRecipient());
        result.setOriginator(request.getOriginator());
        result.setMessage(request.getMessage());
        return result;
    };

    /**
     * DB -> DTO adapter for Report beans.
     */
    public static final Function<Report, ReportItem> dbToServiceReport = report -> {
        final ReportItem result = new ReportItem();
        result.setId(report.getId());
        result.setCreateDate(report.getCreateDate());
        result.setRecipient(report.getMsisdn());
        result.setOriginator(report.getOriginator());
        result.setMessage(report.getMessage());
        result.setChunksLeft(report.getChunksLeft());
        result.setFailedChunks(report.getFailedChunks());
        result.setSendDate(report.getSendDate());
        result.setUrl(report.getUrl());
        return result;
    };
}
