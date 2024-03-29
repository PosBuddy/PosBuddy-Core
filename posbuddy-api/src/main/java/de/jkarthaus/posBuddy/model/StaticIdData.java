package de.jkarthaus.posBuddy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@JsonInclude(JsonInclude.Include.ALWAYS)
@EqualsAndHashCode
public class StaticIdData {
    String posBuddyId;
    double balance;
    @EqualsAndHashCode.Exclude
    LocalDateTime syncTimeStamp = LocalDateTime.now();
    List<Revenue> revenueList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
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
