package com.argallar.smsproxy.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.db.entity.chunk.ISmsChunkRepository;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.util.Constants;
import com.argallar.smsproxy.util.InternalErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
//Main DB is configured on memory... if that changes this config should change 
//accordingly so no test-data is created (and committed) on persistent DB.
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsChunkRepositoryTest {
    
    @Value("${sender.chunk.maxretries}")
    private int maxRetries;
    
    @Autowired
    private ISmsChunkRepository target;

    @Test
    public void find() {

        SmsChunk actual = target.find(1);
        assertNotNull(actual);
        assertEquals(Integer.valueOf(1), actual.getId());
        assertEquals("1555424238", actual.getMsisdn());
        assertEquals("INBOX", actual.getOriginator());
        assertEquals("050003AB0401", actual.getUdh());
        assertEquals("0042FF2388120023130042FF2388120023130042FF2388120023130042FF2388" +
                "120023130042FF2388120023130042FF238812002313", actual.getMessage());
        assertFalse(actual.isLast());
        assertNotNull(actual.toString());

        actual = target.find(null);
        assertNull(actual);

        actual = target.find(99);
        assertNull(actual);
    }
    
	@Test
	public void create() {

        List<SmsChunk> items = new ArrayList<SmsChunk>();
        
        SmsChunk item = target.find(2);
        item.setId(null);
        items.add(item);
        item = target.find(3);
        item.setId(null);
        items.add(item);
        
        List<SmsChunk> actual = target.insert(items);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        
        for (int i = 0; i < 2; i++) {
            items.get(i).setId(actual.get(i).getId());
        }
        assertEquals(items, actual);
        
        items.clear();
        actual = target.insert(items);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
	}

	@Test
	public void updateRetries() {

        SmsChunk item = target.find(1);
		target.updateRetries(item);
		SmsChunk actual = target.find(1);
		
		assertEquals(item.getRetries() + 1, actual.getRetries());
		        
		item.setId(99);
        try {
            target.updateRetries(item);
            throw new AssertionError();
        } catch (InternalErrorException e) {
            //should use error-codes instead of messages
            assertEquals(Constants.MSG_QUERY_IERROR, e.getMessage());
        }
    }

    @Test
    public void findNext() {

        SmsChunk actual = target.findNext();
        
        assertNotNull(actual);
        assertNotNull(actual.getId());
        
        SmsChunk item = target.find(actual.getId());
        
        assertEquals(item, actual);
        
        for (int i = actual.getRetries(); i < maxRetries; i++) {
            target.updateRetries(actual);
        }
        actual = target.findNext();
        assertNotEquals(item, actual);
    }

	@Test
	public void delete() {
	    
        target.delete(4);
        SmsChunk actual = target.find(4);

        assertNull(actual);
        
        try {
            target.delete(99);
            throw new AssertionError();
        } catch (InternalErrorException e) {
            //should use error-codes instead of messages
            assertEquals(Constants.MSG_QUERY_IERROR, e.getMessage());
        }
	}

}
