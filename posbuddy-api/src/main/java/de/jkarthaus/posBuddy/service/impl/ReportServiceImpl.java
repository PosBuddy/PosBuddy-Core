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

    @Value("${datasources.jasper.url:''}")
    private String jasperUrl;

    @Value("${datasources.jasper.username:''}")
    private String jasperUsername;

    @Value("${datasources.jasper.password:''}")
    private String jasperPassword;

    private Connection databaseConnection;

    @PostConstruct
    private void init() {
        log.info("Init Report Database Connection: {}", jasperUrl);
        try {
            databaseConnection = DriverManager.getConnection(
                    jasperUrl,
                    jasperUsername,
                    jasperPassword
            );
            log.info("established report-database connection to:{}",
                    databaseConnection.getMetaData().getDatabaseProductName()
            );
        } catch (SQLException e) {
            log.error("failed to initialize report database connection", e);
        }
    }

    @Override
    public void createOneTimeIdreport(UUID posBuddyId) throws JRException, IOException, SQLException {
        Map<String, Object> parameters = new HashMap<>();

        JasperReport menueReport = JasperCompileManager
                .compileReport(
                        "/home/jkarthaus/JaspersoftWorkspace/posBuddy/oneTimeID.jrxml"
                );

        HashMap map = new HashMap();
        map.put("REPORT_CONNECTION", databaseConnection);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                menueReport,
                parameters,
                databaseConnection
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
