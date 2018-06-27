package com.argallar.smsproxy.business.chunk.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.db.entity.request.SmsRequest;

/**
 * Processor class to get the list of SmsChunks (result) from a given SmsRequest. 
 * Each SmsChunk contains the whole data needed to send it as a single SMS.
 * 
 */
public class SmsChunker extends ChunkProcessor<SmsRequest, List<SmsChunk>> {

    private static final Logger LOG = LoggerFactory.getLogger(SmsChunker.class);
    
    private List<SmsChunk> result;

    private static final int SMS_MAXLENGTH = 160;
    // CSMS_MAXLENGTH = SMS_MAXLENGTH - UDH-length
    private static final int CSMS_MAXLENGTH = 153;
    
    private String refNumber;
    private String numChunks;

    protected SmsChunker(SmsRequest request) { 
        super(LOG, request);
        this.result = new ArrayList<>();
    }
    
    protected void doProcess() {
        
        int msgLen = this.getItem().getMessage().length();
        if (msgLen <= SMS_MAXLENGTH) {
            // Message is not too long... easy way.
            final SmsChunk chunk = new SmsChunk();
            chunk.setIdRequest(this.getItem().getId());
            chunk.setMsisdn(this.getItem().getMsisdn());
            chunk.setOriginator(this.getItem().getOriginator());
            chunk.setMessage(this.getItem().getMessage());
            chunk.setLast(true);
            result.add(chunk);
            
        } else {
            // Message is too long... we need to chunk it.
            final List<String> msgChunks = new ArrayList<>();
            for (int i = 0; i < msgLen; i+= CSMS_MAXLENGTH) {
                msgChunks.add(this.getItem().getMessage().substring(i, 
                        Math.min(msgLen, i + CSMS_MAXLENGTH)));
            }

            numChunks = String.format("%02X", msgChunks.size());
            refNumber = String.format("%02X", (new Random()).nextInt(0x100));

            IntStream.range(0, msgChunks.size())
            .mapToObj(i -> {
                
                final SmsChunk chunk = new SmsChunk();
                chunk.setIdRequest(this.getItem().getId());
                chunk.setMsisdn(this.getItem().getMsisdn());
                chunk.setOriginator(this.getItem().getOriginator());
                // UDH will only be set when there are more than one chunk
                chunk.setUdh(buildUdh(i));
                // Message needs to be binary encoded...
                //chunk.setMessage(encode7BitHex(msgChunks.get(i)));
                chunk.setMessage(encodeHex(msgChunks.get(i)));
                chunk.setLast(i == (msgChunks.size()-1));
                
                return chunk;
            }).forEachOrdered(result::add);
            
            LOG.debug("We have {} new chunks to send", this.result.size());
        }
    }

    @Override
    public List<SmsChunk> getResult() {
        return this.result;
    }

    private String buildUdh(int pos) {
        
        final StringBuilder udh = new StringBuilder();
        udh.append("05");                           // UDH Length
        udh.append("00");                           // IE Identifier
        udh.append("03");                           // IE Data Length
        udh.append(refNumber);                      // Reference Number
        udh.append(numChunks);                      // Number of pieces
        udh.append(String.format("%02X", pos+1));   // Sequence number
        
        return udh.toString();
    }

    private String encodeHex(String msg) {
        
        StringBuilder hex = new StringBuilder();
        IntStream.range(0, msg.length())
        .mapToObj(i -> Integer.toHexString(msg.charAt(i)))
        .forEachOrdered(hex::append);
        
        return hex.toString();
    }
    
    // I felt so stupid when I realised that this type on encoding wasn't needed that I 
    // decided to keep the code...
    private String encode7BitHex(String msg) {

        StringBuilder hex = new StringBuilder();
        List<String> plain7bit = new ArrayList<>();
        
        // padding to complete 7th septet of UDH
        plain7bit.add("0");
        
        // ascii --> plain-7bit
        plain7bit.addAll(IntStream.range(0, msg.length())
                .mapToObj(i -> Integer.toBinaryString(msg.charAt(i)))
                .map(s -> StringUtils.leftPad(s, 7, '0'))
                .collect(Collectors.toList()));
        
        // last segment to have a whole byte at the end of message
        int remainder = plain7bit.parallelStream().mapToInt(String::length).sum() % 8;
        if (remainder != 0) {
            plain7bit.add(StringUtils.repeat("0", 8 - remainder));
        }

        // plain-7bit --> encoded-8bit
        String[] segments = plain7bit.toArray(new String[plain7bit.size()]);
        IntStream.range(0, segments.length)
        .mapToObj(i -> {
            String octet = null;
            String elem = segments[i];
            // elem.length will always be [0,7] as 7 is the size of every char, 
            // padding/remainder are always shorter... and during this process we will 
            // only shrink "future" segments.
            if (elem.length() > 0) {
                // no fear to access out of limits... 
                // last segment will have length == 0 when loop reaches it
                String next = segments[i+1];
                // we build the next byte starting with present elem and filling the rest
                // of positions (elem.length < 8) with the (shift) less significative 
                // bits of next segment. Of course we need to move this bits 
                // (next -> elem).
                int shift = next.length() - (8 - elem.length());
                octet = (next.substring(shift) + elem);
                segments[i+1] = next.substring(0, shift);
            } 
            return octet;
        })
        .filter(Objects::nonNull)

        // encoded-8bit --> hex
        .mapToInt(s -> Integer.parseInt(s, 2))
        .mapToObj(i -> Integer.toString(i, 16))
        .map(s -> StringUtils.leftPad(s, 2, '0'))
        .forEachOrdered(hex::append);
               
        return hex.toString();
    }
}
