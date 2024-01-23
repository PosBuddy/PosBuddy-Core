package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import de.jkarthaus.posBuddy.model.gui.ServingStationResponse;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;


@Controller(value = "/api/v1", port = "${ micronaut.server.port }")
@RequiredArgsConstructor
@Slf4j
public class PublicRestController {

    final ItemMapper itemMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;
    final PartyActionService partyActionService;
    final DataImportService dataImportService;
    final SecurityService securityService;

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "public")

    public ItemResponse getItems(String station) {
        log.debug("get items for station:{}", station);
        return itemMapper.toResponse(
                itemRepository.findByStation(station),
                dispensingStationRepository.getDispensingStations()
        );
    }

    @Secured(IS_ANONYMOUS)
    @Get(uri = "/stations", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "public")
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
    @Tag(name = "public")

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
    @Get(uri = "/revenue/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "public")

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


}
