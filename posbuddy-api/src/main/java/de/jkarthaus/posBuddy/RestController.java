package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.ServingRequest;
import de.jkarthaus.posBuddy.model.gui.ServingStationResponse;
import de.jkarthaus.posBuddy.service.PartyActionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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
            return  partyActionService.getIdentityResponseByPosBuddyId(posBuddyId);
        } catch (posBuddyIdNotValidException e) {
            throw new RuntimeException(e);
        } catch (posBuddyIdNotAssignedException e) {
            throw new RuntimeException(e);
        }
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

        return HttpResponse.ok();
    }


}
