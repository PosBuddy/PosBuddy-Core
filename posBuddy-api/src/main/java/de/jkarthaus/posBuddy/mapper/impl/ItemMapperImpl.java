package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.mapper.ItemMapper;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ItemMapperImpl implements ItemMapper {


    @Override
    public ItemResponse toResponse(List<ItemEntity> itemEntityList) {
        return new ItemResponse(
                itemEntityList.stream().map(itemEntity -> {
                            return new ItemResponse.Item(
                                    itemEntity.getId(),
                                    itemEntity.getUnit(),
                                    itemEntity.getMinAge(),
                                    new ItemResponse.DispensingStation(itemEntity.getDispensingStationId(), ""),
                                    itemEntity.getPrice()
                            );
                        }
                ).collect(Collectors.toList())
        );
    }
}
