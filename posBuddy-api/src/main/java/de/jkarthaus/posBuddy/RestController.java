package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.ServingStationResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;


@Controller("/api/v1")
@RequiredArgsConstructor
public class RestController {

    final ItemMapper itemMapper;
    final ItemRepository itemRepository;
    final DispensingStationRepository dispensingStationRepository;

    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    public ItemResponse getItems(String station) {

        return itemMapper.toResponse(
                itemRepository.findByStation(station),
                dispensingStationRepository.getDispensingStations()
        );
    }

    @Get(uri = "/stations", produces = MediaType.APPLICATION_JSON)
    public ServingStationResponse getStations() {
        return new ServingStationResponse(
                "ckB",
                "Cocktail Bar",
                "right to the DJ"
        );
    }


}
