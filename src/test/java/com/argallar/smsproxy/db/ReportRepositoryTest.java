package com.argallar.smsproxy.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.db.view.report.IReportRepository;
import com.argallar.smsproxy.db.view.report.Report;

@RunWith(SpringJUnit4ClassRunner.class)
// Main DB is configured on memory... if that changes this config should change 
// accordingly so no test-data is created (and committed) on persistent DB.
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class ReportRepositoryTest {
    
    @Autowired
    private IReportRepository target;

	@Test
	public void findAll() {

		List<Report> actual = target.findAll();

		for (Report report : actual) {
		    if (report.getId().equals(1)) {
                assertEquals("1555424238", report.getMsisdn());
                assertEquals("INBOX", report.getOriginator());
                assertEquals(1, report.getFailedChunks());
            } else if (report.getId().equals(2)) {
		        assertEquals(1, report.getChunksLeft());
                assertEquals("short test message.", report.getMessage());
                assertNull(report.getSendDate());
                assertNull(report.getUrl());
                assertNotNull(report.getCreateDate());
                assertEquals(1, report.getChunksLeft());
                assertNotNull(report.toString());
		    }
		}
	}

}
