package de.jkarthaus.posBuddy.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class FtpConfig {
    private String host;
    private String username;
    private String password;
}
