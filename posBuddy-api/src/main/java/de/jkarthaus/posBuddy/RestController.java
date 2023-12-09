package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.ServingRequest;
import de.jkarthaus.posBuddy.model.gui.ServingStationResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.RequestAttribute;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RestController {

    final ItemMapper itemMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;

    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    public ItemResponse getItems(String station) {
        log.debug("get items for station:{}", station);
        return itemMapper.toResponse(
                itemRepository.findByStation(station),
                dispensingStationRepository.getDispensingStations()
        );
    }

    @Get(uri = "/stations", produces = MediaType.APPLICATION_JSON)
    public ServingStationResponse getStations() {
        log.debug("get stations");
        return new ServingStationResponse(
                "ckB",
                "Cocktail Bar",
                "right to the DJ"
        );
    }

    @Post(uri = "/serve/{posId}", produces = MediaType.APPLICATION_JSON)

    public HttpResponse serve(String posId, ServingRequest servingRequest) {
        log.info("add {} items for revenue", servingRequest.getServeItems().size());
        return HttpResponse.ok();
    }


}
