package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.model.config.FtpConfig;
import de.jkarthaus.posBuddy.model.gui.FtpConfigDto;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public interface ConfigMapper {

    FtpConfig toFtpConfig(ConfigEntity configEntity) throws IOException;


    ConfigEntity toConfigEntity(FtpConfig ftpConfig) throws IOException;

    FtpConfigDto toFtpConfigDto(FtpConfig ftpConfig);

    FtpConfig toFtpConfig(FtpConfigDto ftpConfigDto);
}
