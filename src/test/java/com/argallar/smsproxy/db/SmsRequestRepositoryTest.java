package com.argallar.smsproxy.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
import com.argallar.smsproxy.db.entity.request.ISmsRequestRepository;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.util.Constants;
import com.argallar.smsproxy.util.InternalErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
//Main DB is configured on memory... if that changes this config should change 
//accordingly so no test-data is created (and committed) on persistent DB.
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsRequestRepositoryTest {
    
    @Autowired
    private ISmsRequestRepository target;

    @Test
    public void find() {

        SmsRequest actual = target.find(2);
        assertNotNull(actual);
        assertEquals(Integer.valueOf(2), actual.getId());
        assertEquals("1555424238", actual.getMsisdn());
        assertEquals("dpradom", actual.getOriginator());
        assertEquals("short test message.", actual.getMessage());
        assertNotNull(actual.getCreateDate());
        assertNull(actual.getSendDate());
        assertNull(actual.getUrl());
        assertNotNull(actual.toString());

        actual = target.find(null);
        assertNull(actual);

        actual = target.find(99);
        assertNull(actual);
    }
    
    @Test
    public void insert() {
        
        SmsRequest item = target.find(1);
        item.setId(null);
        SmsRequest actual = target.insert(item);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        
        item.setId(actual.getId());
        assertEquals(item, actual);
    }

	@Test
	public void updateSent() {

        SmsRequest item = target.find(1);
        item.setId(null);
        item.setCreateDate(null);;
        item = target.insert(item);

		target.updateSent(item.getId(), "https://test");
		SmsRequest actual = target.find(item.getId());

        assertNotNull(actual.getSendDate());
        assertNotNull(actual.getUrl());

        item.setSendDate(actual.getSendDate());
        item.setUrl(actual.getUrl());
        assertEquals(item, actual);
        
        try {
            target.updateSent(99, "https://test");
            throw new AssertionError();
        } catch (InternalErrorException e) {
            //should use error-codes instead of messages
            assertEquals(Constants.MSG_QUERY_IERROR, e.getMessage());
        }
	}

    @Test
    public void findAll() {

        List<SmsRequest> actual = target.findAll();
        assertNotNull(actual);
        assertNotEquals(0, actual.size());

        SmsRequest item;
        for(SmsRequest request : actual) {
            item = target.find(request.getId());
            assertEquals(item, request);
        }
    }
}
