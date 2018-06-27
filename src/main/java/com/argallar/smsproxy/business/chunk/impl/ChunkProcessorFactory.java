package com.argallar.smsproxy.business.chunk.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.SmsRequest;

@Component
public class ChunkProcessorFactory implements IChunkProcessorFactory {

    @Value("${sender.apiKey}")
    private String apiKey;
    
    public ChunkProcessor<SmsRequest, List<SmsChunk>> getChunker(final SmsRequest request) {
        return new SmsChunker(request);
    }
    
    public ChunkProcessor<SmsChunk, SmsResult> getSender(final SmsChunk chunk) {
        return new SmsSender(apiKey, chunk);
    }
}
