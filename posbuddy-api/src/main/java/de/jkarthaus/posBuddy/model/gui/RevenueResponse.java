package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class RevenueResponse {

    private String itemText;

    private int amount;

    private double value;

    private String action;

    private LocalDateTime timeOfAction;
}
