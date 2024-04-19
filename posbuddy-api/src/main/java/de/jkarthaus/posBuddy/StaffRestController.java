package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.DispensingStationMapper;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.mapper.PermissionMapper;
import de.jkarthaus.posBuddy.model.gui.*;
import de.jkarthaus.posBuddy.service.PartyActionService;
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

import java.util.List;

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;


@Controller(value = "staff/api/v1", port = "${ micronaut.server.ssl.port }")
@RequiredArgsConstructor
@Slf4j
public class StaffRestController {

    final ItemMapper itemMapper;
    final DispensingStationMapper dispensingStationMapper;
    final PermissionMapper permissionMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;
    final PartyActionService partyActionService;
    final SecurityService securityService;


    //-----------------------------------------------------------------------------------------------------------------permissions
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/permissions", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get permissions by certificate"),
    })
    @Tag(name = "secure")
    public PermissionResponse getPermissions(
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.debug("get Permissions based on client Certificate");
        return permissionMapper.toResponse(
                securityService.getPermissions(x509Authentication)
        );
    }

    //-----------------------------------------------------------------------------------------------------------------items
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item list"),
    })
    @Tag(name = "secure")
    public List<ItemResponse> getItems() {
        log.debug("get all items");
        return itemMapper.toResponse(
                itemRepository.findAll(),
                dispensingStationRepository.getDispensingStations()
        );
    }

    //------------------------------------------------------------------------------------------------dispensingSTation
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items/{dispensingStation}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item list for given station"),
    })
    @Tag(name = "secure")
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
    @Tag(name = "secure")
    public List<DispensingStationResponse> getStations() {
        log.debug("get all dispensing stations");
        return dispensingStationMapper.toResponse(
                dispensingStationRepository.getDispensingStations()
        );
    }

    //--------------------------------------------------------------------------------------------------------------identity
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/identity/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "ID not valid"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "secure")
    public HttpResponse<IdentityResponse> getIdentity(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication
    ) {
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isServeOrCheckout(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            return HttpResponse.ok(partyActionService.getIdentityResponseByPosBuddyId(posBuddyId));
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        }
    }

    //---------------------------------------------------------------------------------------------------------------revenue
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/revenue/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "secure")
    public List<RevenueResponse> getRevenue(String posBuddyId) {
        try {
            return partyActionService.getRevenueResponseByPosBuddyId(posBuddyId);
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            throw new RuntimeException(e);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------Serve
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/serve/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "secure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "balance to low"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
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
            if (securityService.isServeOrCheckout(x509Authentication)) {
                partyActionService.serveItems(serveItems, posBuddyId);
            } else {
                log.warn("forbidden access to serve endpoint");
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        } catch (OutOfBalanceException e) {
            log.error("out of balance exception:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
        return HttpResponse.ok();
    }

    //--------------------------------------------------------------------------------------------------------------Allocate
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/allocate/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "409", description = "ID already allocated"),
            @ApiResponse(responseCode = "400", description = "ID not valid"),
    })
    @Tag(name = "secure")
    public HttpResponse allocate(
            String posBuddyId,
            AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("allocate Person to posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.allocatePosBuddyId(posBuddyId, allocatePosBuddyIdRequest);
        } catch (PosBuddyIdNotAllocateableException e) {
            log.error("PosBuddyIdNotAllocateableException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.CONFLICT);
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
        return HttpResponse.ok();
    }

    //------------------------------------------------------------------------------------------------------------deAllocate
    @Secured(IS_ANONYMOUS)
    @Get(uri = "/deAllocate/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "ID is not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "400", description = "ID is static or Balance not 0"),
    })
    @Tag(name = "secure")
    public HttpResponse deAllocate(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("deAllocate Person from posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.deAllocatePosBuddyId(posBuddyId);
        } catch (posBuddyIdNotAllocatedException | posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.notFound();
        } catch (OutOfBalanceException | PosBuddyIdStaticException e) {
            log.error("OutOfBalance or static ID Exception : {}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return HttpResponse.ok();
    }

    //----------------------------------------------------------------------------------------------------------------Payout
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/payout/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "No balance"),
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "secure")
    public HttpResponse payout(
            String posBuddyId,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("payout {} EUR from posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.notAllowed();
        }
        if (!securityService.isCheckoutStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.payout(posBuddyId);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.notFound();
        } catch (OutOfBalanceException e) {
            log.error("OutOfBalanceException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
        return HttpResponse.ok();
    }

    //---------------------------------------------------------------------------------------------------------------deposit
    @Secured(IS_ANONYMOUS)
    @Post(uri = "/deposit/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Forbidden - you need a checkout certificate"),
            @ApiResponse(responseCode = "404", description = "ID not allocated"),
            @ApiResponse(responseCode = "405", description = "Not allowed - you need a valid certificate"),
    })
    @Tag(name = "secure")
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
        if (!securityService.isCheckoutStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.addDeposit(posBuddyId, value);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.notFound();
        }
        return HttpResponse.ok();
    }


}
