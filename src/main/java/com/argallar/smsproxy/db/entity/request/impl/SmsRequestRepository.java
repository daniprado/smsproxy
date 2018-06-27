package com.argallar.smsproxy.db.entity.request.impl;

import java.util.Date;
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

import com.argallar.smsproxy.db.entity.request.ISmsRequestRepository;
import com.argallar.smsproxy.db.entity.request.SmsRequest;
import com.argallar.smsproxy.util.Constants;
import com.argallar.smsproxy.util.InternalErrorException;

/**
 * JDBC-template implementation of ISmsRequestRepository
 */
@PropertySource("classpath:queries.properties")
@Repository
public class SmsRequestRepository implements ISmsRequestRepository {

    private static final Logger LOG = 
            LoggerFactory.getLogger(SmsRequestRepository.class);

    @Value("${query.sms_request.insert}")
    private String insertSql;
    @Value("${query.sms_request.update_send}")
    private String updateSendSql;
    @Value("${query.sms_request.select}")
    private String selectSql;
    @Value("${query.sms_request.select_all}")
    private String selectAllSql;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public SmsRequest insert(final SmsRequest item) {
        LOG.debug("Starting insert [{}]...", item);
        
        final KeyHolder holder = new GeneratedKeyHolder();
        final Date createDate = new Date();
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("msisdn", item.getMsisdn())
                .addValue("originator", item.getOriginator())
                .addValue("message", item.getMessage())
                .addValue("createDate", createDate);
        int rows = this.jdbcTemplate.update(insertSql, parameters, holder);

        if (rows != 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        
        item.setId(holder.getKey().intValue());
        item.setCreateDate(createDate);
        LOG.debug("Finished insert [{}].", item.getId());
        return item;
    }

    @Override
    public void updateSent(final Integer idRequest, String url) {
        LOG.debug("Starting updateSent [{}]...", idRequest);
        final SqlParameterSource parameters = new MapSqlParameterSource() 
                .addValue("id", idRequest)
                .addValue("sendDate", new Date()) 
                .addValue("url", url);
        int rows = this.jdbcTemplate.update(updateSendSql, parameters);

        if (rows != 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        LOG.debug("Finished updateSent.");
    }

    @Override
    public SmsRequest find(final Integer idRequest) {
        LOG.debug("Starting find [{}]...", idRequest);
        final SqlParameterSource parameters = new MapSqlParameterSource() 
                .addValue("id", idRequest);
        List<SmsRequest> result = this.jdbcTemplate.query(selectSql, parameters,
                new BeanPropertyRowMapper<>(SmsRequest.class));
        if (result.size() > 1) {
            throw new InternalErrorException(Constants.MSG_QUERY_IERROR);
        }
        
        SmsRequest item = result.isEmpty() ? null : result.get(0);
        LOG.debug("Finished find [{}].", item);
        return item;
        
    }

    @Override
    public List<SmsRequest> findAll() {
        LOG.debug("Starting findAll...");
        
        List<SmsRequest> result = this.jdbcTemplate.query(selectAllSql, 
                new BeanPropertyRowMapper<>(SmsRequest.class));

        LOG.debug("Finished findAll.");
        return result;
    }
}
