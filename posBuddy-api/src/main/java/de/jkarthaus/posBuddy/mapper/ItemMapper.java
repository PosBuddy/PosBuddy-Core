package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface ItemMapper {

    ItemResponse toResponse(List<ItemEntity> itemEntityList);
}
