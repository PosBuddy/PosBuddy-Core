package de.jkarthaus.posBuddy.tools;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class Tools {

    public static boolean isAgeUnderYouthProtection(LocalDate birthday) {
        if (birthday == null) {
            log.warn("cannot calculate age of posBuddyId:{} because birthday is null");
            return false;
        }
        return birthday.plusYears(16).isAfter(LocalDate.now());
    }

}
