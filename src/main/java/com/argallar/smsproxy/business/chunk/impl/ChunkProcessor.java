package com.argallar.smsproxy.business.chunk.impl;

import java.util.function.Consumer;

import org.slf4j.Logger;

import com.argallar.smsproxy.util.InternalErrorException;

/**
 * Superclass of both data "processors".
 * It defines their common behaviour and lets defining onSuccess/Error methods to apply
 * on results/params respectively.
 *	
 * @param <P>
 *          Param type. Starting data-container for the process.
 * @param <R>
 *          Result type. Goal data-container of the process.
 */
public abstract class ChunkProcessor<P, R> {
    
    private Logger log;
    
    private P item;
    private Consumer<R> onSuccess;
    private Consumer<P> onError;
    private boolean processed;
    private boolean success;
    
    public ChunkProcessor(Logger log, P item) {
        this.log = log;
        this.item = item;
        this.processed = false;
        this.success = false;
    }
    
    /**
     * Especific behaviour of concrete-classes inside the process() template method
     */
    protected abstract void doProcess();
    
    public P getItem() {
        return this.item;
    }
    
    public abstract R getResult();

    /**
     * Was execution correct? 
     * It will return false is process() has not been called yet
     */
    public boolean wasSuccess() {
        return this.success;
    }
    
    public ChunkProcessor<P, R> onSuccess(Consumer<R> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
    
    public ChunkProcessor<P, R> onError(Consumer<P> onError) {
        this.onError = onError;
        return this;
    }
    
    public void process() {
        if (!this.processed) {
            log.debug("Starting process...");
            try {
                try {
                    doProcess();
                    this.success = true;
                    if (onSuccess != null) {
                        onSuccess.accept(this.getResult());
                    }
                } catch (Exception e) {
                    if (onError != null) {
                        onError.accept(this.item);
                    } else {
                        throw e;
                    }
                }
            } finally {
                this.processed = true;
                log.debug("Finished process.");
            }
        } else {
            throw new InternalErrorException("Already processed!");
        }
    }
}
