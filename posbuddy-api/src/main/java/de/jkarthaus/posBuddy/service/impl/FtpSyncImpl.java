package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.ConfigRepository;
import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.mapper.ConfigMapper;
import de.jkarthaus.posBuddy.model.FtpConfig;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import de.jkarthaus.posBuddy.service.FtpSyncService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class FtpSyncImpl implements FtpSyncService {

    private HashMap<String, Integer> lastUploadHashValue;

    private FtpConfig ftpConfig;

    private boolean isConfigValid = false;

    private String lastLog;

    private final ConfigRepository configRepository;
    private final ConfigMapper configMapper;

    @Override
    public void putFtpServerConfig(ftoConfigRecord ftoConfigRecord) {

    }

    @Override
    @PostConstruct
    public void getFtpServerConfig() {
        log.info("Try to read ftp Server config from Database");
        ftpConfig = new FtpConfig();
        Optional<ConfigEntity> optionalConfigEntity = configRepository
                .findById(ConfigID.FTP_CONFIG);
        if (optionalConfigEntity.isEmpty()) {
            log.warn("No FTP configuration found - cannot create static guest data.");
            isConfigValid = false;
            this.lastLog = """
                    **ERROR**  
                     No FTP configuration found - _cannot create static guest data._
                    """;
            return;
        }
        try {
            ftpConfig = configMapper.toFtpConfig(optionalConfigEntity.get());
        } catch (IOException e) {
            log.error("error reading FTP config from database");
            isConfigValid = false;
            return;
        }
        isConfigValid = true;
        log.info("succesfully read FTP config from database");
    }

    @Scheduled(fixedDelay = "60s", initialDelay = "5s")
    @Override
    public void startSync() {
        log.info("Start FTP Snyc...");

        if (!isConfigValid) {
            log.warn("Ftp config not set.");
            return;
        }

    }

    @Override
    public String getLastLog() {
        return lastLog;
    }
}
