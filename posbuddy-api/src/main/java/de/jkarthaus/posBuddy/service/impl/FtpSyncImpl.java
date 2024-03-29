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
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class FtpSyncImpl implements FtpSyncService {

    private static String FTP_HASH_FILE = "posBuddySync.obj";
    private HashMap<String, Integer> lastUploadHashValue;

    private FtpConfig posBuddyFtpConfig;
    private FTPClient ftpClient;

    private boolean isServerResponsible = false;

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
        // init the ftp client
        ftpClient = new FTPClient();
        // -- try to get ftpConfig from database
        log.info("Try to read ftp Server config from Database");
        posBuddyFtpConfig = new FtpConfig();
        Optional<ConfigEntity> optionalConfigEntity = configRepository
                .findById(ConfigID.FTP_CONFIG);
        try {
            if (optionalConfigEntity.isEmpty()) {
                log.warn("No FTP configuration found - create default config...");
                this.ftpSyncLogResponse = new FtpSyncLogResponse(
                        LocalDateTime.now(),
                        "**Default config created**"
                );
                configRepository.updateConfig(configMapper.toConfigEntity(posBuddyFtpConfig));
                return posBuddyFtpConfig;
            }
            posBuddyFtpConfig = configMapper.toFtpConfig(optionalConfigEntity.get());
        } catch (IOException e) {
            log.error("error reading FTP config from database");
            return posBuddyFtpConfig;
        }
        log.info("succesfully read FTP config from database");
        checkFtp();
        return posBuddyFtpConfig;
    }

    private void checkFtp() {
        if (!posBuddyFtpConfig.isEnabled()) {
            return;
        }
        try {
            log.info("check FTP connection to host:{}", posBuddyFtpConfig.getHost());
            ftpClient.connect(posBuddyFtpConfig.getHost());
            int ftpReplycode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(ftpReplycode)) {
                log.error("Ftp Server gives negative reply code :{}", ftpReplycode);
                ftpClient.disconnect();
                isServerResponsible = false;
            }
            boolean loggedIn = ftpClient.login(
                    posBuddyFtpConfig.getUsername(),
                    posBuddyFtpConfig.getPassword()
            );
            if (!loggedIn) {
                log.error("FTP login failed");
                isServerResponsible = false;
                this.ftpSyncLogResponse = new FtpSyncLogResponse(
                        LocalDateTime.now(),
                        "**Ftp login failed...**"
                );
                ftpClient.disconnect();
                return;
            }
            boolean chdir = ftpClient.changeWorkingDirectory(posBuddyFtpConfig.getDestination());
            if (!chdir) {
                log.error("FTP chdir to {} failed", posBuddyFtpConfig.getDestination());
                isServerResponsible = false;
                this.ftpSyncLogResponse = new FtpSyncLogResponse(
                        LocalDateTime.now(),
                        "**Ftp change to directory " + posBuddyFtpConfig.getDestination() + " failed...**"
                );
                ftpClient.logout();
                ftpClient.disconnect();
                return;
            }
            try {
                ObjectInputStream in = new ObjectInputStream(ftpClient.retrieveFileStream(FTP_HASH_FILE));
                lastUploadHashValue = (HashMap<String, Integer>) in.readObject();
                in.close();
            } catch (Exception e) {
                ObjectOutputStream out = new ObjectOutputStream(ftpClient.storeFileStream(FTP_HASH_FILE));
                log.warn("Error reading :{} create new...", FTP_HASH_FILE);
                out.writeObject(lastUploadHashValue);
                out.flush();
                out.close();
            }

        } catch (IOException e) {
            log.error("IoException when trying to connect FTP Server:{}", e.getMessage());
        } catch (Exception e) {
            log.error("ClassNotFoundException :{}", e.getMessage());
        }
    }


    @Scheduled(fixedDelay = "60s", initialDelay = "5s")
    @Override
    public void startSync() {
        if (!posBuddyFtpConfig.isEnabled()) {
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
