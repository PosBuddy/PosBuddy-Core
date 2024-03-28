package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.model.FtpConfig;
import de.jkarthaus.posBuddy.model.gui.FtpSyncLogResponse;

import java.io.IOException;

public interface FtpSyncService {

    FtpSyncLogResponse getLastLog();

    void putFtpServerConfig(FtpConfig ftpConfig) throws IOException;

    FtpConfig getFtpServerConfig();

    void startSync();

}
