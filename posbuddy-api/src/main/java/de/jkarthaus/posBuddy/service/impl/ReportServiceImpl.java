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

    public static final String ONE_TIME_REPORT_PREFIX = "oneTimeID_";

    private static final String ONE_TIME_REPORT = "oneTimeID.jrxml";

    @Value("${datasources.jasper.url:''}")
    private String jasperUrl;

    @Value("${datasources.jasper.username:''}")
    private String jasperUsername;

    @Value("${datasources.jasper.password:''}")
    private String jasperPassword;

    @Value("${report.src}")
    private Path reportSource;

    @Value("${report.dest}")
    private Path reportDest;


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
        if (!reportSource.resolve(ONE_TIME_REPORT).toFile().exists()) {
            log.error("could not find :{} at :{}", ONE_TIME_REPORT, reportSource);
        }
        if (!reportDest.toFile().exists()) {
            log.warn("report-dest directory:{} does not exist -> create", reportDest);
            try {
                Files.createDirectories(reportDest);
            } catch (IOException e) {
                log.error("error on create report dest directory:{}", e);
            }
        }
    }

    @Override
    public void createOneTimeIdreport(UUID posBuddyId) throws JRException, IOException, SQLException {
        Map<String, Object> parameters = new HashMap<>();
        JasperReport menueReport = JasperCompileManager.compileReport(
                reportSource.resolve(ONE_TIME_REPORT).toString()
        );
        parameters.put(
                "posBuddyId", posBuddyId.toString()
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                menueReport,
                parameters,
                databaseConnection
        );
        String filename = ONE_TIME_REPORT_PREFIX + System.currentTimeMillis() + ".pdf";
        Files.write(
                reportDest.resolve(filename),
                JasperExportManager.exportReportToPdf(jasperPrint)
        );
        log.info("oneTimeId Report saved to:{}", filename);
    }


    @Override
    public void createMenueReport() throws JRException, IOException, SQLException {
        Map<String, Object> parameters = new HashMap<>();


    }


}
