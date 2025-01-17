package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.DispensingStationMapper;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.mapper.PermissionMapper;
import de.jkarthaus.posBuddy.mapper.ReportItemMapper;
import de.jkarthaus.posBuddy.model.gui.*;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.service.ReportService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.x509.X509Authentication;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;


@Controller(value = "staff/api/v1", port = "${ micronaut.server.ssl.port }")
@RequiredArgsConstructor
@Slf4j
public class StaffRestController {

    final ReportItemMapper reportItemMapper;
    final ItemMapper itemMapper;
    final DispensingStationMapper dispensingStationMapper;
    final PermissionMapper permissionMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;
    final PartyActionService partyActionService;
    final ReportService reportService;
    final SecurityService securityService;


    //------------------------------------------------------------------------------------------------------permissions
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/permissions", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get permissions by certificate"),
    })
    @Tag(name = "staff")
    public PermissionResponse getPermissions(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.debug("get Permissions based on client Certificate");
        return permissionMapper.toResponse(
                securityService.getPermissions(x509Authentication)
        );
    }

    //----------------------------------------------------------------------------------------------get all reprt items
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/reportItems", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item list"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout or admin certificate"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "staff")
    public HttpResponse<List<ReportItemResponse>> getReportItems(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Kasse oder Admin Berechtigung erforderlich");
        }
        log.debug("get all report items");
        try {
            return HttpResponse.ok(reportItemMapper.toResponse(reportService.getReportList()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------------get the report
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/reportData/{filename}")
    @Produces("application/pdf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "the report as pdf"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout or admin certificate"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "staff")
    public HttpResponse<byte[]> getReportData(
            String filename,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Kasse oder Admin Berechtigung erforderlich");
        }
        log.debug("get report data");
        try {
            return HttpResponse.ok(reportService.getReportData(filename));
        } catch (IOException e) {
            log.error(e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //---------------------------------------------------------------------------------------------------------get items
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item list"),
    })
    @Tag(name = "staff")
    public List<ItemResponse> getItems() {
        log.debug("get all items");
        return itemMapper.toResponse(
                itemRepository.findAll(),
                dispensingStationRepository.getDispensingStations()
        );
    }

    //------------------------------------------------------------------------------------get Items on dispensingSTation
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items/{dispensingStation}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item list for given station"),
    })
    @Tag(name = "staff")
    public List<ItemResponse> getFilterdItems(String dispensingStation) {
        return itemMapper.toResponse(
                itemRepository.findByStation(dispensingStation),
                dispensingStationRepository.getDispensingStations()
        );
    }

    //------------------------------------------------------------------------------------------------dispensingStations
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/dispensingStations", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dispensing ststation list"),
    })
    @Tag(name = "staff")
    public List<DispensingStationResponse> getStations() {
        log.debug("get all dispensing stations");
        return dispensingStationMapper.toResponse(
                dispensingStationRepository.getDispensingStations()
        );
    }

    //----------------------------------------------------------------------------------------------------------identity
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/identity/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "ID not valid"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a serve, checkout or admin certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "staff")
    public HttpResponse<IdentityResponse> getIdentity(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication
    ) {
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isServeOrCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Keine ausgabe,kasse oder admin berechtigung");
        }
        try {
            return HttpResponse.ok(
                    partyActionService.getIdentityResponseByPosBuddyId(posBuddyId)
            );
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "ID ungültig");
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "ID nicht zugewiesen");
        }
    }

    //----------------------------------------------------------------------------------------------------------revenue
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/revenue/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "ID not valid"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a serve, checkout or admin certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "staff")
    public HttpResponse<List<RevenueResponse>> getRevenue(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication
    ) {
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isServeOrCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Keine ausgabe,kasse oder admin berechtigung");
        }
        try {
            return HttpResponse.ok(partyActionService.getRevenueResponseByPosBuddyId(posBuddyId));
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "ID ungültig");
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "ID nicht zugewiesen");
        }
    }


    //-------------------------------------------------------------------------------------------------------Serve Items
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/serve/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "staff")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "balance to low"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout or admin certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    public HttpResponse<String> serve(
            String posBuddyId,
            @Body List<ServeItem> serveItems,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("add {} items for revenue", serveItems.size());
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
        }
        try {
            if (securityService.isServeOrCheckoutOrAdmin(x509Authentication)) {
                partyActionService.serveItems(serveItems, posBuddyId);
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN, "keine ausgabe,kasse oder admin berechtigung");
            }
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (OutOfBalanceException e) {
            log.error("out of balance exception:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "Kein ausreichendes Guthaben");
        }
        return HttpResponse.ok();
    }

    //-------------------------------------------------------------------------------------Allocate volatile posBuddyId
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/allocateVolatile/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "409", description = "ID already allocated"),
            @ApiResponse(responseCode = "400", description = "ID not valid"),
    })
    @Tag(name = "staff")
    public HttpResponse allocateVolatileId(
            String posBuddyId,
            @Body AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("allocate Person to posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Keine Berechtigung");
        }
        try {
            partyActionService.allocatePosBuddyId(
                    posBuddyId,
                    allocatePosBuddyIdRequest,
                    false
            );
        } catch (PosBuddyIdNotAllocateableException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.CONFLICT, "ID bereits zugewiesen");
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "ID ist ungültig");
        }
        return HttpResponse.ok();
    }

    //-------------------------------------------------------------------------------------Allocate static posBuddyId
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/allocateStatic/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a admin certificate"),
            @ApiResponse(responseCode = "409", description = "ID already allocated"),
            @ApiResponse(responseCode = "400", description = "ID not valid"),
    })
    @Tag(name = "staff")
    public HttpResponse allocateStatic(
            String posBuddyId,
            @Body AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("allocate Person to posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Keine Admin Berechtigung");
        }
        try {
            partyActionService.allocatePosBuddyId(
                    posBuddyId,
                    allocatePosBuddyIdRequest,
                    true
            );
        } catch (PosBuddyIdNotAllocateableException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.CONFLICT, "ID bereits zugewiesen");
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "ID ist ungültig");
        }
        return HttpResponse.ok();
    }

    //-------------------------------------------------------------------------------------Allocate one time posBuddyId
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/allocateOneTimeId", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "409", description = "ID already allocated"),
            @ApiResponse(responseCode = "400", description = "ID not valid"),
    })
    @Tag(name = "staff")
    public HttpResponse allocateOneTimePosBuddyId(
            @Body AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("allocate Person to a one time posBuddyId");
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.allocateOneTimePosBuddyId(allocatePosBuddyIdRequest);
        } catch (PosBuddyIdNotAllocateableException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.CONFLICT, "ID bereits zugewiesen");
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "ID nicht gültig");
        } catch (JRException | SQLException | IOException serverException) {
            log.error("Exception:{}", serverException.getMessage());
            return HttpResponse.serverError(serverException.getMessage());
        }
        return HttpResponse.ok();
    }

    //-------------------------------------------------------------------------------------------------------deAllocate
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/deAllocate/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "ID is not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "400", description = "ID is static or Balance not 0"),
    })
    @Tag(name = "staff")
    public HttpResponse deAllocate(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("deAllocate Person from posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Keine Kasse oder Admin Berechtigung");
        }
        try {
            partyActionService.deAllocatePosBuddyId(posBuddyId);
        } catch (posBuddyIdNotAllocatedException | posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "ID nicht zugewiesen");
        } catch (OutOfBalanceException | PosBuddyIdStaticException e) {
            log.error("OutOfBalance or static ID Exception : {}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return HttpResponse.ok();
    }

    //-----------------------------------------------------------------------------------------------------------Payout
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/payout/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "No balance"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "staff")
    public HttpResponse payout(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("payout {} EUR from posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Kasse oder Admin berechtigung erforderlich");
        }
        try {
            partyActionService.payout(posBuddyId);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "ID nicht zugewiesen");
        } catch (OutOfBalanceException e) {
            log.error("OutOfBalanceException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "Kein Guthaben");
        }
        return HttpResponse.ok();
    }

    //-----------------------------------------------------------------------------------------------------------deposit
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/deposit/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout or admin certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "500", description = "Server Error occurred"),
    })
    @Tag(name = "staff")
    public HttpResponse deposit(
            String posBuddyId,
            @QueryValue Float value,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("deposit {} EUR for posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutOrAdmin(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN, "Kasse oder Admin berechtigung erforderlich");
        }
        try {
            partyActionService.addDeposit(posBuddyId, value);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND, "ID nicht zugewiesen");
        } catch (IOException e) {
            log.error("IOException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return HttpResponse.ok();
    }


}
