package de.jkarthaus.posBuddy.model.gui;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.jkarthaus.posBuddy.model.enums.ReportType;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ReportItemResponse {
    ReportType reportType;
    String fileName;
    LocalDateTime creationDate;
}
