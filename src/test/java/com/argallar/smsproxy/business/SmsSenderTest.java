package com.argallar.smsproxy.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.db.entity.chunk.ISmsChunkRepository;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.util.InternalErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsSenderTest {
    
    @Autowired
    private ISmsChunkRepository chunkRepo;
    
    @Autowired
    private IChunkProcessorFactory processorFactory;

	@Test
	public void doProcess() {
        
	    SmsChunk chunk = chunkRepo.find(5);
        ChunkProcessor<SmsChunk, SmsResult> sender = processorFactory.getSender(chunk);
        sender.process();
        assertNotNull(sender.getResult());
        assertNotNull(sender.getResult().getId());
        assertNotNull(sender.getResult().getUrl());
        assertNotNull(sender.getResult().toString());
        
        chunk = chunkRepo.find(2);
        sender = processorFactory.getSender(chunk);
        sender.process();
        assertNotNull(sender.getResult());
        assertNotNull(sender.getResult().getId());
        
        chunk.setMsisdn("0");
        sender = processorFactory.getSender(chunk);
        try {
            sender.process();
            throw new AssertionError();
        } catch (InternalErrorException e) {
            //should use error-codes instead of messages
            assertEquals("Communication with MessageBird failed! [2]", e.getMessage());
        }
		
	}

}
