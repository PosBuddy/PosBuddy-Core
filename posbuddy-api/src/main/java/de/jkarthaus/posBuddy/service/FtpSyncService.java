package de.jkarthaus.posBuddy.service;

public interface FtpSyncService {

    String getLastLog();

    static record ftoConfigRecord(
            String server,
            String username,
            String password
    ) {
    }

    void putFtpServerConfig(ftoConfigRecord ftoConfigRecord);

    void getFtpServerConfig();

    void startSync();

}
