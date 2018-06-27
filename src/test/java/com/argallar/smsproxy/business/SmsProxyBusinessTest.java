package com.argallar.smsproxy.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;
import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.business.impl.SmsProxyBusiness;
import com.argallar.smsproxy.db.entity.chunk.ISmsChunkRepository;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.ISmsRequestRepository;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.db.view.report.IReportRepository;
import com.argallar.smsproxy.util.InternalErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsProxyBusinessTest {

    @Mock
    private ISmsRequestRepository requestRepo;
    @Mock
    private ISmsChunkRepository chunkRepo;
    @Mock
    private IReportRepository reportRepo;
    @Mock
    private IChunkProcessorFactory processorFactory;

    @InjectMocks
    private SmsProxyBusiness target = new SmsProxyBusiness();
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getReport() throws Exception {

        List<ReportItem> actual = target.getReport();
        assertNotNull(actual);
        
        SmsRequest request;
        for (ReportItem reportItem : actual) {
            request = requestRepo.find(reportItem.getId());
            assertEquals(request.getOriginator(), reportItem.getOriginator());
            assertEquals(request.getMessage(), reportItem.getMessage());
            assertEquals(request.getMsisdn(), reportItem.getRecipient());
            assertEquals(request.getCreateDate(), reportItem.getCreateDate());
            assertEquals(request.getSendDate(), reportItem.getSendDate());
            assertEquals(request.getUrl(), reportItem.getUrl());
            assertNotNull(reportItem.toString());
        }
    }
    
	@Test
	public void createRequest() throws Exception {
	    
		RequestData newRequest = new RequestData();
		newRequest.setOriginator("a test");
		newRequest.setRecipient("789012345");
		newRequest.setMessage("Test message.");
		
        when(processorFactory.getChunker(any()))
        .thenReturn(new FakeChunker(new SmsRequest(), true));
		target.createRequest(newRequest);
        verify(requestRepo).insert(any());
        verify(chunkRepo).insert(any());
	}

	@Test
	public void sendNextChunk() {

	    boolean actual = target.sendNextChunk();
		assertTrue(actual);
        
        SmsChunk chunk = new SmsChunk();
        chunk.setId(1);
        when(chunkRepo.findNext()).thenReturn(chunk);
        when(processorFactory.getSender(any())).thenReturn(new FakeSender(chunk, false));
        actual = target.sendNextChunk();
        verify(chunkRepo).updateRetries(chunk);
        assertFalse(actual);
        
        when(processorFactory.getSender(any())).thenReturn(new FakeSender(chunk, true));
        actual = target.sendNextChunk();
        verify(chunkRepo).delete(1);
        assertTrue(actual);
        
        when(processorFactory.getSender(any())).thenReturn(new FakeSender(chunk, true));
        chunk.setLast(true);
        chunk.setIdRequest(1);
        actual = target.sendNextChunk();
        verify(requestRepo).updateSent(1, null);
        assertTrue(actual);
        
        when(processorFactory.getSender(any())).thenReturn(new FakeSender(chunk, true));
        doThrow(new RuntimeException("testing")).when(chunkRepo).delete(any());
        try {
            target.sendNextChunk();
            throw new AssertionError();
        } catch (InternalErrorException e) {
            //should use error-codes instead of messages
            assertEquals("Error when message is already sent!", e.getMessage());
        }
	}
    
    private class FakeChunker extends ChunkProcessor<SmsRequest, List<SmsChunk>> {
        
        private boolean success;

        public FakeChunker(SmsRequest item, boolean success) {
            super(LoggerFactory.getLogger(SmsProxyBusinessTest.class), item);
            this.success = success;
        }

        @Override
        protected void doProcess() {
            if (!success) {
                throw new InternalErrorException("failed on purpose.");
            }
        }

        @Override
        public List<SmsChunk> getResult() {
            return new ArrayList<>();
        }
    }
    
    private class FakeSender extends ChunkProcessor<SmsChunk, SmsResult> {
        
        private boolean success;

        public FakeSender(SmsChunk item, boolean success) {
            super(LoggerFactory.getLogger(SmsProxyBusinessTest.class), item);
            this.success = success;
        }

        @Override
        protected void doProcess() {
            if (!success) {
                throw new InternalErrorException("failed on purpose.");
            }
        }

        @Override
        public SmsResult getResult() {
            return new SmsResult();
        }
    }

}
