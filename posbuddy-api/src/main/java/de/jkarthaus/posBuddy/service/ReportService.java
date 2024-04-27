package de.jkarthaus.posBuddy.service;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    void createMenueReport() throws JRException, IOException;
}
