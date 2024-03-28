package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Serdeable
@NoArgsConstructor
@AllArgsConstructor
public class FtpConfigDto {
    private String host;
    private String username;
    private String password;

}
