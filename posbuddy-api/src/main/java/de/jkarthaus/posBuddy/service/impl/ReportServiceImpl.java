package de.jkarthaus.posBuddy.service.impl;

import io.micronaut.context.annotation.Value;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements de.jkarthaus.posBuddy.service.ReportService {

    @Value("${posbuddy.do-ftp-sync:true}")
    private boolean doFtpSync;


    @PostConstruct
    private void init() {

    }

    @Override
    public void createOneTimeIdreport(UUID posBuddyId) throws JRException, IOException, SQLException {
        Map<String, Object> parameters = new HashMap<>();

        JasperReport menueReport = JasperCompileManager
                .compileReport(
                        "/home/jkarthaus/JaspersoftWorkspace/posBuddy/oneTimeID.jrxml"
                );

        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/posbuddy",
                "posbuddy",
                "posBuddy"
        );

        HashMap map = new HashMap();
        map.put("REPORT_CONNECTION", conn);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                menueReport,
                parameters
        );

        Files.write(
                Path.of("/tmp/oneTimeId.pdf"),
                JasperExportManager.exportReportToPdf(jasperPrint)
        );


    }


    @Override
    public void createMenueReport() throws JRException, IOException, SQLException {
        Map<String, Object> parameters = new HashMap<>();


    }


}
