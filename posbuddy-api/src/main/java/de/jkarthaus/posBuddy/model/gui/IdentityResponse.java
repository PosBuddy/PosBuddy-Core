package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class IdentityResponse {
    private String posBuddyId;

    private String surName;

    private String lastName;

    private boolean youthProtectionAct;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private Float balance;

}
