package com.argallar.smsproxy.db.view.report.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.argallar.smsproxy.db.entity.chunk.impl.SmsChunkRepository;
import com.argallar.smsproxy.db.view.report.IReportRepository;
import com.argallar.smsproxy.db.view.report.Report;

/**
 * JDBC-template implementation of IReportRepository
 */
@PropertySource("classpath:queries.properties")
@Repository
public class ReportRepository implements IReportRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SmsChunkRepository.class);

    @Value("${query.report.select_all}")
    private String selectSql;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Report> findAll() {
        LOG.debug("Starting findAll...");

        List<Report> result = this.jdbcTemplate.query(selectSql, 
                new BeanPropertyRowMapper<>(Report.class));
        
        LOG.debug("Finished findAll.");
        return result;
    }

}
