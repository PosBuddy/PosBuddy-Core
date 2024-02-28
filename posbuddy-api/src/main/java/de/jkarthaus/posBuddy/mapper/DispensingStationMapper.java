package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.model.gui.DispensingStationResponse;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface DispensingStationMapper {

    List<DispensingStationResponse> toResponse(List<DispensingStationEntity> dispensingStationEntities);


}
