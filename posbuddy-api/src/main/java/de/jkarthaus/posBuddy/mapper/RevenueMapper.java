package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface RevenueMapper {

    RevenueResponse toResponse(List<RevenueEntity> revenueEntities);
}
