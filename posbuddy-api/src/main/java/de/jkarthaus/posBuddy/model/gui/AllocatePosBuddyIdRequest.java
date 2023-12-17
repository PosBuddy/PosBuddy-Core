package de.jkarthaus.posBuddy.model.gui;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class AllocatePosBuddyIdRequest {
    private String posBuddyId;
    private String surname;
    private String lastname;
    private LocalDate birthday;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private Float balance;
}
