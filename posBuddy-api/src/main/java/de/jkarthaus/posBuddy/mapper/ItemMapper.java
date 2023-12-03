package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import io.micronaut.context.annotation.Mapper;
import jakarta.inject.Singleton;

@Singleton

public interface ItemMapper {

    @Mapper.Mapping(
            to = "id",
            from = "artikel"
    )

    @Mapper
    ItemResponse toResponse(ItemEntity itemEntity);
}
