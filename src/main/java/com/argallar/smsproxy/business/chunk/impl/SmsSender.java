package com.argallar.smsproxy.business.chunk.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.util.InternalErrorException;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.Message;
import com.messagebird.objects.MessageResponse;

/**
 * Processor class to get a SmsResult from a given SmsChunk by sending it to 
 * MessageBird's API. 
 * 
 */
public class SmsSender extends ChunkProcessor<SmsChunk, SmsResult> {

    private static final Logger LOG = LoggerFactory.getLogger(SmsSender.class);
    
    private String apiKey;
    private SmsResult result;
    
    protected SmsSender(String apiKey, SmsChunk chunk) {
        super(LOG, chunk);
        this.apiKey = apiKey;
    }

    protected void doProcess() {
        
        try {
            final MessageBirdService messageBirdService = 
                    new MessageBirdServiceImpl(apiKey);
            final MessageBirdClient messageBirdClient = 
                    new MessageBirdClient(messageBirdService);

            Message message;
            if (this.getItem().getUdh() != null) {
                // If the chunk has UDH... we need to send it a BinarySMS 
                // (it is one of the pieces of a CSMS)
                message = Message.createBinarySMS(this.getItem().getOriginator(), 
                        this.getItem().getUdh(), this.getItem().getMessage(), 
                        this.getItem().getMsisdn());
            } else {
                // If not... easy way
                message = new Message(this.getItem().getOriginator(), 
                        this.getItem().getMessage(), this.getItem().getMsisdn());
            }
            LOG.debug("Trying to send chunk [{}]", this.getItem());
            MessageResponse mr = messageBirdClient.sendMessage(message);
            LOG.debug("Chunk sent! [{}]", mr);
            
            // In real world we would likely want more data from MessageBird's
            this.result = new SmsResult();
            this.result.setId(mr.getId());
            this.result.setUrl(mr.getHref());
            
        } catch (UnauthorizedException | GeneralException e) {
            String msgError = 
                    String.format("Communication with MessageBird failed! [%s]", 
                            this.getItem().getId());
            LOG.error(msgError, e);
            throw new InternalErrorException(msgError, e);
        }
    }

    public SmsResult getResult() {
        return result;
    }
}
