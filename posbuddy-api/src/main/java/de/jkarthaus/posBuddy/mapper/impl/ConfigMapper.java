package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.model.FtpConfig;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Singleton
@RequiredArgsConstructor
public class ConfigMapper implements de.jkarthaus.posBuddy.mapper.ConfigMapper {

    final ObjectMapper objectMapper;

    @Override
    public FtpConfig toFtpConfig(ConfigEntity configEntity) throws IOException {
        if (!configEntity.getId().equals(ConfigID.FTP_CONFIG)) {
            throw new RuntimeException(
                    "Entity:" + configEntity + " ist not " + ConfigID.FTP_CONFIG
            );
        }
        return objectMapper.readValue(configEntity.getJsonConfig(), FtpConfig.class);
    }
}
