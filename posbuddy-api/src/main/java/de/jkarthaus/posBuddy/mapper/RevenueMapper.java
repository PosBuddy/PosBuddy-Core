package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface RevenueMapper {

    List<RevenueResponse> toResponse(List<RevenueEntity> revenueEntities);
}
