package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class ServingRequest {
    List<ServeItem> serveItems;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Serdeable
    public static class ServeItem {
        String itemId;
        int count;
    }
}
