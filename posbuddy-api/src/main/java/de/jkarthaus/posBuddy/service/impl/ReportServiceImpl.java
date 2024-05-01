package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements de.jkarthaus.posBuddy.service.ReportService {

    private final ItemRepository itemRepository;

    @Override
    public void createMenueReport() throws JRException, IOException {
        Map<String, Object> parameters = new HashMap<>();
        List<ItemEntity> itemEntities = itemRepository.findAll();
        JRDataSource JRdataSource = new JRBeanCollectionDataSource(itemEntities);

        parameters.put("datasource", JRdataSource);

        JasperReport menueReport = JasperCompileManager.compileReport(
                "/home/jkarthaus/git/gve-posBuddy/menueReport/menue.jrxml"
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
