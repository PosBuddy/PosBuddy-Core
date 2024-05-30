package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.model.config.FtpConfig;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import de.jkarthaus.posBuddy.model.gui.FtpConfigDto;
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

    @Override
    public ConfigEntity toConfigEntity(FtpConfig ftpConfig) throws IOException {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setId(ConfigID.FTP_CONFIG);
        configEntity.setJsonConfig(
                objectMapper.writeValueAsString(ftpConfig)
        );
        return configEntity;
    }

    @Override
    public FtpConfigDto toFtpConfigDto(FtpConfig ftpConfig) {
        return new FtpConfigDto(
                ftpConfig.isEnabled(),
                ftpConfig.getHost(),
                ftpConfig.getUsername(),
                ftpConfig.getPassword(),
                ftpConfig.getDestination()
        );
    }

    @Override
    public FtpConfig toFtpConfig(FtpConfigDto ftpConfigDto) {
        return new FtpConfig(
                ftpConfigDto.isEnabled(),
                ftpConfigDto.getHost(),
                ftpConfigDto.getUsername(),
                ftpConfigDto.getPassword(),
                ftpConfigDto.getDestination()
        );
    }
}
