package de.jkarthaus.posBuddy.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@JsonInclude(JsonInclude.Include.ALWAYS)
public class FtpConfig {
    private boolean enabled = false;
    private String host = "";
    private String username = "";
    private String password = "";
    private String destination = "";
}
