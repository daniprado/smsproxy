package com.argallar.smsproxy.db.view.report;

import java.util.List;

/**
 * Report data recoverer.  
 */
public interface IReportRepository {

    List<Report> findAll();
}
