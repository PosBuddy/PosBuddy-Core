package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import de.jkarthaus.posBuddy.model.gui.DispensingStationResponse;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Controller(value = "/api/v1", port = "${ micronaut.server.ssl.port }")
@RequiredArgsConstructor
@Slf4j
public class PublicRestController {

    final ItemMapper itemMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;
    final PartyActionService partyActionService;
    final DataImportService dataImportService;
    final SecurityService securityService;

    @PermitAll
    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "public")
    public List<ItemResponse> getItems(String station) {
        log.debug("get items for station:{}", station);
        return itemMapper.toResponse(
                itemRepository.findByStation(station),
                dispensingStationRepository.getDispensingStations()
        );
    }

    @PermitAll
    @Get(uri = "/stations", produces = MediaType.APPLICATION_JSON)
    @Tag(name = "public")
    public DispensingStationResponse getStations() {
        log.debug("get stations");
        return new DispensingStationResponse(
                "ckB",
                "Cocktail Bar",
                "right to the DJ"
        );
    }

    @PermitAll
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

    @PermitAll
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
