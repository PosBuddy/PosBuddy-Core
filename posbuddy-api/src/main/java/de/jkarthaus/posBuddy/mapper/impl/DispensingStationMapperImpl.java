package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.mapper.DispensingStationMapper;
import de.jkarthaus.posBuddy.model.gui.DispensingStationResponse;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class DispensingStationMapperImpl implements DispensingStationMapper {
    @Override
    public List<DispensingStationResponse> toResponse(List<DispensingStationEntity> dispensingStationEntities) {
        return dispensingStationEntities
                .stream()
                .map(dispensingStationEntity -> new DispensingStationResponse(
                                dispensingStationEntity.getId(),
                                dispensingStationEntity.getName(),
                                dispensingStationEntity.getLocation()
                        )
                ).collect(Collectors.toList());
    }
}
