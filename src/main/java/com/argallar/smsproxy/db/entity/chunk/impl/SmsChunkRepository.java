package com.argallar.smsproxy.db.entity.chunk.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.argallar.smsproxy.db.entity.chunk.ISmsChunkRepository;
import com.argallar.smsproxy.db.entity.chunk.SmsChunk;
import com.argallar.smsproxy.util.Constants;
import com.argallar.smsproxy.util.InternalErrorException;

/**
 * JDBC-template implementation of ISmsChunkRepository
 */
@PropertySource("classpath:queries.properties")
@Repository
public class SmsChunkRepository implements ISmsChunkRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SmsChunkRepository.class);

    @Value("${sender.chunk.maxretries}")
    private int maxRetries;
    
    @Value("${query.sms_chunk.insert}")
    private String insertSql;
    @Value("${query.sms_chunk.select}")
    private String selectSql;
    @Value("${query.sms_chunk.select_next}")
    private String selectNextSql;
    @Value("${query.sms_chunk.update_retries}")
    private String updateRetriesSql;
    @Value("${query.sms_chunk.delete}")
    private String deleteSql;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<SmsChunk> insert(final List<SmsChunk> items) {
        LOG.debug("Starting create<list>...");
        
        items.stream().forEachOrdered(this::insert);
        LOG.debug("Finished create<list>.");
        return items;
    }

    private SmsChunk insert(final SmsChunk item) {
        LOG.debug("Starting create [{}]...", item);

        final KeyHolder holder = new GeneratedKeyHolder();
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id_request", item.getIdRequest())
                .addValue("msisdn", item.getMsisdn())
                .addValue("originator", item.getOriginator())
                .addValue("udh", item.getUdh())
                .addValue("message", item.getMessage())
                .addValue("last", item.isLast())
                .addValue("retries", 0);
        final int rows = this.jdbcTemplate.update(insertSql, parameters, holder);

        if (rows != 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }

        item.setId(holder.getKey().intValue());
        LOG.debug("Finished create [{}].", item.getId());
        return item;
    }

    @Override
    public SmsChunk findNext() {
        LOG.debug("Starting findNext ...");

        SmsChunk item = null;
        // max_retries param lets the system keep on sending messages after one has got
        // somehow stuck... and ignore it.
        // On a Production environment ignoring it won't be enough, we should monitorize
        // this situation and/or warn when a message got stuck.
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("max_retries", maxRetries);
        final List<SmsChunk> result = this.jdbcTemplate.query(selectNextSql, parameters, 
                new BeanPropertyRowMapper<>(SmsChunk.class));

        if (result.size() > 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        item = result.isEmpty() ? null : result.get(0);
        LOG.debug("Finished findNext [{}].", item);
        return item;
    }

    @Override
    public SmsChunk find(final Integer idChunk) {
        LOG.debug("Starting find [{}]...", idChunk);

        SmsChunk item = null;
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", idChunk);
        final List<SmsChunk> result = this.jdbcTemplate.query(selectSql, parameters, 
                new BeanPropertyRowMapper<>(SmsChunk.class));

        if (result.size() > 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        item = result.isEmpty() ? null : result.get(0);
        LOG.debug("Finished find [{}].", item);
        return item;
    }

    @Override
    public void updateRetries(final SmsChunk item) {
        LOG.debug("Starting updateRetries [{}]...", item);
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", item.getId());
        final int rows = this.jdbcTemplate.update(updateRetriesSql, parameters);

        if (rows != 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        LOG.debug("Finished updateRetries.");
    }

    @Override
    public void delete(final Integer idChunk) {
        LOG.debug("Starting delete [{}]...", idChunk);
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", idChunk);
        final int rows = this.jdbcTemplate.update(deleteSql, parameters);

        if (rows != 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        LOG.debug("Finished delete.");
    }
}
