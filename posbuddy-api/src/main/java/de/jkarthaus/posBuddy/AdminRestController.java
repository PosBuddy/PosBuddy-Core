package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.ConfigMapper;
import de.jkarthaus.posBuddy.model.gui.FtpConfigDto;
import de.jkarthaus.posBuddy.model.gui.FtpSyncLogResponse;
import de.jkarthaus.posBuddy.model.gui.SpecialTransactionDto;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.FtpSyncService;
import de.jkarthaus.posBuddy.service.PartyActionService;
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
    final PartyActionService partyActionService;
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

    //-----------------------------------------------------------------------------------------------special transaction
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/specialTransaction/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "forbidden - you need a admin certificate"),
            @ApiResponse(responseCode = "403", description = "Out of Balance, id not valid"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "500", description = "server error occured"),
    })
    @Tag(name = "admin")
    public HttpResponse<String> specialTransaction(
            String posBuddyId,
            @Body SpecialTransactionDto specialTransactionDto,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        try {
            if (securityService.isAdmin(x509Authentication)) {
                log.info("try special transaction...");
                partyActionService.doSpecialRevenue(
                        posBuddyId,
                        specialTransactionDto.getAction(),
                        specialTransactionDto.getValue(),
                        specialTransactionDto.getItemText()
                );
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (OutOfBalanceException oobe) {
            log.error("OutOfBalanceException:{}", oobe.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "Guthaben zu gering");
        } catch (ActionNotSupportetException anse) {
            log.error("ActionNotSupportetException:{}", anse.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, anse.getLocalizedMessage());
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "posBuddyId ungültig");
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "id nicht zugeordnet");
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return HttpResponse.ok();
    }


}
