package de.jkarthaus.posBuddy.service;


import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public interface ReportService {
    void createOneTimeIdreport(UUID posBuddyId) throws JRException, IOException, SQLException;

    void createMenueReport() throws JRException, IOException, SQLException;
}
