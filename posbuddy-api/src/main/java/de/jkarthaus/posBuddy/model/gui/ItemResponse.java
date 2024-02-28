package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@Introspected
public class ItemResponse {

    private String id;
    private String unit;
    private Integer minAge;
    private DispensingStation dispensingStation;
    private Double price;

    @Data
    @Serdeable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DispensingStation {
        String id;
        String name;
    }

}
