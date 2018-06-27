package com.argallar.smsproxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.argallar.smsproxy.business.ISmsProxyBusiness;
import com.argallar.smsproxy.util.InternalErrorException;

/**
 * Front of existent scheduled tasks.
 */
@EnableScheduling
@ImportResource("classpath:spring-tasks.xml")
@Component(value="smsProxyScheduler")
public class SmsProxyScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SmsProxyScheduler.class);

    @Value("${sender.maxfails}")
    private Integer maxFails;

    private boolean running = true;
    private int recurringFails = 0;

    @Autowired
    private ISmsProxyBusiness business;

    /**
     * Scheduled task that sends SMS requests to MessageBird's API. It has a fixed delay 
     * with its previous execution of 1 second, assuming the loss of time while 
     * performing DB tasks, etc (ideally 0ms).
     * Probably the delay between executions could be adjusted somewhere around 850ms to
     * get better results, but I prefer being conservative as the requirement is keeping
     * the time AT LEAST equal to 1 second.
     */
    public void sendNextChunk() {
        if (this.running) {
            try {
                calculateStatus(this.business.sendNextChunk());
            } catch (InternalErrorException e) {
                // Acting in a conservative way here... we don't want the scheduled task
                // to get stuck and still messing the system!
                // In a Production environment writting log won't be enough, we should
                // probably send a warn or (even better) monitorize the task to be aware 
                // it has stopped as soon as possible.
                LOG.error("Internal error running scheduler!!", e);
                setRunning(false);
                throw e;
            }
        }
    }

    // In the same way as previous comment, we don't want the scheduled task to keep trying
    // things and doing nothing because, i.e. network link to MessageBird is down...
    private synchronized void calculateStatus(boolean wasSuccess) {
        if (!wasSuccess) {
            LOG.warn("Scheduler execution failed");
            this.recurringFails++;
            if (this.recurringFails >= maxFails) {
                LOG.error("Something is wrong... too many failures!");
                setRunning(false);
            }
        } else {
            this.recurringFails = 0;
        }
    }

    /**
     * Utility method to stop/restart scheduled task's work.
     * 
     * @param run
     *      boolean to indicate if the task should run or not
     */
    public synchronized void setRunning(boolean run) {
        this.running = run;
        this.recurringFails = run ? 0 : this.recurringFails;
    }

    void setMaxFails(Integer maxFails) {
        this.maxFails = maxFails;
    }
}
