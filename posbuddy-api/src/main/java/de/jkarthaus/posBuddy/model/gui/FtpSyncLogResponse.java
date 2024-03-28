package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Serdeable
@AllArgsConstructor
@NoArgsConstructor
public class FtpSyncLogResponse {
    private LocalDateTime lastSyncAttempt;
    private String log;
}
