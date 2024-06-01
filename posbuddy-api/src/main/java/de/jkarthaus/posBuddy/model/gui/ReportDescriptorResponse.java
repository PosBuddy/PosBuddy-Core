package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@Introspected
public class ReportDescriptorResponse {
    private String reportType;
    private String fileName;
    private LocalDateTime creationDate;
}
