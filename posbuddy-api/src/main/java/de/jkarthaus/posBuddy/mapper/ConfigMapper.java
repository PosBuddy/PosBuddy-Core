package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.model.FtpConfig;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public interface ConfigMapper {

    FtpConfig toFtpConfig(ConfigEntity configEntity) throws IOException;


}
