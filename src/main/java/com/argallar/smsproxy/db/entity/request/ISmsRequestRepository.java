package com.argallar.smsproxy.db.entity.request;

import java.util.List;

/**
 * Repository to maintain data of SMS-requests registered.
 */
public interface ISmsRequestRepository {

    /**
     * Creation of a new SMS-request in the system.
     *
     * @param request
     *          data to be inserted.
     * @return same object passed as parameter with proper DB ID.
     */
    SmsRequest insert(SmsRequest request);

    /**
     * Update of given request to fill sendDate & MessageBird's response info.
     * 
     * @param idRequest
     *          DB ID of the request to update
     * @param url
     *          URL to query SMS status on MessageBird (example of recovered info)
     */
    void updateSent(Integer idRequest, String url);

    SmsRequest find(Integer idRequest);
    
    List<SmsRequest> findAll();

}
