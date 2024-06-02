package de.jkarthaus.posBuddy.service;


import de.jkarthaus.posBuddy.service.impl.ReportServiceImpl;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    List<ReportServiceImpl.reportDescriptor> getReportList() throws IOException;

    void createOneTimeIdreport(UUID posBuddyId) throws JRException, IOException, SQLException;

    void createMenueReport() throws JRException, IOException, SQLException;

    byte[] getReportData(String filename) throws IOException;
}
