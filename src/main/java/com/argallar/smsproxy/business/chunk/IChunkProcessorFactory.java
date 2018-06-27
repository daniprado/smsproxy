package com.argallar.smsproxy.business.chunk;

import java.util.List;

import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.SmsRequest;

public interface IChunkProcessorFactory {
    
    ChunkProcessor<SmsRequest, List<SmsChunk>> getChunker(SmsRequest request);
    
    ChunkProcessor<SmsChunk, SmsResult> getSender(SmsChunk chunk);
}
