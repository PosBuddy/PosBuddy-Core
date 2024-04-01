package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.DispensingStationResponse;
import de.jkarthaus.posBuddy.model.gui.IdDataResponse;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.service.DataImportService;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.service.SecurityService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all Items for given station"),
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all dispensingStations"),
    })
    @Tag(name = "public")
    public List<DispensingStationResponse> getStations() {
        log.debug("get dispensingStations");
        return partyActionService.getDispensingStations();
    }

    @PermitAll
    @Get(uri = "/identity/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all dispensingstations"),
            @ApiResponse(responseCode = "400", description = "posbuddy id is not valid"),
            @ApiResponse(responseCode = "404", description = "posbuddy id is not allocated"),
    })
    @Tag(name = "public")
    public HttpResponse<IdentityResponse> getIdentity(String posBuddyId) {
        try {
            return HttpResponse.ok(
                    partyActionService.getIdentityResponseByPosBuddyId(posBuddyId)
            );
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        }
    }

    @PermitAll
    @Get(uri = "/identityData/{posBuddyId}", produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all dispensingstations"),
            @ApiResponse(responseCode = "400", description = "posbuddy id is not valid"),
            @ApiResponse(responseCode = "404", description = "posbuddy id is not allocated"),
    })
    @Tag(name = "public")
    public HttpResponse<IdDataResponse> getIdData(String posBuddyId) {
        try {
            return HttpResponse.ok(
                    partyActionService.getIdDataResponse(posBuddyId)
            );
        } catch (posBuddyIdNotValidException e) {
            log.error("posBuddyIdNotValidException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        } catch (posBuddyIdNotAllocatedException e) {
            log.error("posBuddyIdNotAllocatedException:{}", e.getMessage());
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        }
    }
}
