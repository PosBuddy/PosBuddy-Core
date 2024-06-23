package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.ConfigRepository;
import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.mapper.ConfigMapper;
import de.jkarthaus.posBuddy.model.StaticIdData;
import de.jkarthaus.posBuddy.model.config.FtpConfig;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import de.jkarthaus.posBuddy.model.gui.FtpSyncLogResponse;
import de.jkarthaus.posBuddy.service.FtpSyncService;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.serde.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class FtpSyncImpl implements FtpSyncService {

    @Value("${posbuddy.do-ftp-sync:true}")
    private boolean doFtpSync;

    private static final String FTP_HASH_FILE = "posBuddySync.obj";
    private HashMap<String, Integer> lastUploadHashValue = new HashMap<>();

    private FtpConfig posBuddyFtpConfig;
    private FTPClient ftpClient;

    private boolean isServerResponsible = false;

    private FtpSyncLogResponse ftpSyncLogResponse;

    private final ConfigRepository configRepository;
    private final RevenueRepository revenueRepository;
    private final IdentityRepository identityRepository;
    private final ConfigMapper configMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void putFtpServerConfig(FtpConfig ftpConfig) throws IOException {
        configRepository.updateConfig(
                configMapper.toConfigEntity(ftpConfig)
        );
    }

    @Override
    @PostConstruct
    public FtpConfig getFtpServerConfig() {
        posBuddyFtpConfig = new FtpConfig();
        if (!doFtpSync) {
            log.warn("FTP sync is disabled by profile.");
            return posBuddyFtpConfig;
        }
        // init the ftp client
        ftpClient = new FTPClient();
        // -- try to get ftpConfig from database
        log.info("Try to read ftp Server config from Database");
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
        log.info("successfully read FTP config from database");
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
            int ftpClientReplyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(ftpClientReplyCode)) {
                log.error("Ftp Server gives negative reply code :{}", ftpClientReplyCode);
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
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setControlKeepAliveTimeout(Duration.ofSeconds(300));
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
                log.info("get lastUploadHashValue from Ftp Server.");
                ObjectInputStream in = new ObjectInputStream(ftpClient.retrieveFileStream(FTP_HASH_FILE));
                lastUploadHashValue = (HashMap<String, Integer>) in.readObject();
                in.close();
            } catch (EOFException | NullPointerException exception) {
                if (exception instanceof EOFException) {
                    log.error("FTP Hash File:{} has size 0 -> delete this File", FTP_HASH_FILE);
                    ftpClient.deleteFile(FTP_HASH_FILE);
                }
                if (exception instanceof NullPointerException) {
                    log.warn("Sync-State File :{} not found on FTP Server -> create new", FTP_HASH_FILE);
                }
                lastUploadHashValue = new HashMap<>();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(lastUploadHashValue);
                oos.flush();
                oos.close();
                InputStream is = new ByteArrayInputStream(baos.toByteArray());
                ftpClient.storeFile(FTP_HASH_FILE, is);
                is.close();
                //ftpClient.completePendingCommand();
                log.info("create new {} on Ftp server...", FTP_HASH_FILE);
            }
            isServerResponsible = true;
            ftpClient.logout();
            ftpClient.disconnect();
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
        if (!isServerResponsible) {
            this.ftpSyncLogResponse = new FtpSyncLogResponse(
                    LocalDateTime.now(),
                    "Ftp Server is NOT reachable..."
            );
            return;
        }
        List<StaticIdData> uploadStaticIdDataList = new ArrayList<>();
        List<String> removeIdList = new ArrayList<>();
        List<IdentityEntity> identityEntityList = identityRepository.getAllocatedIdentitys();
        identityEntityList.forEach(identityEntity -> {
                    StaticIdData staticIdData = new StaticIdData();
                    staticIdData.setPosBuddyId(identityEntity.getPosbuddyid());
                    staticIdData.setBalance(identityEntity.getBalance());
                    staticIdData.setRevenueList(
                            getStaticRevenueList(identityEntity.getPosbuddyid(), identityEntity.getStartallocation())
                    );
                    if (isObjectModified(staticIdData)) {
                        uploadStaticIdDataList.add(staticIdData);
                    }
                }
        );
        lastUploadHashValue.forEach((posBuddyId, integer) -> {
                    if (identityEntityList
                            .stream()
                            .filter(identityEntity -> identityEntity.getPosbuddyid().equals(posBuddyId))
                            .findAny()
                            .isEmpty()
                    ) {
                        removeIdList.add(posBuddyId);
                    }
                }
        );
        removeIdList.forEach(posBuddyId -> lastUploadHashValue.remove(posBuddyId));
        doServerOperations(uploadStaticIdDataList, removeIdList);
    }


    private void doServerOperations(List<StaticIdData> uploadStaticIdData, List<String> removeIdList) {
        if (uploadStaticIdData.isEmpty() && removeIdList.isEmpty()) {
            log.info("Ftp Server is full synced - nothing to upload or remove");
            return;
        }
        log.info("upload:{} remove:{} identity's", uploadStaticIdData.size(), removeIdList.size());
        try {
            ftpClient.connect(posBuddyFtpConfig.getHost());
            ftpClient.login(
                    posBuddyFtpConfig.getUsername(),
                    posBuddyFtpConfig.getPassword()
            );
            ftpClient.changeWorkingDirectory(posBuddyFtpConfig.getDestination());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setControlKeepAliveTimeout(Duration.ofSeconds(300));
            ftpClient.setDefaultTimeout(10 * 1000);
            for (StaticIdData staticIdData : uploadStaticIdData) {
                objectMapper.writeValue(new FileOutputStream(new File("/tmp/upload.json")), staticIdData);
                ftpClient.storeFile(
                        staticIdData.getPosBuddyId() + ".json",
                        new FileInputStream(new File("/tmp/upload.json"))
                );
                log.info("upload id:{} reply:{}", staticIdData.getPosBuddyId(), ftpClient.getReplyCode());
            }
            // remove id's from ftp server
            for (String posBuddyRemoveId : removeIdList) {
                if (ftpClient.listFiles(posBuddyRemoveId + ".json").length > 0) {
                    log.info("remove id:{} from FTP Server", posBuddyRemoveId);
                    if (!ftpClient.deleteFile(posBuddyRemoveId + ".json")) {
                        log.warn("remove file:{}  failed :{}.",
                                posBuddyRemoveId,
                                ftpClient.getReplyString()
                        );
                    }
                } else {
                    log.warn("try to delete posBuddyId:{} but not found on FTP Server", posBuddyRemoveId);
                }
            }
            // store new dync state file...
            log.info("Store new SyncState");
            ObjectOutputStream out = new ObjectOutputStream(ftpClient.storeFileStream(FTP_HASH_FILE));
            out.writeObject(lastUploadHashValue);
            out.flush();
            out.close();
            ftpClient.completePendingCommand();
            log.info("disconnect from FTP Server");
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            log.error("IOException try to upload staticIdData");
        }
    }

    private boolean isObjectModified(StaticIdData staticIdData) {
        if (lastUploadHashValue.containsKey(staticIdData.getPosBuddyId())) {
            if (lastUploadHashValue.get(staticIdData.getPosBuddyId()) == staticIdData.hashCode()) {
                log.debug("static id:{} unchanged since last upload", staticIdData.getPosBuddyId());
                return false;
            }
            log.debug("static id:{} modified -> upload to server", staticIdData.getPosBuddyId());
            lastUploadHashValue.put(staticIdData.getPosBuddyId(), staticIdData.hashCode());
        }
        log.debug("Static Identity:{} not uploaded yet.", staticIdData.getPosBuddyId());
        lastUploadHashValue.put(staticIdData.getPosBuddyId(), staticIdData.hashCode());
        return true;
    }

    private List<StaticIdData.Revenue> getStaticRevenueList(String posBuddyId, LocalDateTime startAllocation) {
        List<StaticIdData.Revenue> revenueList = new ArrayList<>();
        List<RevenueEntity> revenueEntities = revenueRepository.getRevenuesByIdDescending(posBuddyId, startAllocation);
        revenueEntities.forEach(revenueEntity -> revenueList.add(
                        new StaticIdData.Revenue(
                                revenueEntity.getItemtext(),
                                revenueEntity.getAmount(),
                                revenueEntity.getValue(),
                                revenueEntity.getPaymentaction(),
                                revenueEntity.getTimeofaction()
                        )
                )
        );
        return revenueList;
    }

    @Override
    public FtpSyncLogResponse getLastLog() {
        return ftpSyncLogResponse;
    }
}
