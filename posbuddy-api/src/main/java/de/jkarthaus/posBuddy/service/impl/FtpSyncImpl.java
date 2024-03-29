package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.ConfigRepository;
import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.mapper.ConfigMapper;
import de.jkarthaus.posBuddy.model.FtpConfig;
import de.jkarthaus.posBuddy.model.StaticIdData;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import de.jkarthaus.posBuddy.model.gui.FtpSyncLogResponse;
import de.jkarthaus.posBuddy.service.FtpSyncService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class FtpSyncImpl implements FtpSyncService {

    private HashMap<String, Integer> lastUploadHashValue;

    private FtpConfig ftpConfig;

    private FtpSyncLogResponse ftpSyncLogResponse;

    private final ConfigRepository configRepository;
    private final RevenueRepository revenueRepository;
    private final IdentityRepository identityRepository;
    private final ConfigMapper configMapper;

    @Override
    public void putFtpServerConfig(FtpConfig ftpConfig) throws IOException {
        configRepository.updateConfig(
                configMapper.toConfigEntity(ftpConfig)
        );
    }

    @Override
    @PostConstruct
    public FtpConfig getFtpServerConfig() {
        log.info("Try to read ftp Server config from Database");
        ftpConfig = new FtpConfig();
        Optional<ConfigEntity> optionalConfigEntity = configRepository
                .findById(ConfigID.FTP_CONFIG);
        try {
            if (optionalConfigEntity.isEmpty()) {
                log.warn("No FTP configuration found - create default config...");
                this.ftpSyncLogResponse = new FtpSyncLogResponse(
                        LocalDateTime.now(),
                        "**Default config created**"
                );
                configRepository.updateConfig(configMapper.toConfigEntity(ftpConfig));
                return ftpConfig;
            }
            ftpConfig = configMapper.toFtpConfig(optionalConfigEntity.get());
        } catch (IOException e) {
            log.error("error reading FTP config from database");
            return ftpConfig;
        }
        log.info("succesfully read FTP config from database");
        return ftpConfig;
    }

    private void checkFtp() {
        if (!ftpConfig.isEnabled()) {
            return;
        }
        log.info("check configured FTP connection");

    }


    @Scheduled(fixedDelay = "60s", initialDelay = "5s")
    @Override
    public void startSync() {
        if (!ftpConfig.isEnabled()) {
            log.debug("Ftp static sync disabled.");
            this.ftpSyncLogResponse = new FtpSyncLogResponse(
                    LocalDateTime.now(),
                    "Ftp Sync is disabled..."
            );
            return;
        }
        List<StaticIdData> staticIdDataList = new ArrayList<>();
        List<IdentityEntity> identityEntityList = identityRepository.getAllocatedIdentitys();
        identityEntityList.forEach(identityEntity -> {
                    StaticIdData staticIdData = new StaticIdData();
                    staticIdData.setPosBuddyId(identityEntity.getPosbuddyid());
                    staticIdData.setBalance(identityEntity.getBalance());
                    staticIdData.setRevenueList(getStaticRevenueList(identityEntity.getPosbuddyid()));
                    if (isObjectModified(staticIdData)) {
                        staticIdDataList.add(staticIdData);
                    }
                }
        );
        doUpload(staticIdDataList);
    }

    private void doUpload(List<StaticIdData> staticIdDataList) {
        log.info("upload {} identitys to ftp Server", staticIdDataList.size());
        //TODO : implement upload
    }

    private boolean isObjectModified(StaticIdData staticIdData) {
        if (lastUploadHashValue.containsKey(staticIdData.getPosBuddyId())) {
            if (lastUploadHashValue.get(staticIdData.getPosBuddyId()) == staticIdData.hashCode()) {
                log.debug("static id:{} unchaned since last upload", staticIdData.getPosBuddyId());
                return false;
            }
            log.debug("static id:{} modified -> upload to server", staticIdData.getPosBuddyId());
            lastUploadHashValue.put(staticIdData.getPosBuddyId(), staticIdData.hashCode());
        }
        log.debug("Static Identity:{} not uploaded yet.", staticIdData.getPosBuddyId());
        lastUploadHashValue.put(staticIdData.getPosBuddyId(), staticIdData.hashCode());
        return true;
    }

    private List<StaticIdData.Revenue> getStaticRevenueList(String posBuddyId) {
        List<StaticIdData.Revenue> revenueList = new ArrayList<>();
        List<RevenueEntity> revenueEntities = revenueRepository.getRevenuesByIdDescending(posBuddyId);
        revenueEntities.stream().forEach(revenueEntity -> {
                    revenueList.add(new StaticIdData.Revenue(
                                    revenueEntity.getItemtext(),
                                    revenueEntity.getAmount(),
                                    revenueEntity.getValue(),
                                    revenueEntity.getPaymentaction(),
                                    revenueEntity.getTimeofaction()
                            )
                    );
                }
        );
        return revenueList;
    }

    @Override
    public FtpSyncLogResponse getLastLog() {
        return ftpSyncLogResponse;
    }
}
