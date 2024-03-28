package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.exception.ParseImportException;
import de.jkarthaus.posBuddy.mapper.ConfigMapper;
import de.jkarthaus.posBuddy.model.gui.FtpConfigDto;
import de.jkarthaus.posBuddy.model.gui.FtpSyncLogResponse;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.FtpSyncService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.x509.X509Authentication;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;


@Controller(value = "/api/v1", port = "${ micronaut.server.ssl.port }")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {

    final FtpSyncService ftpSyncService;
    final SecurityService securityService;
    final DataImportService dataImportService;
    final ConfigMapper configMapper;

    //--------------------------------------------------------------------------------------------------store ftp config
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/ftpConfig", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "server error occured"),
            @ApiResponse(responseCode = "401", description = "forbidden - you need a admin certificate"),
            @ApiResponse(responseCode = "405", description = "not allowed - you need a valid certificate"),
    })
    @Tag(name = "admin")
    public HttpResponse<String> setConfig(
            @Body FtpConfigDto ftpConfigDto,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                ftpSyncService.putFtpServerConfig(
                        configMapper.toFtpConfig(ftpConfigDto)
                );
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpResponse.ok();
    }

    //----------------------------------------------------------------------------------------------------get ftp config
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/ftpConfig", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "server error occured"),
            @ApiResponse(responseCode = "401", description = "forbidden - you need a admin certificate"),
            @ApiResponse(responseCode = "405", description = "not allowed - you need a valid certificate"),
    })
    @Tag(name = "admin")
    public HttpResponse<FtpConfigDto> getFtpConfig(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                return HttpResponse.ok(
                        configMapper.toFtpConfigDto(ftpSyncService.getFtpServerConfig())
                );
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage());
            return HttpResponse.serverError();
        }
    }

    //--------------------------------------------------------------------------------------get ftp synchronisation log
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/ftpSyncLog", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "server error occured"),
            @ApiResponse(responseCode = "401", description = "forbidden - you need a admin certificate"),
            @ApiResponse(responseCode = "405", description = "not allowed - you need a valid certificate"),
    })
    @Tag(name = "admin")
    public HttpResponse<FtpSyncLogResponse> getFtpSyncLog(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                return HttpResponse.ok(
                        ftpSyncService.getLastLog()
                );
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage());
            return HttpResponse.serverError();
        }
    }

    //-------------------------------------------------------------------------------------------------------ImportStations
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/importDispensingStations", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "admin")
    public HttpResponse importDispensingStations(@Nullable X509Authentication x509Authentication,
                                                 @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                dataImportService.importDispensingStationCsv();
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (ParseImportException e) {
            log.error("ParseImportException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }


    //----------------------------------------------------------------------------------------------------------ImportItems
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/importItems", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "admin")
    public HttpResponse<String> importItems(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                dataImportService.importItemCsv();
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (ParseImportException e) {
            log.error("ParseImportException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }


}
