package de.jkarthaus.posBuddy.service.impl;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements de.jkarthaus.posBuddy.service.ReportService {

    private final EntityManager entityManager;

    @Override
    public void createMenueReport() throws JRException, IOException {
        JasperReport menueReport = JasperCompileManager.compileReport(
                "/home/jkarthaus/git/gve-posBuddy/menueReport/menue.jrxml"
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(
                JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER,
                entityManager
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                menueReport,
                parameters
        );

        Files.write(
                Path.of("/tmp/menue.pdf"),
                JasperExportManager.exportReportToPdf(jasperPrint)
        );


    }


}
