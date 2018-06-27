package com.argallar.smsproxy.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.ISmsRequestRepository;
import com.argallar.smsproxy.db.entity.request.SmsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsChunkerTest {

    @Autowired
    private ISmsRequestRepository requestRepo;
    
    @Autowired
    private IChunkProcessorFactory processorFactory;
    
	@Test
	public void doProcess() {
	    
	    SmsRequest request = requestRepo.find(2);
	    ChunkProcessor<SmsRequest, List<SmsChunk>> chunker = 
	            processorFactory.getChunker(request);
	    chunker.process();
	    
	    assertNotNull(chunker.getResult());
	    assertEquals(1, chunker.getResult().size());
	    
	    SmsChunk chunk = chunker.getResult().get(0);
        assertEquals(request.getMsisdn(), chunk.getMsisdn());
        assertEquals(request.getOriginator(), chunk.getOriginator());
        assertEquals(request.getMessage(), chunk.getMessage());
        assertNull(chunk.getUdh());
        assertTrue(chunk.isLast());
        
        
        request = requestRepo.find(1);
        chunker = processorFactory.getChunker(request);
        chunker.process();
        
        assertNotNull(chunker.getResult());
        assertFalse(chunker.getResult().isEmpty());
        assertNotEquals(1, chunker.getResult().size());
        
        for (SmsChunk c : chunker.getResult()) {
            assertEquals(request.getMsisdn(), c.getMsisdn());
            assertEquals(request.getOriginator(), c.getOriginator());
            assertNotEquals(request.getMessage(), c.getMessage());
            assertNotNull(c.getUdh());
        }
	    
	}

}
