package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class RevenueResponse {

    List<RevenueEntry> revenueEntryList = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Serdeable
    public static class RevenueEntry {
        private String itemText;

        private int amount;

        private double value;

        private String action;

        private LocalDateTime timeOfAction;

    }

}