package de.jkarthaus.posBuddy;

import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.RequestAttribute;
import lombok.RequiredArgsConstructor;

@Controller("/api/v1")
@RequiredArgsConstructor
public class RestController {


    @Get(uri = "/items", produces = MediaType.APPLICATION_JSON)
    public ItemResponse getItems(@RequestAttribute String station) {
        return new ItemResponse(
                "coke",
                "Glass 0.2",
                0,
                "Bar",
                2.2
        );
    }


}
