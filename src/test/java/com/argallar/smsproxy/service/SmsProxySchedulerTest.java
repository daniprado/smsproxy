package com.argallar.smsproxy.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.argallar.smsproxy.SmsProxy;
import com.argallar.smsproxy.business.ISmsProxyBusiness;
import com.argallar.smsproxy.util.InternalErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsProxySchedulerTest {

    @Value("${sender.maxfails}")
    private Integer maxFails;
    
    @Mock
    private ISmsProxyBusiness business;

    @InjectMocks
    private SmsProxyScheduler target = new SmsProxyScheduler();
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target.setMaxFails(maxFails);
    }
    
	@Test
	public void sendNextChunk() {

	    when(business.sendNextChunk()).thenReturn(true);
		target.sendNextChunk();
		verify(business).sendNextChunk();

        when(business.sendNextChunk()).thenReturn(false);
        for (int i = 0; i < maxFails; i++) {
            target.sendNextChunk();
        }
        verify(business, times(maxFails + 1)).sendNextChunk();
        
        target.sendNextChunk();
        verify(business, times(maxFails + 1)).sendNextChunk();
        
        target.setRunning(true);

        doThrow(new InternalErrorException("testing")).when(business).sendNextChunk();
        try {
            target.sendNextChunk();
            throw new AssertionError();
        } catch (InternalErrorException e) {
            // all good
        }
        verify(business, times(maxFails + 2)).sendNextChunk();

        target.sendNextChunk();
        verify(business, times(maxFails + 2)).sendNextChunk();
	}

}
