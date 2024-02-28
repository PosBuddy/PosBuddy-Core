package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ItemMapperImpl implements ItemMapper {


    @Override
    public List<ItemResponse> toResponse(List<ItemEntity> itemEntityList, List<DispensingStationEntity> dispensingStationEntities) {
        return itemEntityList.stream().map(itemEntity -> new ItemResponse(
                        itemEntity.getId(),
                        itemEntity.getUnit(),
                        itemEntity.getMinAge(),
                        new ItemResponse.DispensingStation(
                                itemEntity.getDispensingStationId(),
                                dispensingStationEntities
                                        .stream()
                                        .filter(dispensingStationEntity ->
                                                dispensingStationEntity
                                                        .getId()
                                                        .equals(itemEntity.getDispensingStationId())
                                        )
                                        .findAny()
                                        .get()
                                        .getName()),
                        itemEntity.getPrice()
                )
        ).collect(Collectors.toList());
    }
}
