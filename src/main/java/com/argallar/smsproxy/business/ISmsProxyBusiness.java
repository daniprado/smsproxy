package com.argallar.smsproxy.business;

import java.util.List;

import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;

/**
 * Front of the use cases implemented by the system.
 *	
 */
public interface ISmsProxyBusiness {

    /**
     * Creation of a new request to send a SMS to MessageBird's API.
     * This implies 2 actions:
     *  - Register the Request on the system
     *  - Divide the SMS in chunks (if necessary) and register then.
     *
     * @param newRequest
     *          data of the SMS to send
     */
    void createRequest(RequestData newRequest);

    /**
     * Sending of a chunk of SMS. 
     * Them all were generated/calculated when the request-SMS containing them was 
     * created on the system and put in a FIFO queue so the next to be sent is always 
     * the oldest.
     *
     */
    boolean sendNextChunk();

    /**
     * Utility use-case to picture the state of the system.
     * 
     */
    List<ReportItem> getReport();
    
}
