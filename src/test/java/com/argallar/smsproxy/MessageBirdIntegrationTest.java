package com.argallar.smsproxy;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.SmsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class MessageBirdIntegrationTest {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(MessageBirdIntegrationTest.class);
    
    private static final String[] MSG = {
            // SHORT
            "This is a short test message sent to MessageBird's API. <end>", 
            // LONG
            "This is a long test message sent to MessageBird's API: Lorem ipsum dolor " +
            "sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
            "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
            "exercitation ullamco laboris nisi <end>"
            };
    
    @Autowired
    private IChunkProcessorFactory processorFactory;

    @Test
    public void doProcess() {
        
        Arrays.stream(MSG)
        .map(msg -> {
            SmsRequest request = new SmsRequest();
            request.setOriginator("INBOX");
            request.setMsisdn("1555424238");
            request.setMessage(msg);
            return request;
        }).forEach(request -> {
            LOG.debug("request -> [{}]", request);
            processRequest(request);
        });
        
    }

    private void processRequest(SmsRequest request) {

        ChunkProcessor<SmsRequest, List<SmsChunk>> chunker = 
                processorFactory.getChunker(request);
        chunker.process();
        
        chunker.getResult().stream()
        .map(chunk -> processorFactory.getSender(chunk))
        .forEachOrdered(sender -> {
            LOG.debug("chunk -> [{}]", sender.getItem());
            sender.process();
            LOG.debug("result -> [{}]", sender.getResult());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing
            }
        });
    }

}
