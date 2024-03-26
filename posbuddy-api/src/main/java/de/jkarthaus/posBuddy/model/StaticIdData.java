package de.jkarthaus.posBuddy.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class StaticIdData {
    String posBuddyId;
    double balance;
    List<Revenue> revenueList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Serdeable
    private static class Revenue {
        private String itemText;

        private int amount;

        private double value;

        private String action;

        private LocalDateTime timeOfAction;
    }

}
