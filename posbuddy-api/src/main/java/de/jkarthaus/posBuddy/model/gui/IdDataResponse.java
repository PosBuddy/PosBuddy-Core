package de.jkarthaus.posBuddy.model.gui;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.ALWAYS)
public class IdDataResponse {
    String posBuddyId;
    private String surName;
    private String lastName;
    private boolean youthProtectionAct;
    double balance;
    List<Revenue> revenueList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Serdeable
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class Revenue {
        private String itemText;

        private int amount;

        private double value;

        private String action;

        private LocalDateTime timeOfAction;
    }

}
