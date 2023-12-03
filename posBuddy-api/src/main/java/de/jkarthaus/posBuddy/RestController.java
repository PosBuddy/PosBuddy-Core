package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.db.entities.ItemEntity;
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

    @Get(uri = "/items/{station}", produces = MediaType.APPLICATION_JSON)
    public ItemResponse getItems(String station) {
        return itemMapper.toResponse(
                new ItemEntity(
                        "artikel",
                        "einheit",
                        0,
                        "station",
                        0.0

                )
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
