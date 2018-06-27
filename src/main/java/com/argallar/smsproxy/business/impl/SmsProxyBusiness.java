package com.argallar.smsproxy.business.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;
import com.argallar.smsproxy.bean.SmsResult;
import com.argallar.smsproxy.business.ISmsProxyBusiness;
import com.argallar.smsproxy.business.chunk.IChunkProcessorFactory;
import com.argallar.smsproxy.business.chunk.impl.ChunkProcessor;
import com.argallar.smsproxy.db.entity.chunk.ISmsChunkRepository;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.ISmsRequestRepository;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.db.view.report.IReportRepository;
import com.argallar.smsproxy.util.DBAdapter;
import com.argallar.smsproxy.util.InternalErrorException;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class SmsProxyBusiness implements ISmsProxyBusiness {

    private static final Logger LOG = LoggerFactory.getLogger(SmsProxyBusiness.class);

    @Autowired(required = false)
    private ISmsRequestRepository requestRepo;
    @Autowired(required = false)
    private ISmsChunkRepository chunkRepo;
    @Autowired(required = false)
    private IReportRepository reportRepo;
    
    @Autowired(required = false)
    private IChunkProcessorFactory processorFactory;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createRequest(final RequestData newRequest) {
        LOG.debug("Starting createRequest [{}]...", newRequest);
        
        final SmsRequest request = this.requestRepo.insert(
                DBAdapter.serviceToDbRequest.apply(newRequest));
        processorFactory.getChunker(request)
            .onSuccess(this.chunkRepo::insert)
            .process();
        LOG.debug("Finished createRequest [{}].", request);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean sendNextChunk() {
        LOG.debug("Starting sendNextChunk...");

        boolean success = true;
        final SmsChunk nextChunk = this.chunkRepo.findNext();

        if (nextChunk != null) {
            LOG.debug("got next chunk! [{}]", nextChunk);
            ChunkProcessor<SmsChunk, SmsResult> sender = 
                    processorFactory.getSender(nextChunk)
                    .onError(this.chunkRepo::updateRetries);
            sender.process();

            if (sender.wasSuccess()) {
                LOG.info("chunk sent [{}]", sender.getResult());
                try {
                    chunkSent(nextChunk, sender.getResult());
                } catch (Exception e) {
                    // Worst possible error: we have already sent the chunk, but updating
                    // DB to register it fails... we must stop processing immediately to 
                    // avoid re-sending the chunk.
                    String errorMsg = "Error when message is already sent!";
                    LOG.error(errorMsg, e);
                    throw new InternalErrorException(errorMsg, e);
                }
            }
            
            success = sender.wasSuccess();
        }
        LOG.debug("Finished sendNextChunk [{}]", success);
        return success;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void chunkSent(SmsChunk nextChunk, SmsResult result) {
        this.chunkRepo.delete(nextChunk.getId());
        // In real world we would want to get data from every chunk in a CSMS, not just 
        // the last one...
        if (nextChunk.isLast()) {
            this.requestRepo.updateSent(nextChunk.getIdRequest(), result.getUrl());
        }
    }

    @Override
    public List<ReportItem> getReport() {
        return this.reportRepo.findAll().stream()
                .map(DBAdapter.dbToServiceReport)
                .collect(Collectors.toList());
    }

}
