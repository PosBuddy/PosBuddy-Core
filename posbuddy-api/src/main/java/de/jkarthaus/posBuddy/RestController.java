package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.*;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.x509.X509Authentication;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;


@Controller("/api/v1")
@RequiredArgsConstructor
@Slf4j

public class RestController {

    final ItemMapper itemMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;
    final PartyActionService partyActionService;
    final DataImportService dataImportService;
    final SecurityService securityService;

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    public ItemResponse getItems(String station) {
        log.debug("get items for station:{}", station);
        return itemMapper.toResponse(
                itemRepository.findByStation(station),
                dispensingStationRepository.getDispensingStations()
        );
    }

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/stations", produces = MediaType.APPLICATION_JSON)
    public ServingStationResponse getStations() {
        log.debug("get stations");
        return new ServingStationResponse(
                "ckB",
                "Cocktail Bar",
                "right to the DJ"
        );
    }

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/identity/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    public IdentityResponse getIdentity(String posBuddyId) {

        try {
            return partyActionService.getIdentityResponseByPosBuddyId(posBuddyId);
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            throw new RuntimeException(e);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Secured(IS_ANONYMOUS)
    @Post(uri = "/importItems", produces = MediaType.APPLICATION_JSON)
    public HttpResponse importItems(@Nullable X509Authentication x509Authentication,
                                    @Nullable Authentication authentication) {
        try {
            dataImportService.importItemCsv();
        } catch (ParseImportException e) {
            log.error("ParseImportException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }

    @Secured(IS_ANONYMOUS)
    @Post(uri = "/serve/{posId}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse serve(
            String posId,
            ServingRequest servingRequest,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("add {} items for revenue", servingRequest.getServeItems().size());
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
        }
        if (!securityService.isServeStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.serveItems(servingRequest, posId);
        } catch (posBuddyIdNotAllocatedException e) {
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }

    @Secured(IS_ANONYMOUS)
    @Post(uri = "/allocate/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
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
            throw new RuntimeException(e);
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/deAllocate/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
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
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            throw new RuntimeException(e);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }

    @Secured(IS_ANONYMOUS)
    @Post(uri = "/payment/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse payment(
            String posBuddyId,
            @QueryValue Float value,
            @Nullable X509Authentication x509Authentication,
            @Nullable Authentication authentication) {
        log.info("payout {} EUR from posBuddyId:", posBuddyId);
        if (x509Authentication != authentication) {
            log.error("ERROR: Authentication and X509Authentication should be the same instance");
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        if (!securityService.isCheckoutStation(x509Authentication)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        try {
            partyActionService.payment(posBuddyId, value);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            throw new RuntimeException(e);
        } catch (OutOfBalanceException e) {
            log.error("OutOfBalanceException:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return HttpResponse.ok();
    }


}
