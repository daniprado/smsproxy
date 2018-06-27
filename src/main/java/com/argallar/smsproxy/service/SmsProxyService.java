package com.argallar.smsproxy.service;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.argallar.smsproxy.bean.ReportItem;
import com.argallar.smsproxy.bean.RequestData;
import com.argallar.smsproxy.business.ISmsProxyBusiness;

/**
 * REST services front. Exposes the API the server offers.
 *
 */
@RestController
//TODO Should be secured
public class SmsProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsProxyService.class);

    @Autowired
    private ISmsProxyBusiness business;

    /**
     * POST request to send a SMS.
     *
     * @param newRequest
     *            Data to create the SMS
     */
    @PostMapping("/request-sms")
    public ResponseEntity<RequestData> createRequest(
            @Valid @RequestBody final RequestData newRequest) {
        LOG.info("Starting createRequest [{}]...", newRequest);
        this.business.createRequest(newRequest);

        LOG.info("Finished createRequest.");
        return new ResponseEntity<>(newRequest, HttpStatus.OK);
    }

    /**
     * GET request to obtain data of all SMSs in the system. 
     * Utility operation that would have to be reviewed before sent to Production 
     * environment...
     *
     * @return List of SMSs requested indicating if they have already been sent (and 
     * when).
     */
    @GetMapping("/report")
    public ResponseEntity<List<ReportItem>> getReport() {
        LOG.info("Starting getReport...");
        List<ReportItem> result = this.business.getReport();
        
        LOG.info("Finished getReport.");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
