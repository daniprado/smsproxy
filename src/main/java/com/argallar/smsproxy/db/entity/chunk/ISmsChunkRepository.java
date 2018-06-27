package com.argallar.smsproxy.db.entity.chunk;

import java.util.List;

/**
 * Repository to temporary maintain SMS-parts that are not yet sent.
 */
public interface ISmsChunkRepository {

    /**
     * Creation of a new chunk. 
     *
     * @param item
     *          data to be inserted
     * @return same object passed as parameter with proper DB ID
     */
    List<SmsChunk> insert(final List<SmsChunk> item);

    /**
     * Query to find the next chunk to be processed.
     * Chunks that have equal (or more) retries to configured maximun will be ignored.
     *
     * @return the chunk
     */
    SmsChunk findNext();

    SmsChunk find(Integer idChunk);
    
    /**
     * Update to keep track of how many times a single chunk failed to be sent.
     *
     * @param item
     *          the chunk which send process failed
     */
    void updateRetries(SmsChunk item);

    /**
     * Deletion of the chunk from the system.
     *
     * @param idChunk
     *          DB ID of the chunk to delete
     */
    void delete(Integer idChunk);
}
