package com.argallar.smsproxy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.db.view.report.Report;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class DBAdapterTest {

	@Test
	public void adapt() {

		RequestData request = createRequest();
        
		SmsRequest expected = new SmsRequest();
		expected.setOriginator(request.getOriginator());
		expected.setMsisdn(request.getRecipient());
		expected.setMessage(request.getMessage());

        SmsRequest actual = DBAdapter.serviceToDbRequest.apply(request);
		assertEquals(actual, expected);
	}

    @Test
	public void adaptReports() {

		Report report = createReport();
		ReportItem expected = new ReportItem();
		    
		expected.setId(report.getId());
	    expected.setCreateDate(report.getCreateDate());
	    expected.setRecipient(report.getMsisdn());
	    expected.setOriginator(report.getOriginator());
	    expected.setMessage(report.getMessage());
	    expected.setChunksLeft(report.getChunksLeft());
        expected.setFailedChunks(report.getFailedChunks());
        expected.setSendDate(report.getSendDate());
        expected.setUrl(report.getUrl());

		ReportItem actual = DBAdapter.dbToServiceReport.apply(report);
        assertEquals(actual, expected);
        assertNotNull(actual.toString());
	}

    private RequestData createRequest() {
        RequestData request = new RequestData();
        request.setOriginator("testing");
        request.setRecipient("0123456789012");
        request.setMessage("this is a test message.");
        
        return request;
    }

    private Report createReport() {
        Report report = new Report();
        
        report.setId(1);
        report.setCreateDate(new Date());
        report.setMsisdn("0123456789012");
        report.setOriginator("testing");
        report.setMessage("this is a test message.");
        report.setChunksLeft(2);
        report.setFailedChunks(0);
        report.setSendDate(new Date());
        report.setUrl("https://test");
        
        return report;
    }

}
