package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import io.micronaut.transaction.annotation.ReadOnly;

import java.util.List;

public interface DispensingStationRepository {
    @ReadOnly
    List<DispensingStationEntity> getDispensingStations();
}
