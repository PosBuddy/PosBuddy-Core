package de.jkarthaus.posBuddy.report;

import de.jkarthaus.posBuddy.service.ReportService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@MicronautTest
public class ReportTest {

    @Inject
    private ReportService reportService;


    @Test
    void testOneTimeReport() {
        try {
            reportService.createOneTimeIdreport();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
